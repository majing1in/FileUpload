package com.xiaoma.code.service;

import com.xiaoma.code.entity.FileInfo;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @Author Administrator
 * @Date 2022/5/7 10:22:04
 */
public interface FileUploadService {

    Long isExistFile(String filePath, String fileName, Integer type);

    /**
     * 保存上传文件
     *
     * @param fileNumber 文件编号（分块）
     * @param fileInfo   文件信息（文件名、文件路径）
     * @param file       file文件
     * @param type       上传类型（0 普通上传 1 分片上传）
     * @return 当前file大小
     */
    Long saveUploadFile(Integer fileNumber, FileInfo fileInfo, MultipartFile file, Integer type) throws Exception;

    /**
     * 合并分块
     *
     * @param chunks   总块数
     * @param fileInfo 文件信息（文件名、文件路径）
     */
    void mergeBlockFile(Integer chunks, FileInfo fileInfo) throws IOException;
}
