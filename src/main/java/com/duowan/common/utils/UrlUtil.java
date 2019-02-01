package com.duowan.common.utils;

import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;

/**
 * @author Arvin
 */
public class UrlUtil {

    public static final String PROTOCOL_HTTPS = "https";
    public static final String PROTOCOL_HTTP = "http";

    private static final String HTTP_URL_ENCODE_REGEX = "(?i)https?%.*";

    private UrlUtil() {
    }

    /**
     * 根据参数名获取请求地址中的参数值
     *
     * @param url       请求地址
     * @param paramName 参数名
     * @return 参数值
     */
    public static String getParamValue(String url, String paramName) {
        if (StringUtils.isBlank(url) || StringUtils.isBlank(paramName)) {
            return null;
        }
        paramName = paramName + "=";
        for (String part : url.split("[?&]")) {
            if (part.contains(paramName)) {
                return part.replace(paramName, "");
            }
        }
        return null;
    }

    /**
     * 将 URL 参数转换成 Map 姓氏
     *
     * @param url url 地址，decode 之后的
     * @return 返回 url 参数
     */
    public static Map<String, String> extractParamsAsMap(String url) {
        AssertUtil.assertNotBlank(url, "要提取URL参数的URL不能为空");
        String noHashDataUrl = splitByHashData(url)[0];
        String paramString = noHashDataUrl.replaceFirst(".*\\?", "");
        String[] keyValues = paramString.split("&");

        if (keyValues.length < 1) {
            return new HashMap<>(0);
        }
        Map<String, String> map = new HashMap<>(keyValues.length);
        for (String keyValue : keyValues) {
            if (!keyValue.contains("=")) {
                map.put(keyValue, null);
            } else {
                String[] array = keyValue.split("=");
                if (array.length == 1) {
                    map.put(array[0], "");
                } else {
                    map.put(array[0], array[1]);
                }
            }

        }
        return map;
    }

    /**
     * 解码 URL
     *
     * @param url 要解码的 URL
     * @return 返回解码后的URL
     */
    public static String decodeUrl(String url) {
        try {
            if (StringUtils.isBlank(url)) {
                return "";
            }
            while (url.matches(HTTP_URL_ENCODE_REGEX)) {
                url = URLDecoder.decode(url, "UTF-8");
            }
            return url;
        } catch (Exception e) {
            return url;
        }
    }

    /**
     * 解码 参数值
     *
     * @param paramValue 要解码的参数值
     * @return 返回解码后的 值
     */
    public static String decodeParamValue(String paramValue) {
        try {
            if (StringUtils.isBlank(paramValue)) {
                return "";
            }
            String curVal = paramValue;
            String nextVal = URLDecoder.decode(curVal, "UTF-8");
            while (!curVal.equals(nextVal) && paramValue.contains("%")) {
                curVal = nextVal;
                nextVal = URLDecoder.decode(curVal, "UTF-8");
            }
            return nextVal;
        } catch (Exception e) {
            return paramValue;
        }
    }

    /**
     * encode url
     *
     * @param url url 地址
     * @return 返回encode指定次数后的url
     */
    public static String encodeUrl(String url) {
        return encodeUrl(url, 1);
    }

    /**
     * encode url
     *
     * @param url   url 地址
     * @param times encode 次数
     * @return 返回encode指定次数后的url
     */
    public static String encodeUrl(String url, int times) {
        try {
            String value = decodeUrl(url);
            int encodeTimes = 0;
            while (encodeTimes < times) {
                value = URLEncoder.encode(value, "UTF-8");
                encodeTimes++;
            }
            return value;
        } catch (Exception e) {
            return url;
        }
    }

    /**
     * encode paramValue
     *
     * @param paramValue paramValue 地址
     * @return 返回encode指定次数后的 参数值
     */
    public static String encodeParamValue(String paramValue) {
        return encodeParamValue(paramValue, 1);
    }

    /**
     * encode paramValue
     *
     * @param paramValue paramValue 地址
     * @param times      encode 次数
     * @return 返回encode指定次数后的 参数值
     */
    public static String encodeParamValue(String paramValue, int times) {
        try {
            String value = decodeParamValue(paramValue);
            int encodeTimes = 0;
            while (encodeTimes < times) {
                value = URLEncoder.encode(value, "UTF-8");
                encodeTimes++;
            }
            return value;
        } catch (UnsupportedEncodingException e) {
            return paramValue;
        }
    }

    /**
     * 切分成 hashData，返回一个数组，第一个值为url，第二个值为hash数据，
     *
     * @param url 要切分的 url
     * @return 返回hash数据， array[0]为不带hashData的地址， array[1] 为hash数据， 始终返回长度为2，且内容非空的数组
     */
    public static String[] splitByHashData(String url) {
        final String[] array = new String[]{url, ""};
        int index = url.indexOf("?");
        int indexHash = url.indexOf("#");
        if (indexHash > index) {
            array[1] = url.substring(indexHash);
            array[0] = url.substring(0, indexHash);
        }

        return array;
    }

