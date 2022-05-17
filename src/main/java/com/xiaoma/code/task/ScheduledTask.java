package com.xiaoma.code.task;

import com.xiaoma.code.constants.Constant;
import com.xiaoma.code.utils.FileUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * @Author Administrator
 * @Date 2022/5/9 14:31:45
 */
@Component
public class ScheduledTask {

    @Value("${file.root.path}")
    private String fileRootPath;

    @Value("${file.temp.time}")
    private Long deleteTempFileTime;

    @Scheduled(cron = "0 */30 * * * ?")
    public void removeFutureTask() {
        FileMergeThreadPool.removeTask();
    }

    @Scheduled(cron = "30 10 1 * * ?")
    public void clearTempFile() {
        File file = new File(fileRootPath);
        File[] files = file.listFiles();
        if (files == null) {
            return;
        }
        for (File tempFile : files) {
            if (tempFile.isFile()) {
                continue;
            }
            String tempFileName = tempFile.getName();
            if (!tempFileName.startsWith(Constant.TEMP_FILE_PREFIX_2)) {
                continue;
            }
            long createTimeStamp = Long.parseLong(tempFileName.split("-")[1]);
            long currentTimeMillis = System.currentTimeMillis();
            if ((currentTimeMillis - createTimeStamp) > deleteTempFileTime) {
                File absoluteFile = tempFile.getAbsoluteFile();
                FileUtil.deleteRecursionFile(absoluteFile);
            }
        }
    }

}
