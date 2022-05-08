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
}
