package com.duowan.common.utils.exception;


import com.yygame.common.exception.CodeException;

/**
 * @author Arvin
 */
public class SignFailException extends CodeException {

    public static final int ERROR_CODE = 401;

    public SignFailException() {
        super(ERROR_CODE, "签名错误");
    }

    public SignFailException(String message) {
        super(ERROR_CODE, "签名错误: " + message);
    }
}
