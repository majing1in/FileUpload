package com.xiaoma.code.service.impl;

import com.xiaoma.code.async.AbstractFileMergeTask;
import com.xiaoma.code.async.RandomFileMergeTask;
import com.xiaoma.code.async.SequenceFileMergeTask;
import com.xiaoma.code.async.FileMergeThreadPool;
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

    public Long saveUploadFile(Integer fileNumber, FileInfo fileInfo, MultipartFile multipartFile, Integer type) throws Exception {
        StringBuilder filePath = new StringBuilder(fileRootPath).append(FileUtil.updatePath(fileInfo.getFilePath()));
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        File file;
        try {
            File fileDirectory = new File(filePath.toString());
            String fileName = fileInfo.getFileName();
            if (!fileDirectory.exists() && !fileDirectory.mkdirs()) {
                throw new BizException(CREATE_DIRECTORY_EXCEPTION);
            }
            if (Objects.equals(type, Constant.UPLOAD_TYPE_BLOCK)) {
                filePath.append(File.separator).append(Constant.TEMP_FILE_PREFIX).append(fileName);
                fileDirectory = new File(filePath.toString());
                if (!fileDirectory.exists() && !fileDirectory.mkdir()) {
                    throw new BizException(CREATE_TEMP_DIRECTORY_EXCEPTION);
                }
                fileName = fileNumber.toString();
            }
            String fileCompletePath = fileDirectory.getAbsolutePath() + File.separator + fileName;
            file = new File(fileCompletePath);
            Assert.isTrue((!file.exists() || file.delete()), DELETE_DIRECTORY_EXCEPTION);
            Assert.isTrue(file.createNewFile(), CREATE_FILE_EXCEPTION);
        } finally {
            mainLock.unlock();
        }
        try (FileOutputStream outputStream = new FileOutputStream(file);
             InputStream inputStream = multipartFile.getInputStream()) {
            byte[] bytes = new byte[8196];
            while (inputStream.read(bytes, 0, bytes.length) != -1) {
                outputStream.write(bytes);
            }
        }
        return multipartFile.getSize();
    }

    @Override
    public void mergeBlockFile(Integer chunks, FileInfo fileInfo) {
        String updatePath = FileUtil.updatePath(fileInfo.getFilePath());
        String fileTempPath = fileRootPath + updatePath + File.separator +
                Constant.TEMP_FILE_PREFIX + fileInfo.getFileName() + File.separator;
        File fileTemp = new File(fileTempPath);
        File[] files = fileTemp.listFiles();
        if ((files == null || chunks > files.length - 1) || FileMergeThreadPool.getFuture(fileTempPath) || !fileTemp.exists()) {
            return;
        }
        AbstractFileMergeTask mergeTask = (files.length > 4) ?
                new RandomFileMergeTask(fileInfo, fileTempPath, fileRootPath, fileTemp, files)
                :new SequenceFileMergeTask(fileInfo, fileTempPath, fileRootPath, fileTemp, files);
        FileMergeThreadPool.execute(mergeTask);
    }
}
