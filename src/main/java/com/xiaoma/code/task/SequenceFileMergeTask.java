package com.xiaoma.code.task;

import com.xiaoma.code.entity.FileInfo;
import com.xiaoma.code.utils.FileUtil;

import java.io.*;


/**
 * @Author: Xiaoma
 * @Date: 2022/05/08 15:17
 * @Email: 2533144458@qq.com
 */
@SuppressWarnings("ResultOfMethodCallIgnored")
public class SequenceFileMergeTask extends AbstractFileMergeTask {

    public SequenceFileMergeTask(FileInfo fileInfo, String fileTempPath, String fileRootPath, File fileTemp, File[] files) {
        super(fileInfo, fileTempPath, fileRootPath, fileTemp, files);
    }

    @Override
    public Boolean call() throws Exception {
        String updatePath = FileUtil.updatePath(this.getFileInfo().getFilePath());
        File[] files = this.getFiles();
        FileUtil.sortTempFiles(files);
        FileInputStream first = new FileInputStream(files[0]);
        FileInputStream second = new FileInputStream(files[1]);
        SequenceInputStream inputStream = new SequenceInputStream(first, second);
        for (int i = 2; i < files.length; i++) {
            InputStream third = new FileInputStream(files[i]);
            inputStream = new SequenceInputStream(inputStream, third);
        }
        String finalPath = this.getFileRootPath() + updatePath + File.separator + this.getFileInfo().getFileName();
        FileOutputStream outputStream = new FileOutputStream(finalPath);
        try {
            byte[] bytes = new byte[8192];
            int len;
            while ((len = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, len);
                outputStream.flush();
            }
        } finally {
            inputStream.close();
            outputStream.close();
        }
        FileUtil.deleteFiles(files);
        return this.getFileTemp().delete();
    }
}
