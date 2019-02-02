package com.yygame.common.utils;

import com.duowan.common.utils.UrlUtil;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

/**
 * 测试 Url 工具类
 *
 * @author Arvin
 * @version 1.0
 * @since 2018/7/27 15:24
 */
public class UrlUtilTest {
    @Test
    public void adapterUrlProtocol() throws Exception {

        Assert.assertEquals("http://www.baidu.com", UrlUtil.adapterUrlProtocol("http", "http://www.baidu.com"));
        assertEquals("https://www.baidu.com", UrlUtil.adapterUrlProtocol("https", "http://www.baidu.com"));

        assertEquals("https://www.baidu.com", UrlUtil.adapterUrlProtocol("https", "https://www.baidu.com"));
        assertEquals("http://www.baidu.com", UrlUtil.adapterUrlProtocol("http", "https://www.baidu.com"));

        assertEquals("http%3A%2F%2Fwww.baidu.com", UrlUtil.adapterUrlProtocol("http", "http%3A%2F%2Fwww.baidu.com"));
        assertEquals("https%3A%2F%2Fwww.baidu.com", UrlUtil.adapterUrlProtocol("https", "http%3A%2F%2Fwww.baidu.com"));

        assertEquals("https%3A%2F%2Fwww.baidu.com", UrlUtil.adapterUrlProtocol("https", "https%3A%2F%2Fwww.baidu.com"));
        assertEquals("http%3A%2F%2Fwww.baidu.com", UrlUtil.adapterUrlProtocol("http", "https%3A%2F%2Fwww.baidu.com"));


    }

    @Test
    public void getParamValue() {

        String url = "http://www.baidu.com/?s=1&s1=2&s2=3";

        assertEquals("1", UrlUtil.getParamValue(url, "s"));
        assertEquals("2", UrlUtil.getParamValue(url, "s1"));
        assertEquals("3", UrlUtil.getParamValue(url, "s2"));

    }

    @Test
    public void extractParamsAsMap() {
        String url = "http://www.baidu.com/?s1=1&s2=2&s3=3&s4&s5=";

        Map<String, String> paramMap = UrlUtil.extractParamsAsMap(url);
        assertEquals(5, paramMap.size());
        assertEquals("1", paramMap.get("s1"));
        assertEquals("2", paramMap.get("s2"));
        assertEquals("3", paramMap.get("s3"));
        assertEquals(null, paramMap.get("s4"));
        assertEquals("", paramMap.get("s5"));

    }

}