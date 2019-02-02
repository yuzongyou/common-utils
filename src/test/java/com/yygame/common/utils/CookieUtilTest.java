package com.yygame.common.utils;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * @author yzy
 */
public class CookieUtilTest {

    private HttpServletRequest request = Mockito.mock(HttpServletRequest.class);

    @Test
    public void getCookie() throws Exception {

        Cookie[] cookies = new Cookie[]{
                new Cookie("username", "yzy"),
                new Cookie("age", "26")
        };
        Mockito.when(request.getCookies()).thenReturn(cookies);

        Assert.assertEquals("yzy", CookieUtil.getCookie(request, "username"));
        assertEquals("26", CookieUtil.getCookie(request, "age"));
        assertEquals(null, CookieUtil.getCookie(request, "empty"));

    }

    @Test
    public void getCookieNameValueMap() {
        Cookie[] cookies = new Cookie[]{
                new Cookie("username", "yzy"),
                new Cookie("age", "26")
        };
        Mockito.when(request.getCookies()).thenReturn(cookies);

        Map<String, String> map = CookieUtil.getCookieNameValueMap(request);
        assertEquals(map.get("username"), "yzy");
        assertEquals(map.get("age"), "26");
    }

    @Test
    public void getCookieMap() {
        Cookie[] cookies = new Cookie[]{
                new Cookie("username", "yzy"),
                new Cookie("age", "26")
        };
        Mockito.when(request.getCookies()).thenReturn(cookies);
        Map<String, Cookie> map = CookieUtil.getCookieMap(request);

        assertEquals(map.get("username").getValue(), "yzy");
        assertEquals(map.get("age").getValue(), "26");
    }

    @Test
    public void addHttpOnlyCookie() {
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        ArgumentCaptor<Cookie> cookieArgumentCaptor = ArgumentCaptor.forClass(Cookie.class);

        CookieUtil.addHttpOnlyCookie("username", "yzy", -1, true, ".duowan.com", response);

        Mockito.verify(response).addCookie(cookieArgumentCaptor.capture());

        Cookie cookie = cookieArgumentCaptor.getValue();
        assertEquals(cookie.isHttpOnly(), true);
    }

    @Test
    public void addCookie() throws Exception {

        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        ArgumentCaptor<Cookie> cookieArgumentCaptor = ArgumentCaptor.forClass(Cookie.class);
        ArgumentCaptor<String> p3pHeaderNameArgumentCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> p3pHeaderValueArgumentCaptor = ArgumentCaptor.forClass(String.class);

        CookieUtil.addCookie("username", "yzy", -1, true, true, ".duowan.com", response);

        Mockito.verify(response).addCookie(cookieArgumentCaptor.capture());
        Mockito.verify(response).setHeader(p3pHeaderNameArgumentCaptor.capture(), p3pHeaderValueArgumentCaptor.capture());

        Cookie cookie = cookieArgumentCaptor.getValue();
        assertEquals("username", cookie.getName());
        assertEquals("yzy", cookie.getValue());
        assertEquals(-1, cookie.getMaxAge());
        assertEquals(true, cookie.isHttpOnly());
        assertEquals(".duowan.com", cookie.getDomain());

        assertEquals(CookieUtil.P3P_HERDER_NAME, p3pHeaderNameArgumentCaptor.getValue());
        assertEquals(CookieUtil.P3P_HERDER_VALUE, p3pHeaderValueArgumentCaptor.getValue());

    }

    @Test
    public void testGetTopDomain() throws Exception {

        Mockito.when(request.getServerName()).thenReturn("udblogin.duowan.com");

        String topDomain = CookieUtil.getTopDomain(request);

        assertEquals(".duowan.com", topDomain);

        Mockito.when(request.getServerName()).thenReturn("dev-udblogin.4366.com");

        topDomain = CookieUtil.getTopDomain(request);

        assertEquals(".4366.com", topDomain);

        assertEquals(".baidu.com", CookieUtil.getTopDomain(".baidu.com"));
        assertEquals(".baidu.com", CookieUtil.getTopDomain("www.baidu.com"));


    }
}