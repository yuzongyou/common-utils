package com.yygame.common.utils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <per>
 * 提供最原始的 HTTP 使用方式
 * 1. 使用短连接，因此每次发起HTTP请求都会新建TCP连接和关闭连接，没有连接池，可以尝试使用 HttpClient, OkHttp 之类的框架，实现了连接池
 * 2. 支持开发环境设置 Host，适合在不同的环境下，采用不同host的方案，特别是在开发和测试环境中这个还是很方便的，不用开发者去配置各种各样的host
 * </per>
 *
 * @author yzy
 * @time 2018/1/23 20:39
 */
public class HttpUtil {

    private static final Logger logger = LoggerFactory.getLogger(HttpUtil.class);

    /**
     * 默认超时时间
     */
    public static final int DEFAULT_CONNECTION_TIMEOUT = 3000;
    /**
     * 默认读超时时间
     */
    public static final int DEFAULT_READ_TIMEOUT = 3000;

    /**
     * get 请求
     *
     * @param url      请求地址，可以含参数
     * @param paramMap 参数MAP
     * @return 返回响应字符串
     */
    public static String doGet(String url, Map<String, String> paramMap) {
        return doGet(url, paramMap, null, DEFAULT_CONNECTION_TIMEOUT, DEFAULT_READ_TIMEOUT);
    }

    /**
     * get 请求
     *
     * @param url               请求地址，可以含参数
     * @param paramMap          参数MAP
     * @param connectionTimeout 连接超时时间
     * @param readTimeout       读超时时间
     * @return 返回响应字符串
     */
    public static String doGet(String url, Map<String, String> paramMap, int connectionTimeout, int readTimeout) {
        return doGet(url, paramMap, null, connectionTimeout, readTimeout);
    }

    /**
     * url 和参数连接
     *
     * @param url      url 地址
     * @param paramMap 参数MAP
     * @return
     */
    private static String concatUrlAndParams(String url, Map<String, String> paramMap) {
        if (null == paramMap || paramMap.isEmpty()) {
            return url;
        }
        return UrlUtil.appendUrlParams(url, paramMap);
    }

    /**
     * Get 请求，默认连接和读超时时间是 3 秒
     *
     * @param url 请求地址
     * @return 返回结果
     */
    public static String doGet(String url) {
        return doGet(url, DEFAULT_CONNECTION_TIMEOUT, DEFAULT_READ_TIMEOUT);
    }

    /**
     * get 请求
     *
     * @param url               请求地址，可以含参数
     * @param connectionTimeout 连接超时时间
     * @param readTimeout       读超时时间
     * @return 返回响应字符串
     */
    public static String doGet(String url, int connectionTimeout, int readTimeout) {

        return doGet(url, null, null, connectionTimeout, readTimeout);
    }

    public static String doGet(String url, Map<String, String> paramsMap, Map<String, String> cookieMap, int connectionTimeout, int readTimeout) {
        return doGet(url, paramsMap, cookieMap, connectionTimeout, readTimeout, "UTF-8");
    }

    /**
     * get 请求
     *
     * @param url               请求地址，可以含参数
     * @param paramsMap         参数MAP
     * @param cookieMap         Cookie Map
     * @param connectionTimeout 连接超时时间
     * @param readTimeout       读超时时间
     * @return 返回响应字符串
     */
    public static String doGet(String url, Map<String, String> paramsMap, Map<String, String> cookieMap, int connectionTimeout, int readTimeout, String encoding) {
        HttpURLConnection conn = null;
        InputStream is = null;
        String finalUrl = concatUrlAndParams(url, paramsMap);
        try {
            URL oriUrl = new URL(finalUrl);

            conn = (HttpURLConnection) oriUrl.openConnection();

            if (connectionTimeout > 0) {
                conn.setConnectTimeout(connectionTimeout);
            }
            if (readTimeout > 0) {
                conn.setReadTimeout(readTimeout);
            }

            conn.setRequestMethod("GET");

            addCookieForHttpUrlConnection(conn, cookieMap);

            if (logger.isDebugEnabled()) {
                logger.debug("HttpGet: url=" + finalUrl);
            }

            is = getInputStream(conn);

            return readInputStreamAsString(is, "UTF-8");
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            closeInputStream(is);
            closeConnection(conn);
        }
    }

