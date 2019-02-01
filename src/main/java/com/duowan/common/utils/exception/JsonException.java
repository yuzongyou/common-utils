package com.duowan.common.utils.exception;


import com.yygame.common.exception.CodeException;

/**
 * @author Arvin
 */
public class JsonException extends CodeException {

    public JsonException() {
    }

    public JsonException(int code) {
        super(code);
    }

    public JsonException(String message) {
        super(message);
    }

    public JsonException(int code, String message) {
        super(code, message);
    }

    public JsonException(String message, Throwable cause) {
        super(message, cause);
    }

    public JsonException(int code, String message, Throwable cause) {
        super(code, message, cause);
    }

    public JsonException(Throwable cause) {
        super(cause);
    }

    public JsonException(int code, Throwable cause) {
        super(code, cause);
    }

    public JsonException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public JsonException(int code, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(code, message, cause, enableSuppression, writableStackTrace);
    }
}
