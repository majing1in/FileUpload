package com.xiaoma.code.utils;

import java.io.File;

/**
 * @Author: Xiaoma
 * @Date: 2022/05/07 23:00
 * @Email: 2533144458@qq.com
 */
public class FileUtil {

    public static String updatePath(String path) {
        return path.replace("/", File.separator);
    }

    public static Long getDirectoryFileLength(String directory) {
        File directoryFile = new File(directory);
        File[] files;
        if (!directoryFile.exists() || (files = directoryFile.listFiles()) == null) {
            return null;
        }
        long length = 0L;
        for (File file : files) {
            length = length + file.length();
        }
        return length;
    }
}
