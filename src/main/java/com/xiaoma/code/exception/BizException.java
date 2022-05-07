package com.xiaoma.code.exception;

import com.xiaoma.code.enums.BizEnum;

/**
 * @Author: Xiaoma
 * @Date: 2022/05/06 21:39
 * @Email: 2533144458@qq.com
 */
public class BizException extends Exception {

    private Integer code;

    public BizException() {
        super();
    }

    public BizException(String message) {
        super(message);
    }

    public BizException(String message, Throwable cause) {
        super(message, cause);
    }

    public BizException(Throwable cause) {
        super(cause);
    }

    public BizException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public BizException(BizEnum bizEnum) {
        super(bizEnum.getMessage());
        this.code = bizEnum.getCode();
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
