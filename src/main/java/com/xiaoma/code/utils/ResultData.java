package com.xiaoma.code.utils;

import com.xiaoma.code.enums.ResultEnum;
import com.xiaoma.code.exception.BizException;
import lombok.Builder;
import lombok.Data;

/**
 * @Author Administrator
 * @Date 2022/5/7 09:35:47
 */
@Data
public class ResultData<T> {

    private Integer code;

    private String message;

    private T data;

    public ResultData(ResultEnum resultEnum) {
        this.code = resultEnum.getCode();
        this.message = resultEnum.getMessage();
    }

    public ResultData(ResultEnum resultEnum, T data) {
        this.code = resultEnum.getCode();
        this.message = resultEnum.getMessage();
        this.data = data;
    }

    public ResultData(BizException bizException) {
        this.code = bizException.getCode();
        this.message = bizException.getMessage();
    }
}
