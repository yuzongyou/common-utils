package com.yygame.common.utils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * Cookie 操作工具类
 *
 * @author yzy
 */
public class CookieUtil {

    public static final String P3P_HERDER_NAME = "P3P";
    public static final String P3P_HERDER_VALUE = "CP=\"CURa ADMa DEVa PSAo PSDo OUR BUS UNI PUR INT DEM STA PRE COM NAV OTC NOI DSP COR\"";

    /**
     * 永不过期
     **/
    public static int MAX_AGE_FOREVER = -1;

    /**
     * 一天过期
     **/
    public static int MAX_AGE_ONE_DAY = 24 * 60 * 60;

    /**
     * 一周过期
     **/
    public static int MAX_AGE_ONE_WEEK = 7 * 24 * 60 * 60;

    private CookieUtil() {
    }

    /**
     * 获取 Cookie 键值对
     *
     * @param request 当前请求
     * @return 返回Cookie name -> value Map
     */
    public static Map<String, String> getCookieNameValueMap(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (null == cookies) {
            return new HashMap<>(0);
        }

        Map<String, String> map = new HashMap<>(cookies.length);
        for (Cookie cookie : cookies) {
            map.put(cookie.getName(), cookie.getValue());
        }
        return map;
    }

    /**
     * 获取 Cookie 名称 --> Cookie 对象 MAP
     *
     * @param request 当前请求
     * @return 返回Cookie name -> Cookie Map
     */
    public static Map<String, Cookie> getCookieMap(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (null == cookies) {
            return new HashMap<>(0);
        }

        Map<String, Cookie> map = new HashMap<>(cookies.length);
        for (Cookie cookie : cookies) {
            map.put(cookie.getName(), cookie);
        }
        return map;
    }


