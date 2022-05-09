package com.xiaoma.code.service.impl;

import com.xiaoma.code.task.AbstractFileMergeTask;
import com.xiaoma.code.task.FileMergeThreadPool;
import com.xiaoma.code.task.RandomFileMergeTask;
import com.xiaoma.code.task.SequenceFileMergeTask;
import com.xiaoma.code.constants.Constant;
import com.xiaoma.code.entity.FileInfo;
import com.xiaoma.code.exception.BizException;
import com.xiaoma.code.service.FileUploadService;
import com.xiaoma.code.utils.Assert;
import com.xiaoma.code.utils.FileUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;

import static com.xiaoma.code.enums.BizEnum.*;

/**
 * @Author Administrator
 * @Date 2022/5/7 10:23:04
 */
@Service
public class FileUploadServiceImpl implements FileUploadService {

    private final ReentrantLock mainLock = new ReentrantLock();

    @Value("${file.root.path}")
    private String fileRootPath;

    @Override
    public Long isExistFile(String path, String name, Integer type) {
        StringBuilder filePath = new StringBuilder(fileRootPath).append(FileUtil.updatePath(path));
        File fileDirectory = new File(filePath.toString());
        if (!fileDirectory.exists()) {
            return -1L;
        }
        if (Constant.UPLOAD_TYPE_BLOCK.equals(type)) {
            StringBuilder tempFileDirectory = filePath.append(File.separator).append(Constant.TEMP_FILE_PREFIX).append(name);
            return FileUtil.getDirectoryFileLength(tempFileDirectory.toString());
        }
        filePath.append(File.separator).append(name);
        File file = new File(filePath.toString());
        return file.length();
    }

    @Override
    public Long saveUploadFile(Integer fileNumber, FileInfo fileInfo, MultipartFile multipartFile, Integer type) throws Exception {
        StringBuilder filePath = new StringBuilder(fileRootPath).append(FileUtil.updatePath(fileInfo.getFilePath()));
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        File file;
        String fileDirectoryPath = null;
        try {
            File fileDirectory = new File(filePath.toString());
            String fileName = fileInfo.getFileName();
            // 目录不存在则先创建目录
            if (!fileDirectory.exists() && !fileDirectory.mkdirs()) {
                throw new BizException(CREATE_DIRECTORY_EXCEPTION);
            }
            // 分块上传处理
            if (Objects.equals(type, Constant.UPLOAD_TYPE_BLOCK)) {
                filePath.append(File.separator).append(Constant.TEMP_FILE_PREFIX).append(fileName);
                fileDirectory = new File(filePath.toString());
                // 创建分块临时目录
                if (!fileDirectory.exists() && !fileDirectory.mkdir()) {
                    throw new BizException(CREATE_TEMP_DIRECTORY_EXCEPTION);
                }
                fileDirectoryPath = fileDirectory.getAbsolutePath();
                // 临时目录存在文件，编号继续向后递增
                Integer size = FileMergeThreadPool.getTempFileCount(fileDirectory);
                fileNumber = size != null ? size + fileNumber : fileNumber;
                fileName = fileNumber.toString();
            }
            filePath.append(File.separator).append(fileName);
            file = new File(filePath.toString());
            Assert.isTrue(!file.exists(), HAS_SAME_FILE_EXCEPTION);
            // 创建文件
            Assert.isTrue(file.createNewFile(), CREATE_FILE_EXCEPTION);
        } finally {
            mainLock.unlock();
        }
        // 写入数据
        try (FileOutputStream outputStream = new FileOutputStream(file); InputStream inputStream = multipartFile.getInputStream()) {
            FileMergeThreadPool.addFileStream(fileDirectoryPath);
            byte[] bytes = new byte[1024];
            int len;
            while ((len = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, len);
            }
        } finally {
            FileMergeThreadPool.deleteFileStream(fileDirectoryPath);
        }
        return multipartFile.getSize();
    }

    @Override
    public void mergeBlockFile(Integer chunks, FileInfo fileInfo) {
        String updatePath = FileUtil.updatePath(fileInfo.getFilePath());
        String fileTempPath = fileRootPath + updatePath + File.separator + Constant.TEMP_FILE_PREFIX + fileInfo.getFileName();
        File fileTemp = new File(fileTempPath);
        File[] files = fileTemp.listFiles();
        // 当文件块数达到目标数量才会获取结果，如果任务正确执行会删除文件
        if ((files == null || chunks > files.length - 1) || FileMergeThreadPool.getFuture(fileTempPath) || !fileTemp.exists()) {
            return;
        }
        // 异步执行两种合并方式
        AbstractFileMergeTask mergeTask = (files.length > 4) ?
                new RandomFileMergeTask(fileInfo, fileTempPath, fileRootPath, fileTemp, files)
                : new SequenceFileMergeTask(fileInfo, fileTempPath, fileRootPath, fileTemp, files);
        FileMergeThreadPool.execute(mergeTask);
    }
}