    /**
     * 替换 URL 给定的参数
     *
     * @param url            url地址
     * @param key            参数key
     * @param newEncodeValue 新的经过 Encode 后的值
     * @return 如果url包含参数则进行替换，不存在则不进行操作
     */
    public static String replaceUrlParam(String url, String key, String newEncodeValue) {

        if (url.contains("#")) {
            String[] array = splitByHashData(url);
            return replaceUrlParam(array[0], key, newEncodeValue) + array[1];
        }

        String regex = "(^.*[\\?&]?)(" + key + "=?[^\\?&]*)([\\?&]?.*$)";

        return url.replaceAll(regex, "$1" + key + "=" + newEncodeValue + "$3");
    }

    /**
     * 替换（如果存在） 或者添加 URL 给定的参数
     *
     * @param url            url地址
     * @param key            参数key
     * @param newEncodeValue 新的经过 Encode 后的值
     * @return 如果url包含参数则进行替换，不存在则不进行操作
     */
    public static String appendOrReplaceUrlParam(String url, String key, String newEncodeValue) {

        if (url.contains("#")) {
            String[] array = splitByHashData(url);
            return appendOrReplaceUrlParam(array[0], key, newEncodeValue) + array[1];
        }

        // 包含参数，替换
        if (url.matches(".*[\\?&]?" + key + "=?[\\?&]?.*$")) {
            return replaceUrlParam(url, key, newEncodeValue);
        }

        // 不存在， 追加
        return appendUrlParams(url, key, newEncodeValue, false);
    }

    /**
     * 追加参数到指定的 url
     *
     * @param url   url地址
     * @param key   参数key
     * @param value 参数值
     * @return 返回添加了参数后的URL
     */
    public static String appendUrlParams(String url, String key, String value) {

        return appendUrlParams(url, key, value, true);
    }

    /**
     * 追加参数到指定的 url
     *
     * @param url   url地址
     * @param key   参数key
     * @param value 参数值
     * @return 返回添加了参数后的URL
     */
    public static String appendUrlParams(String url, String key, String value, boolean encode) {

        url = decodeUrl(url);

        if (StringUtils.isBlank(key)) {
            return url;
        }

        // 切分成url + hash
        String[] array = splitByHashData(url);
        String pureUrl = array[0];
        String hashData = array[1];

        String subQueryString = key;
        if (StringUtils.isNotBlank(value)) {
            subQueryString += "=" + (encode ? encodeParamValue(value) : value);
        }

        String finalUrl;

        if (pureUrl.contains("?")) {
            finalUrl = pureUrl + "&" + subQueryString;
        } else {
            finalUrl = pureUrl + "?" + subQueryString;
        }
        return finalUrl.replaceAll("\\?+", "?").replaceAll("&+", "&") + hashData;
    }

    /**
     * 追加 url 参数
     *
     * @param url       url地址
     * @param paramsMap 参数MAP
     * @return 返回完整的地址
     */
    public static String appendUrlParams(String url, Map<String, String> paramsMap) {

        url = decodeUrl(url);

        if (null == paramsMap || paramsMap.isEmpty()) {
            return url;
        }

        String subQueryString = toUrlParamsString(paramsMap, true, false);
        if (StringUtils.isBlank(subQueryString)) {
            return url;
        }

        // 切分成url + hash
        String[] array = splitByHashData(url);
        String pureUrl = array[0];
        String hashData = array[1];

        String finalUrl;

        if (pureUrl.contains("?")) {
            finalUrl = pureUrl + "&" + subQueryString;
        } else {
            finalUrl = pureUrl + "?" + subQueryString;
        }
        return finalUrl.replaceAll("\\?+", "?").replaceAll("&+", "&") + hashData;
    }

    /**
     * 把数组所有元素排序，并按照“参数=参数值”的模式用“&”字符拼接成字符串
     *
     * @param params 需要排序并参与字符拼接的参数组
     * @return 拼接后字符串, 注意不包含 "?"
     */
    public static String toUrlParamsString(Map<String, String> params, boolean encode, boolean sortKeys) {

        StringBuilder builder = new StringBuilder("");

        if (params == null || params.isEmpty()) {
            return builder.toString();
        }
        List<String> keys = new ArrayList<String>(params.keySet());

        if (sortKeys) {
            Collections.sort(keys);
        }

        for (String key : keys) {
            String value = params.get(key);

            if (StringUtils.isBlank(value)) {
                builder.append(key).append("&");
            } else {

                if (encode) {
                    value = encodeParamValue(value);
                }

                builder.append(key).append("=").append(value).append("&");
            }
        }

        builder.setLength(builder.length() - 1);
        return builder.toString();
    }

    /**
     * 将给定的 url 转换成目标请求协议的 url
     *
     * @param targetProtocol 目标协议，如 http, https
     * @param url            要处理的url
     * @return 返回经过适配协议后的 url
     */
    public static String adapterUrlProtocol(String targetProtocol, String url) {
        AssertUtil.assertTrue(PROTOCOL_HTTP.equalsIgnoreCase(targetProtocol) || PROTOCOL_HTTPS.equalsIgnoreCase(targetProtocol),
                "目标协议不合法，必须是[" + PROTOCOL_HTTPS + "], 或 [" + PROTOCOL_HTTP + "]");

        AssertUtil.assertNotBlank(url, "要适配协议的URL地址不能为空！");

        String regex = "^(?i)" + targetProtocol + "[%:].*$";
        if (url.matches(regex)) {
            // 原本就一样的协议，直接返回
            return url;
        }
        regex = "^(?i)(https?)([%:].*)$";
        return url.replaceFirst(regex, targetProtocol + "$2");

    }
}
