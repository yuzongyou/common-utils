package com.duowan.common.utils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @author Arvin
 * @version 1.0
 * @since 2018/8/28 14:31
 */
public class SessionUtil {

    public static <T> T get(HttpServletRequest request, String key, Class<T> requireType) {
        try {
            HttpSession session = request.getSession();

            Object obj = session.getAttribute(key);

            if (null == obj) {
                return null;
            }

            return requireType.cast(obj);

        } catch (Exception ignored) {
            return null;
        }
    }

    public static void set(HttpServletRequest request, String key, Object value) {
        try {
            HttpSession session = request.getSession();

            session.setAttribute(key, value);
        } catch (Exception ignored) {
        }
    }
}
