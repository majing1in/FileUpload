package com.xiaoma.code.task;

import com.xiaoma.code.entity.FileInfo;

import java.io.File;
import java.util.concurrent.Callable;

/**
 * @Author: Xiaoma
 * @Date: 2022/05/08 19:35
 * @Email: 2533144458@qq.com
 */
public abstract class AbstractFileMergeTask implements Callable<Boolean> {

    private FileInfo fileInfo;

    private String fileTempPath;

    private String fileRootPath;

    private File fileTemp;

    private File[] files;

    public AbstractFileMergeTask(FileInfo fileInfo, String fileTempPath, String fileRootPath, File fileTemp, File[] files) {
        this.fileInfo = fileInfo;
        this.fileTempPath = fileTempPath;
        this.fileRootPath = fileRootPath;
        this.fileTemp = fileTemp;
        this.files = files;
    }

    public FileInfo getFileInfo() {
        return fileInfo;
    }

    public void setFileInfo(FileInfo fileInfo) {
        this.fileInfo = fileInfo;
    }

    public String getFileTempPath() {
        return fileTempPath;
    }

    public void setFileTempPath(String fileTempPath) {
        this.fileTempPath = fileTempPath;
    }

    public String getFileRootPath() {
        return fileRootPath;
    }

    public void setFileRootPath(String fileRootPath) {
        this.fileRootPath = fileRootPath;
    }

    public File getFileTemp() {
        return fileTemp;
    }

    public void setFileTemp(File fileTemp) {
        this.fileTemp = fileTemp;
    }

    public File[] getFiles() {
        return files;
    }

    public void setFiles(File[] files) {
        this.files = files;
    }
}
