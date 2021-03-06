package com.xiaoma.code.constants;

/**
 * @Author Administrator
 * @Date 2022/5/7 16:45:57
 */
public class Constant {

    public static final Integer UPLOAD_TYPE_NORMAL = 0;

    public static final Integer UPLOAD_TYPE_BLOCK = 1;

    public static final String TEMP_FILE_PREFIX_2 = "TEMP";

    public static final String TEMP_FILE_PREFIX_1 = createTempFileName();

    private static String createTempFileName() {
        return TEMP_FILE_PREFIX_2 + "-" + System.currentTimeMillis() + "-";
    }

}
