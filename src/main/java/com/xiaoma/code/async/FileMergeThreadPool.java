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

    /**
     * 分块合并任务
     * key：临时文件夹绝对路径
     * value：任务结果
     */
    private static final ConcurrentHashMap<String, Future<Boolean>> TASK_LIST_MAP = new ConcurrentHashMap<>();

    /**
     * 断点数量保存
     * key：临时文件夹绝对路径
     * value：文件夹中文件数量
     */
    private static final ConcurrentHashMap<String, Integer> TEMP_FILE_MAP = new ConcurrentHashMap<>();

    /**
     * 获取临时文件夹中文件数量
     *
     * @param fileDirectory 临时文件夹
     * @return 文件数量
     */
    public static Integer getTempFileCount(File fileDirectory) {
        String absolutePath = fileDirectory.getAbsolutePath();
        if (!TEMP_FILE_MAP.containsKey(absolutePath)) {
            File[] files = fileDirectory.listFiles();
            if (files != null) {
                TEMP_FILE_MAP.put(absolutePath, files.length);
            }
        }
        return TEMP_FILE_MAP.get(absolutePath);
    }

    /**
     * 获取执行结果
     *
     * @param fileTempPath 临时文件夹路径
     * @return 异步执行结果
     */
    public static synchronized Boolean getFuture(String fileTempPath) {
        removeTask();
        return TASK_LIST_MAP.containsKey(fileTempPath);
    }

    /**
     * 分块合并异步执行
     *
     * @param callable 任务
     */
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
