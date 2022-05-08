package com.xiaoma.code.async;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.*;

/**
 * @Author: Xiaoma
 * @Date: 2022/05/08 15:32
 * @Email: 2533144458@qq.com
 */
public class FileMergeThreadPool {

    private static final ThreadPoolExecutor THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(2, 4, 10, TimeUnit.MINUTES,
            new ArrayBlockingQueue<>(8), Executors.defaultThreadFactory(), new RejectHandler());

    private static final ConcurrentHashMap<String, Future<Boolean>> TASK_LIST = new ConcurrentHashMap<>();

    public static synchronized Boolean getFuture(String fileTempPath) {
        removeTask();
        return TASK_LIST.containsKey(fileTempPath);
    }

    public static synchronized void execute(Callable<Boolean> callable) {
        AbstractFileMergeTask mergeTask = (AbstractFileMergeTask) callable;
        String fileTempPath = mergeTask.getFileTempPath();
        if (TASK_LIST.containsKey(fileTempPath)) {
            return;
        }
        Future<Boolean> future = THREAD_POOL_EXECUTOR.submit(mergeTask);
        TASK_LIST.put(fileTempPath, future);
    }

    private static void removeTask() {
        Iterator<Map.Entry<String, Future<Boolean>>> iterator = TASK_LIST.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Future<Boolean>> entry = iterator.next();
            Future<Boolean> future = entry.getValue();
            if (future.isDone()) {
                iterator.remove();
            }
        }
    }

    static class RejectHandler implements RejectedExecutionHandler {

        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {

        }
    }
}
