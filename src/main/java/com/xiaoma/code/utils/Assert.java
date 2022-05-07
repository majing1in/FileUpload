package com.xiaoma.code.utils;

import com.xiaoma.code.enums.BizEnum;
import com.xiaoma.code.exception.BizException;

/**
 * @Author Administrator
 * @Date 2022/5/7 10:33:28
 */
public class Assert {

    public static void isTrue(boolean expression, BizEnum bizEnum) throws BizException {
        if (!expression) {
            throw new BizException(bizEnum);
        }
    }

    public static void isFalse(boolean expression, BizEnum bizEnum) throws BizException {
        if (expression) {
            throw new BizException(bizEnum);
        }
    }
}
