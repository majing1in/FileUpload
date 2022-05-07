package com.xiaoma.code.service.impl;

import com.xiaoma.code.constants.Constant;
import com.xiaoma.code.dao.FileInfoDao;
import com.xiaoma.code.entity.FileInfo;
import com.xiaoma.code.exception.BizException;
import com.xiaoma.code.service.FileUploadService;
import com.xiaoma.code.utils.Assert;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

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

    @Resource
    private FileInfoDao fileInfoDao;

    public Long saveUploadFile(Integer fileNumber, FileInfo fileInfo, MultipartFile multipartFile, Integer type) throws Exception {
        StringBuilder filePath = new StringBuilder(fileRootPath)
                .append(fileInfo.getFilePath().replace("/", File.separator));
        File fileDirectory = new File(filePath.toString());
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        File file;
        try {
            if (!fileDirectory.exists() && !fileDirectory.mkdirs()) {
                throw new BizException(CREATE_DIRECTORY_EXCEPTION);
            }
            if (type == Constant.UPLOAD_TYPE_BLOCK) {
                filePath.append(File.separator).append("TEMP-").append(fileInfo.getFileName());
                fileDirectory = new File(filePath.toString());
                if (!fileDirectory.exists() && !fileDirectory.mkdir()) {
                    throw new BizException(CREATE_TEMP_DIRECTORY_EXCEPTION);
                }
                fileInfo.setFileName(fileNumber.toString());
            }
            String fileCompletePath = fileDirectory.getAbsolutePath() + File.separator + fileInfo.getFileName();
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

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public void mergeBlockFile(Integer chunks, FileInfo fileInfo) throws IOException {
        String fileTempPath = fileRootPath + fileInfo.getFilePath().replace("/", File.separator) + File.separator + "TEMP-" + fileInfo.getFileName() + File.separator;
        File fileTemp = new File(fileTempPath);
        File[] files = fileTemp.listFiles();
        if (files == null || chunks < files.length) {
            return;
        }
        List<String> fileNames = Arrays.stream(files)
                .map(File::getName).sorted(Comparator.comparingInt(Integer::parseInt)).collect(Collectors.toList());
        FileInputStream first = new FileInputStream(fileNames.get(0));
        FileInputStream second = new FileInputStream(fileNames.get(1));
        SequenceInputStream inputStream = new SequenceInputStream(first, second);
        for (int i = 2; i < fileNames.size(); i++) {
            InputStream third = new FileInputStream(fileNames.get(i));
            inputStream = new SequenceInputStream(inputStream, third);
        }
        String finalPath = fileRootPath + fileInfo.getFilePath().replace("/", File.separator) + File.separator + fileInfo.getFileName();
        FileOutputStream outputStream = new FileOutputStream(finalPath);
        try {
            byte[] bytes = new byte[8196];
            while (inputStream.read(bytes, 0, bytes.length) != -1) {
                outputStream.write(bytes);
                outputStream.flush();
            }
        } finally {
            inputStream.close();
            outputStream.close();
        }
        for (File file : files) {
            if (file.exists()) {
                file.delete();
            }
        }
        fileTemp.delete();
    }
}
