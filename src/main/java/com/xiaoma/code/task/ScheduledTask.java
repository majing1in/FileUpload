package com.xiaoma.code.task;

import com.xiaoma.code.constants.Constant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * @Author Administrator
 * @Date 2022/5/9 14:31:45
 */
@SuppressWarnings("ResultOfMethodCallIgnored")
@Component
public class ScheduledTask {

    @Value("${file.root.path}")
    private String fileRootPath;

    @Scheduled(cron = "0 */30 * * * ?")
    public void removeFutureTask() {
        FileMergeThreadPool.removeTask();
    }

    @Scheduled(cron = "30 10 1 * * ?")
    public void clearTempFile() {
        findAllTempFiles(new File(fileRootPath));
    }

    public void findAllTempFiles(File file) {
        File[] files = file.listFiles();
        for (File value : files) {
            if (!value.isDirectory()) {
                continue;
            }
            if (value.getName().startsWith(Constant.TEMP_FILE_PREFIX)) {
                File[] listFiles = value.listFiles();
                for (File tempFile : listFiles) {
                    if (tempFile.exists()) {
                        tempFile.delete();
                    }
                }
                value.delete();
                return;
            }
            findAllTempFiles(value);
        }
    }
}
