package com.xiaoma.code.task;

import com.xiaoma.code.entity.BlockFileTask;

import java.io.File;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.*;

/**
 * @Author: Xiaoma
 * @Date: 2022/05/08 15:32
 * @Email: 2533144458@qq.com
 */
public class FileMergeThreadPool {

    private static final Object LOCK_REMOVE_TASK = new Object();

    private static final ConcurrentHashMap<String, BlockFileTask> FILE_TASK_MAP = new ConcurrentHashMap<>(256);

    private static final ThreadPoolExecutor THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(2, 4, 10, TimeUnit.MINUTES,
            new ArrayBlockingQueue<>(8), Executors.defaultThreadFactory(), new RejectHandler());

    public static void addFileStream(String fileDirectory) {
        BlockFileTask blockFileTask;
        if (fileDirectory != null && (blockFileTask = FILE_TASK_MAP.get(fileDirectory)) != null) {
            blockFileTask.getFileSteams().incrementAndGet();
        }
    }

    public static void deleteFileStream(String fileDirectory) {
        BlockFileTask blockFileTask;
        if (fileDirectory != null && (blockFileTask = FILE_TASK_MAP.get(fileDirectory)) != null) {
            blockFileTask.getFileSteams().decrementAndGet();
        }
    }

    /**
     * 获取临时文件夹中文件数量
     *
     * @param fileDirectory 临时文件夹
     * @return 文件数量
     */
    public static Integer getTempFileCount(File fileDirectory) {
        String absolutePath = fileDirectory.getAbsolutePath();
        BlockFileTask blockFileTask;
        synchronized (LOCK_REMOVE_TASK) {
            if ((blockFileTask = FILE_TASK_MAP.get(absolutePath)) == null) {
                File[] files = fileDirectory.listFiles();
                blockFileTask = new BlockFileTask();
                blockFileTask.setCreateTime(new Date());
                if (files != null) {
                    blockFileTask.setTempFileCount(files.length);
                }
                FILE_TASK_MAP.put(absolutePath, blockFileTask);
            }
        }
        return blockFileTask.getTempFileCount();
    }

    /**
     * 获取执行结果
     *
     * @param fileTempPath 临时文件夹路径
     * @return 异步执行结果
     */
    public static Boolean getFuture(String fileTempPath) {
        removeTask();
        return !FILE_TASK_MAP.containsKey(fileTempPath);
    }

    /**
     * 分块合并异步执行
     *
     * @param callable 任务
     */
    public static void execute(Callable<Boolean> callable) {
        AbstractFileMergeTask mergeTask = (AbstractFileMergeTask) callable;
        String fileTempPath = mergeTask.getFileTempPath();
        BlockFileTask blockFileTask = FILE_TASK_MAP.get(fileTempPath);
        if (blockFileTask.getTaskFuture() != null && blockFileTask.getFileSteams().get() > 0) {
            return;
        }
        Future<Boolean> future = THREAD_POOL_EXECUTOR.submit(mergeTask);
        blockFileTask.setTaskFuture(future);
    }

    public static void removeTask() {
        synchronized (LOCK_REMOVE_TASK) {
            Iterator<Map.Entry<String, BlockFileTask>> iterator = FILE_TASK_MAP.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, BlockFileTask> entry = iterator.next();
                BlockFileTask blockFileTask = entry.getValue();
                if (blockFileTask != null) {
                    Future<Boolean> future = blockFileTask.getTaskFuture();
                    if (future != null && future.isDone()) {
                        iterator.remove();
                    }
                }
            }
        }
    }

    static class RejectHandler implements RejectedExecutionHandler {

        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {

        }
    }
}
