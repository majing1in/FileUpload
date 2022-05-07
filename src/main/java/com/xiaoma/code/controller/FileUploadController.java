package com.xiaoma.code.controller;

import com.xiaoma.code.constants.Constant;
import com.xiaoma.code.entity.FileInfo;
import com.xiaoma.code.service.FileUploadService;
import com.xiaoma.code.utils.ResultData;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

import static com.xiaoma.code.enums.ResultEnum.SUCCESS;

/**
 * @Author Administrator
 * @Date 2022/5/7 09:32:11
 */
@RestController
@RequestMapping("/api/upload")
public class FileUploadController {

    @Resource
    private FileUploadService fileUploadService;

    @PostMapping("/normal")
    public ResultData<Long> normalFileUpload(@RequestParam("filePath") String filePath,
                                             @RequestParam("fileName") String fileName,
                                             @RequestParam("file") MultipartFile file) throws Exception {
        FileInfo fileInfo = new FileInfo(filePath, fileName);
        Long uploadSize = fileUploadService.saveUploadFile(-1, fileInfo, file, Constant.UPLOAD_TYPE_NORMAL);
        return new ResultData<>(SUCCESS, uploadSize);
    }

    @PostMapping("/block")
    public ResultData<Long> blockFileUpload(@RequestParam("chunks") String chunks,
                                            @RequestParam("chunk") String chunk,
                                            @RequestParam("filePath") String filePath,
                                            @RequestParam("fileName") String fileName,
                                            @RequestParam("file") MultipartFile file) throws Exception {
        FileInfo fileInfo = new FileInfo(filePath, fileName);
        Long uploadSize = fileUploadService.saveUploadFile(Integer.parseInt(chunk), fileInfo, file, Constant.UPLOAD_TYPE_BLOCK);
        fileUploadService.mergeBlockFile(Integer.parseInt(chunks), fileInfo);
        return new ResultData<>(SUCCESS, uploadSize);
    }
}
