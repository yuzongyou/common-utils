package com.duowan.common.utils;

/**
 * @author Arvin
 */
public abstract class PathUtil {

    /**
     * 合理化路径， 将 \\ 转成 /
     *
     * @param path 路径
     * @return 返回替换 \\ // 的地址
     */
    public static String normalizePath(String path) {
        if (null != path) {
            return path.replaceAll("[/\\\\]+", "/");
        }
        return null;
    }
}