    private static void addCookieForHttpUrlConnection(HttpURLConnection conn, Map<String, String> cookieMap) {

        if (null == cookieMap || cookieMap.isEmpty()) {
            return;
        }

        List<String> keys = new ArrayList<String>(cookieMap.keySet());
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = cookieMap.get(key);

            if (StringUtils.isNotBlank(value)) {
                conn.addRequestProperty("Cookie", key + "=" + value);
            }
        }
    }

    private static void closeConnection(HttpURLConnection conn) {
        if (null != conn) {
            conn.disconnect();
        }
    }

    private static InputStream getInputStream(HttpURLConnection httpURLConnection) throws IOException {
        try {
            return httpURLConnection.getInputStream();
        } catch (IOException e) {
            InputStream inputStream = httpURLConnection.getErrorStream();
            if (null == inputStream) {
                throw e;
            }
            return inputStream;
        }
    }

    private static void closeInputStream(InputStream is) {
        if (is != null) {
            try {
                is.close();
            } catch (IOException ignored) {
            }
        }
    }

    /**
     * Post 请求
     *
     * @param url 请求地址
     * @return 返回请求结果
     */
    public static String doPost(String url) {
        return doPost(url, null, DEFAULT_CONNECTION_TIMEOUT, DEFAULT_READ_TIMEOUT);
    }

    /**
     * Post 请求
     *
     * @param url               请求地址
     * @param paramMap          参数MAP
     * @param connectionTimeout 连接超时时间
     * @param readTimeout       读超时时间
     * @return 返回请求结果
     */
    private static String doPost(String url, Map<String, String> paramMap, int connectionTimeout, int readTimeout) {
        return doPost(url, paramMap, null, connectionTimeout, readTimeout);
    }

    public static String doPost(String url, Map<String, String> paramMap, Map<String, String> cookieMap, int connectionTimeout, int readTimeout) {
        return doPost(url, paramMap, cookieMap, connectionTimeout, readTimeout, "UTF-8");
    }

    /**
     * Post 请求
     *
     * @param url               请求地址
     * @param paramMap          参数MAP
     * @param cookieMap         要传输的Cookie
     * @param connectionTimeout 连接超时时间
     * @param readTimeout       读超时时间
     * @param encoding          编码
     * @return 返回请求结果
     */
    public static String doPost(String url, Map<String, String> paramMap, Map<String, String> cookieMap, int connectionTimeout, int readTimeout, String encoding) {
        HttpURLConnection conn = null;
        InputStream is = null;
        try {
            URL oriUrl = new URL(url);

            conn = (HttpURLConnection) oriUrl.openConnection();

            if (connectionTimeout > 0) {
                conn.setConnectTimeout(connectionTimeout);
            }
            if (readTimeout > 0) {
                conn.setReadTimeout(readTimeout);
            }

            conn.setRequestMethod("POST");

            // do not use cache
            conn.setUseCaches(false);
            // use for output
            conn.setDoOutput(true);
            // use for Input
            conn.setDoInput(true);

            String postData = UrlUtil.toUrlParamsString(paramMap, true, false);
            if (StringUtils.isNotBlank(postData)) {
                PrintWriter out = new PrintWriter(conn.getOutputStream());
                // send to server
                out.print(postData);
                // close output stream
                out.close();
            }

            if (logger.isDebugEnabled()) {
                logger.debug("HttpPost: url=" + url + ", postData=" + postData);
            }

            addCookieForHttpUrlConnection(conn, cookieMap);

            is = getInputStream(conn);

            return readInputStreamAsString(is, encoding);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            closeInputStream(is);
            closeConnection(conn);
        }
    }

    private static String readInputStreamAsString(InputStream is, String encoding) throws IOException {
        if (null == is) {
            return null;
        }
        if (null == encoding || "".equals(encoding.trim())) {
            encoding = "UTF-8";
        }
        StringBuilder builder = new StringBuilder();
        byte[] b = new byte[4096];
        for (int n; (n = is.read(b)) != -1; ) {
            builder.append(new String(b, 0, n, encoding));
        }
        return builder.toString();
    }
}
