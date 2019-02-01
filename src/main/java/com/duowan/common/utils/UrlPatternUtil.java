package com.duowan.common.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.Set;

/**
 * Url 匹配工具类
 *
 * @author Arvin
 */
public abstract class UrlPatternUtil {

    /**
     * 是否匹配， 支持通配符 * 和 正则表达式 匹配
     *
     * @param uri      要检查的 uri
     * @param patterns 匹配项目
     * @return 如果匹配项目中有一个匹配都会返回成功
     */
    public static boolean isMatch(String uri, Set<String> patterns) {

        if (patterns == null || patterns.isEmpty()) {
            return false;
        }

        if (StringUtils.isBlank(uri)) {
            return false;
        }

        for (String pattern : patterns) {
            if (uri.equals(pattern)) {
                return true;
            }
            if ("*".equals(pattern)) {
                return true;
            }
            if (pattern.contains("*")) {
                String regex = "^" + pattern.replaceAll("\\*", "[^\\*]*").replaceAll("\\.", "\\\\.") + "$";
                if (uri.matches(regex)) {
                    return true;
                }
            }
            try {
                if (uri.matches(pattern)) {
                    return true;
                }
            } catch (Exception ignored) {
            }
        }
        return false;
    }
}
