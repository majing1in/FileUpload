package com.xiaoma.code.async;

import java.io.File;
import java.util.HashMap;
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

    private static final ConcurrentHashMap<String, Future<Boolean>> TASK_LIST_MAP = new ConcurrentHashMap<>();

    private static final Map<String, Integer> TEMP_FILE_MAP = new HashMap<>();

    public static synchronized Integer getTempFileCount(File fileDirectory) {
        String absolutePath = fileDirectory.getAbsolutePath();
        if (!TEMP_FILE_MAP.containsKey(absolutePath)) {
            File[] files = fileDirectory.listFiles();
            if (files != null) {
                TEMP_FILE_MAP.put(absolutePath, files.length);
            }
        }
        return TEMP_FILE_MAP.get(absolutePath);
    }

    public static synchronized Boolean getFuture(String fileTempPath) {
        removeTask();
        return TASK_LIST_MAP.containsKey(fileTempPath);
    }

    public static synchronized void execute(Callable<Boolean> callable) {
        AbstractFileMergeTask mergeTask = (AbstractFileMergeTask) callable;
        String fileTempPath = mergeTask.getFileTempPath();
        if (TASK_LIST_MAP.containsKey(fileTempPath)) {
            return;
        }
        Future<Boolean> future = THREAD_POOL_EXECUTOR.submit(mergeTask);
        TASK_LIST_MAP.put(fileTempPath, future);
    }

    private static void removeTask() {
        Iterator<Map.Entry<String, Future<Boolean>>> iterator = TASK_LIST_MAP.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Future<Boolean>> entry = iterator.next();
            Future<Boolean> future = entry.getValue();
            if (future.isDone()) {
                TEMP_FILE_MAP.remove(entry.getKey());
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
