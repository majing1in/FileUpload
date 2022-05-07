package com.xiaoma.code.enums;

/**
 * @Author Administrator
 * @Date 2022/5/7 09:53:26
 */
public enum BizEnum {
    ROOT_PATH_EXCEPTION(10000, "文件根目录创建失败"),
    HAS_SAME_FILE_EXCEPTION(10001, "该目录下已存在相同文件"),
    CREATE_FILE_EXCEPTION(10002, "创建文件失败"),
    CREATE_DIRECTORY_EXCEPTION(10003, "创建目录失败"),
    CREATE_TEMP_DIRECTORY_EXCEPTION(10003, "创建临时目录失败"),
    DELETE_DIRECTORY_EXCEPTION(10003, "删除文件失败");

    private Integer code;
    private String message;

    BizEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
