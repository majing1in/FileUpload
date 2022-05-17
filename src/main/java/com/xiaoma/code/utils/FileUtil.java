package com.xiaoma.code.utils;

import com.xiaoma.code.constants.Constant;

import java.io.File;

/**
 * @Author: Xiaoma
 * @Date: 2022/05/07 23:00
 * @Email: 2533144458@qq.com
 */
@SuppressWarnings("ResultOfMethodCallIgnored")
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

    public static void sortTempFiles(File[] files) {
        for (int i = 0; i < files.length - 1; i++) {
            int i1 = Integer.parseInt(files[i].getName());
            for (int j = i + 1; j < files.length; j++) {
                int i2 = Integer.parseInt(files[j].getName());
                if (i1 > i2) {
                    File temp = files[i];
                    files[i] = files[j];
                    files[j] = temp;
                }
            }
        }
    }

    public static void deleteFiles(File[] files) {
        for (File file : files) {
            if (file.exists()) {
                file.delete();
            }
        }
    }

    public static void deleteRecursionFile(File file) {
        File[] files = file.listFiles();
        if (files == null) {
            return;
        }
        for (File value : files) {
            if (!value.isDirectory()) {
                continue;
            }
            if (value.getName().startsWith(Constant.TEMP_FILE_PREFIX_1)) {
                File[] listFiles = value.listFiles();
                if (listFiles == null) {
                    return;
                }
                for (File tempFile : listFiles) {
                    if (tempFile.exists()) {
                        tempFile.delete();
                    }
                }
                value.delete();
                return;
            }
            deleteRecursionFile(value);
        }
    }
}
