package com.duowan.common.utils.exception;


import com.yygame.common.exception.CodeException;

/**
 * @author Arvin
 * @time 2018/4/10 21:06
 */
public class AssertFailException extends CodeException {

    public AssertFailException(String message) {
        super(400, message);
    }

    public AssertFailException(int code, String message) {
        super(code, message);
    }
}
