package com.xiaoma.code.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author: Xiaoma
 * @Date: 2022/05/06 21:50
 * @Email: 2533144458@qq.com
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileInfo {

    private Integer fileId;

    private String fileMd5;

    private String filePath;

    private String fileName;

    private Long fileSize;

    private Date createTime;

    private Date updateTime;

    public FileInfo(String filePath, String fileName) {
        this.filePath = filePath;
        this.fileName = fileName;
    }
}
