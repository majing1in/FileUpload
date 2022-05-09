package com.xiaoma.code.task;

import com.xiaoma.code.entity.FileInfo;
import com.xiaoma.code.utils.FileUtil;

import java.io.File;
import java.io.RandomAccessFile;

/**
 * @Author: Xiaoma
 * @Date: 2022/05/08 18:38
 * @Email: 2533144458@qq.com
 */
@SuppressWarnings("ResultOfMethodCallIgnored")
public class RandomFileMergeTask extends AbstractFileMergeTask {

    public RandomFileMergeTask(FileInfo fileInfo, String fileTempPath, String fileRootPath, File fileTemp, File[] files) {
        super(fileInfo, fileTempPath, fileRootPath, fileTemp, files);
    }

    @Override
    public Boolean call() throws Exception {
        long startTime = System.currentTimeMillis();
        String updatePath = FileUtil.updatePath(this.getFileInfo().getFilePath());
        File[] files = this.getFiles();
        for (int i = 0; i < files.length - 1; i++) {
            int i1 = Integer.parseInt(files[i].getName());
            for (int j = i + 1; j < files.length; j++) {
                int i2 = Integer.parseInt(files[j].getName());
                if (i1 > i2) {
                    File temp = files[i];
                    files[i] = files[j];
                    files[j] = temp;
                }
            }
        }
        String finalPath = this.getFileRootPath() + updatePath + File.separator + this.getFileInfo().getFileName();
        File finalFile = new File(finalPath);
        finalFile.createNewFile();
        RandomAccessFile finalRandomAccessFile = new RandomAccessFile(finalFile, "rw");
        byte[] bytes = new byte[1024];
        for (File file : files) {
            RandomAccessFile tempRandomAccessFile = new RandomAccessFile(file, "r");
            int len;
            while ((len = tempRandomAccessFile.read(bytes)) != -1) {
                finalRandomAccessFile.write(bytes, 0, len);
            }
            tempRandomAccessFile.close();
        }
        finalRandomAccessFile.close();
        for (File file : files) {
            if (file.exists()) {
                file.delete();
            }
        }
        boolean result = this.getFileTemp().delete();
        System.out.println(System.currentTimeMillis() - startTime);
        return result;
    }
}
