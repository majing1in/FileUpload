package com.xiaoma.code.dao;

import com.xiaoma.code.entity.FileInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Author: Xiaoma
 * @Date: 2022/05/06 22:00
 * @Email: 2533144458@qq.com
 */
@Mapper
public interface FileInfoDao {

    void addFileInfo(FileInfo fileInfo);

    void deleteFileInfo(@Param("filePath") String filePath, @Param("fileName") String fileName);
}
