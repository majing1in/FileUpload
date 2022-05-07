package com.xiaoma.code.service;

import com.xiaoma.code.entity.FileInfo;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @Author Administrator
 * @Date 2022/5/7 10:22:04
 */
public interface FileUploadService {

    Long saveUploadFile(Integer fileNumber, FileInfo fileInfo, MultipartFile file, Integer type) throws Exception;

    void mergeBlockFile(Integer chunks, FileInfo fileInfo) throws IOException;
}
