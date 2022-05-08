package com.xiaoma.code.enums;

/**
 * @Author Administrator
 * @Date 2022/5/7 13:44:05
 */
public enum ResultEnum {
    SUCCESS(200, "操作成功"),
    SAME_FILE(201, "该目录下已存在相同的文件"),
    FAIL(400, "操作成功")
    ;
    private Integer code;
    private String message;

    ResultEnum(Integer code, String message) {
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
