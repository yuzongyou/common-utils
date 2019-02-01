package com.duowan.common.utils;

import com.duowan.common.utils.exception.SignFailException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Arvin
 */
public class SignCheckUtil {

    private static final Logger logger = LoggerFactory.getLogger(SignCheckUtil.class);

    /**
     * 不区分大小写检查 MD5 加密字符串是否一致， 检查 actual == MD5(rawString)
     *
     * @param actual    实际MD5 值
     * @param rawString 要加密成MD5 比较的字符串
     */
    public static void checkMd5SignIgnoreCase(String actual, String rawString) {

        if (CommonUtil.isAnyBlank(actual, rawString)) {
            throw new SignFailException("参数为空");
        }

        String expect = EncryptUtil.toMd5UpperCase(rawString);

        if (!actual.equalsIgnoreCase(expect)) {
            throw new SignFailException();
        }
    }

    /**
     * 按照大写检查 MD5 加密字符串是否一致， 检查 actual == MD5(rawString)
     *
     * @param actual    实际MD5 值
     * @param rawString 要加密成MD5 比较的字符串
     */
    public static void checkMd5SignUpperCase(String actual, String rawString) {

        if (CommonUtil.isAnyBlank(actual, rawString)) {
            throw new SignFailException("参数为空");
        }

        String expect = EncryptUtil.toMd5UpperCase(rawString);

        if (!actual.equals(expect)) {
            throw new SignFailException();
        }
    }

    /**
     * 按照小写检查 MD5 加密字符串是否一致， 检查 actual == MD5(rawString)
     *
     * @param actual    实际MD5 值
     * @param rawString 要加密成MD5 比较的字符串
     */
    public static void checkMd5SignLowerCase(String actual, String rawString) {

        if (CommonUtil.isAnyBlank(actual, rawString)) {
            throw new SignFailException("参数为空");
        }

        String expect = EncryptUtil.toMd5LowerCase(rawString);

        if (!actual.equals(expect)) {
            throw new SignFailException();
        }
    }
}