    /**
     * 获取指定的 Cookie 值
     *
     * @param request      当前请求
     * @param name         Cookie 名称
     * @param defaultValue 默认值，如果 Cookie 是 null 就返回默认值
     * @return 返回 Cookie 值
     */
    public static String getCookie(HttpServletRequest request, String name, String defaultValue) {
        Cookie[] cookies = request.getCookies();
        if (null == cookies) {
            return defaultValue;
        }
        for (Cookie cookie : cookies) {
            if (name.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return defaultValue;
    }

    /**
     * 获取指定的 Cookie 值
     *
     * @param request 当前请求
     * @param name    Cookie 名称
     * @return 返回 Cookie 值
     */
    public static String getCookie(HttpServletRequest request, String name) {
        return getCookie(request, name, null);
    }

    /**
     * 获取顶级域名， 如 udblogin.duowan.com --> .duowan.com
     *
     * @param request 当前请求
     * @return 返回顶级域名
     */
    public static String getTopDomain(HttpServletRequest request) {
        return getTopDomain(request.getServerName());
    }

    /**
     * 获取指定完整域名的顶级域
     *
     * @param domain 完整域名
     * @return 返回顶级域名，如 login.yy.com --> .yy.com
     */
    public static String getTopDomain(String domain) {
        return domain.replaceAll("(?i)^.*\\.([^.]+)\\.([^.]+)$", ".$1.$2");
    }

    /**
     * 添加 Cookie 到指定的域名， 默认是
     * maxAge = -1  即不过期
     * httpOnly = false 即允许前端 document.cookie 获取Cookie
     * appendP3PHeader = true 默认添加 P3P Header
     *
     * @param name     Cookie 名称
     * @param value    Cookie 值
     * @param request  写入到当前域名
     * @param response 请求响应
     */
    public static void addCookie(String name, String value, HttpServletRequest request, HttpServletResponse response) {
        addCookie(name, value, MAX_AGE_FOREVER, false, true, request.getServerName(), response);
    }

    /**
     * 添加 Cookie 到指定的域名， 默认是
     * maxAge = -1  即不过期
     * httpOnly = false 即允许前端 document.cookie 获取Cookie
     * appendP3PHeader = true 默认添加 P3P Header
     *
     * @param name     Cookie 名称
     * @param value    Cookie 值
     * @param domain   要写 Cookie 的域名
     * @param response 请求响应
     */
    public static void addCookie(String name, String value, String domain, HttpServletResponse response) {
        addCookie(name, value, MAX_AGE_FOREVER, false, true, domain, response);
    }

    /**
     * 添加 Cookie 到指定的 域名， httpOnly 默认是 true
     *
     * @param name            cookie 的名称
     * @param value           cookie 值
     * @param maxAge          缓存时间， 单位是秒， -1 表示不过期
     * @param appendP3PHeader 是否增加 P3P 头， 主要解决iframe中跨域写失败的问题，建议加上
     * @param request         当前请求，会把 Cookie 写到当前请求中去
     * @param response        当前响应
     */
    public static void addHttpOnlyCookie(String name, String value, int maxAge, boolean appendP3PHeader, HttpServletRequest request, HttpServletResponse response) {
        addCookie(name, value, maxAge, true, appendP3PHeader, request.getServerName(), response);
    }

    /**
     * 添加 Cookie 到指定的 域名， httpOnly 默认是 true
     *
     * @param name            cookie 的名称
     * @param value           cookie 值
     * @param maxAge          缓存时间， 单位是秒， -1 表示不过期
     * @param appendP3PHeader 是否增加 P3P 头， 主要解决iframe中跨域写失败的问题，建议加上
     * @param domain          要写该 cookie 到什么域名上
     * @param response        当前响应
     */
    public static void addHttpOnlyCookie(String name, String value, int maxAge, boolean appendP3PHeader, String domain, HttpServletResponse response) {
        addCookie(name, value, maxAge, true, appendP3PHeader, domain, response);
    }

    /**
     * 添加 Cookie 到指定的 域名
     *
     * @param name            cookie 的名称
     * @param value           cookie 值
     * @param maxAge          缓存时间， 单位是秒， -1 表示不过期
     * @param httpOnly        是否 httpOnly，默认是false，若设置为 true 则客户端无法使用 document.cookie 获取到指定的 Cookie
     * @param appendP3PHeader 是否增加 P3P 头， 主要解决iframe中跨域写失败的问题，建议加上
     * @param request         当前请求
     * @param response        当前响应
     */
    public static void addCookie(String name, String value, int maxAge, boolean httpOnly, boolean appendP3PHeader, HttpServletRequest request, HttpServletResponse response) {
        addCookie(name, value, maxAge, httpOnly, appendP3PHeader, request.getServerName(), response);
    }

    /**
     * 添加 Cookie 到指定的 域名
     *
     * @param name            cookie 的名称
     * @param value           cookie 值
     * @param maxAge          缓存时间， 单位是秒， -1 表示不过期
     * @param httpOnly        是否 httpOnly，默认是false，若设置为 true 则客户端无法使用 document.cookie 获取到指定的 Cookie
     * @param appendP3PHeader 是否增加 P3P 头， 主要解决iframe中跨域写失败的问题，建议加上
     * @param domain          要写该 cookie 到什么域名上
     * @param response        当前响应
     */
    public static void addCookie(String name, String value, int maxAge, boolean httpOnly, boolean appendP3PHeader, String domain, HttpServletResponse response) {
        AssertUtil.assertNotBlank(name, "cookie名称不能为空");
        AssertUtil.assertNotNull(value, "cookie值不能为空");

        Cookie cookie = new Cookie(name, value);
        cookie.setDomain(domain);
        cookie.setMaxAge(maxAge);
        cookie.setPath("/");

        if (httpOnly) {
            cookie.setHttpOnly(true);
        }

        if (appendP3PHeader) {
            response.setHeader(P3P_HERDER_NAME, P3P_HERDER_VALUE);
        }
        response.addCookie(cookie);
    }
}
