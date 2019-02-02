package com.yygame.common.utils;

import com.yygame.common.utils.exception.AssertFailException;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;

/**
 * @author yzy
 * @time 2017/12/3 20:16
 */
public abstract class AssertUtil {

    /**
     * 默认的异常代码
     */
    public static final int DEFAULT_ERROR_CODE = 400;

    public static void assertNotBlank(String value, int errorCode, String message) {
        if (StringUtils.isBlank(value)) {
            throw new AssertFailException(errorCode, message);
        }
    }

    public static void assertNotBlank(String value, String message) {
        assertNotBlank(value, DEFAULT_ERROR_CODE, message);
    }

    public static void assertNotNull(Object value, int errorCode, String message) {
        if (null == value) {
            throw new AssertFailException(errorCode, message);
        }
    }

    public static void assertNotNull(Object value, String message) {
        assertNotNull(value, DEFAULT_ERROR_CODE, message);
    }

    public static void assertNull(Object value, int errorCode, String message) {
        if (null != value) {
            throw new AssertFailException(errorCode, message);
        }
    }

    public static void assertNull(Object value, String message) {
        assertNull(value, DEFAULT_ERROR_CODE, message);
    }

    public static void assertEmpty(Collection<?> collection, String message) {
        assertEmpty(collection, DEFAULT_ERROR_CODE, message);
    }

    public static void assertEmpty(Collection<?> collection, int errorCode, String message) {
        if (collection != null && !collection.isEmpty()) {
            throw new AssertFailException(errorCode, message);
        }
    }

    public static void assertNotEmpty(Collection<?> collection, int errorCode, String message) {
        if (collection == null || collection.isEmpty()) {
            throw new AssertFailException(errorCode, message);
        }
    }

    public static void assertNotEmpty(Collection<?> collection, String message) {
        assertNotEmpty(collection, DEFAULT_ERROR_CODE, message);
    }

    public static void assertTrue(boolean bool, int errorCode, String message) {
        if (!bool) {
            throw new AssertFailException(errorCode, message);
        }
    }

    public static void assertTrue(boolean bool, String message) {
        assertTrue(bool, DEFAULT_ERROR_CODE, message);
    }

    public static void assertFalse(boolean bool, int errorCode, String message) {
        if (bool) {
            throw new AssertFailException(errorCode, message);
        }
    }

    public static void assertFalse(boolean bool, String message) {
        assertFalse(bool, DEFAULT_ERROR_CODE, message);
    }

    public static void assertPageParam(int pageNo, int pageSize, int errorCode) {
        if (pageNo < 1 || pageSize < 1) {
            throw new AssertFailException(errorCode, "分页参数不正确[pageNo=" + pageNo + "], [pageSize=" + pageSize + "]");
        }
    }

    public static void assertPageParam(int pageNo, int pageSize) {
        assertPageParam(pageNo, pageSize, DEFAULT_ERROR_CODE);
    }

    public static void assertClassExists(String className) {

        try {
            Class<?> clazz = Class.forName(className);
            if (null == clazz) {
                throw new AssertFailException("类[" + className + "]不存在");
            }
        } catch (ClassNotFoundException e) {
            throw new AssertFailException("类[" + className + "]不存在");
        }
    }

    /**
     * 查询指定的值是否小于指定值，使用默认错误代码 DEFAULT_ERROR_CODE
     *
     * @param value   要检查的值
     * @param barrier 边界值
     */
    public static void assertLess(Number value, Number barrier) {
        assertLess(value, barrier, DEFAULT_ERROR_CODE);
    }

    /**
     * 查询指定的值是否小于指定值
     *
     * @param value     要检查的值
     * @param barrier   边界值
     * @param errorCode 错误代码
     */
    public static void assertLess(Number value, Number barrier, int errorCode) {
        if (null == value || barrier == null) {
            throw new AssertFailException(errorCode, "校验值为null");
        }
        if (value.doubleValue() >= barrier.doubleValue()) {
            throw new AssertFailException(errorCode, "[" + value + "] 不能大于[" + barrier + "]");
        }
    }

    /**
     * 查询指定的值是否小于指定值，使用默认错误代码 DEFAULT_ERROR_CODE
     *
     * @param value   要检查的值
     * @param barrier 边界值
     */
    public static void assertOver(Number value, Number barrier) {
        assertOver(value, barrier, DEFAULT_ERROR_CODE);
    }

    /**
     * 查询指定的值是否小于指定值
     *
     * @param value     要检查的值
     * @param barrier   边界值
     * @param errorCode 错误代码
     */
    public static void assertOver(Number value, Number barrier, int errorCode) {
        if (null == value || barrier == null) {
            throw new AssertFailException(errorCode, "校验值为null");
        }
        if (value.doubleValue() <= barrier.doubleValue()) {
            throw new AssertFailException(errorCode, "[" + value + "] 不能小于[" + barrier + "]");
        }
    }

    /**
     * 检查指定的值是否在给定的范围内
     *
     * @param value 要比较的值
     * @param min   最小值
     * @param max   最大值
     */
    public static void assertRange(Number value, Number min, Number max) {
        assertRange(value, min, max, DEFAULT_ERROR_CODE);
    }

    /**
     * 检查指定的值是否在给定的范围内
     *
     * @param min       最小值
     * @param max       最大值
     * @param value     要比较的值
     * @param errorCode 错误代码
     */
    public static void assertRange(Number value, Number min, Number max, int errorCode) {
        if (null == min && max == null) {
            return;
        }
        if (value == null) {
            throw new AssertFailException(errorCode, "校验值为null，不在范围[" + min + ", " + max + "] 内");
        }

        if (min != null && value.doubleValue() < min.doubleValue()) {
            throw new AssertFailException(errorCode, "[" + value + "] 不在范围[" + min + ", " + max + "] 内");
        }
        if (max != null && value.doubleValue() > max.doubleValue()) {
            throw new AssertFailException(errorCode, "[" + value + "] 不在范围[" + min + ", " + max + "] 内");
        }

    }
}
