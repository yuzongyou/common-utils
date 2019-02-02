package com.yygame.common.utils;

import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Method;
import java.util.*;

/**
 * 通用工具类，杂七杂八的就放在这里
 *
 * @author yzy
 */
public abstract class CommonUtil {

    /**
     * List 进行反转
     *
     * @param sourceList 要反转的列表
     * @param <T>        类型
     * @return 返回反转后的列表
     */
    public static <T> List<T> reverseList(List<T> sourceList) {

        if (sourceList == null || sourceList.size() < 2) {
            return sourceList;
        }

        int backIndex = sourceList.size() - 1;
        int frontIndex = 0;

        while (backIndex != frontIndex && frontIndex < backIndex) {
            T temp = sourceList.get(backIndex);
            sourceList.set(backIndex, sourceList.get(frontIndex));
            sourceList.set(frontIndex, temp);

            frontIndex++;
            backIndex--;
        }

        return sourceList;
    }

    /**
     * 首字母小写
     *
     * @param value 字符串
     */
    public static String firstLetterToLowerCase(String value) {
        if (StringUtils.isBlank(value)) {
            return value;
        }
        if (value.length() == 1) {
            return value.toLowerCase();
        }

        return String.valueOf(value.charAt(0)).toLowerCase() + value.substring(1);
    }

    /**
     * 首字母大写
     *
     * @param value 字符串
     */
    public static String firstLetterToUpperCase(String value) {
        if (StringUtils.isBlank(value)) {
            return value;
        }
        if (value.length() == 1) {
            return value.toUpperCase();
        }

        return String.valueOf(value.charAt(0)).toUpperCase() + value.substring(1);
    }

    /**
     * 下划线
     */
    public static final String UNDERLINE = "_";

    /**
     * 将下划线转换成驼峰， 并且首字母小写
     *
     * @param value                要转换的字符串
     * @param firstLetterLowerCase 首字母是否小写
     * @return 返回驼峰格式的字符串
     */
    public static String underlineToHump(String value, boolean firstLetterLowerCase) {
        if (StringUtils.isNotBlank(value)) {
            String resultValue = value;
            if (resultValue.contains(UNDERLINE)) {
                String[] array = value.split(UNDERLINE);
                StringBuilder builder = new StringBuilder();
                for (int i = 0; i < array.length; ++i) {
                    String sub = array[i];
                    if (i > 0) {
                        sub = String.valueOf(sub.charAt(0)).toUpperCase() + sub.substring(1);
                    }
                    builder.append(sub);
                }
                resultValue = builder.toString();
            }
            if (firstLetterLowerCase) {
                return firstLetterToLowerCase(resultValue);
            }
        }
        return value;
    }

    /**
     * 驼峰转 下划线分割
     *
     * @param value 要转换的字符串
     * @return 返回下划线格式的字符串
     */
    public static String humpToUnderline(String value) {
        return firstLetterToLowerCase(value).replaceAll("([A-Z]+)", "_$1").toLowerCase();
    }

    /**
     * 检测给定的字符串任意一个是否是 blank
     *
     * @param strings 字符串列表
     * @return 如果给定的字符串有一个是blank的则返回true，所有都不为空才返回false
     */
    public static boolean isAnyBlank(CharSequence... strings) {
        return StringUtils.isAnyBlank(strings);
    }

    /**
     * 检查给定的字符串列表是否全部不是 blank
     *
     * @param strings 要检测的字符串列表
     * @return 如果有一个字符串为空则返回false，否则返回true
     */
    public static boolean isAllNotBlank(CharSequence... strings) {

        if (strings == null || strings.length < 1) {
            return false;
        }

        for (final CharSequence string : strings) {
            if (StringUtils.isBlank(string)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 检查给定的字符串列表是否全部为空
     *
     * @param strings 要检测的字符串集合
     * @return 如果有一个字符串为空则返回false，否则返回true
     */
    public static boolean isAllBlank(CharSequence... strings) {

        return StringUtils.isAllBlank(strings);
    }

    /**
     * 连接字符串, 默认中间没有分隔符
     *
     * @param nullToIgnore 是否忽略 null 对象, false 的话会转换成 "null" 再拼接
     * @param objects      对象列表
     * @return 返回连接好的字符串
     */
    public static String concat(boolean nullToIgnore, Object... objects) {
        return concat(nullToIgnore, "", objects);
    }

    /**
     * 连接字符串
     *
     * @param nullToIgnore 是否忽略 null 对象, false 的话会转换成 "null" 再拼接
     * @param separator    分隔符
     * @param objects      对象列表
     * @return 返回连接好的字符串
     */
    public static String concat(boolean nullToIgnore, String separator, Object... objects) {
        if (null == objects || objects.length < 1) {
            return "";
        }
        separator = null == separator ? "" : separator;
        StringBuilder builder = new StringBuilder();
        for (Object obj : objects) {
            if (null == obj) {
                if (!nullToIgnore) {
                    builder.append("null").append(separator);
                }
            } else {
                builder.append(obj).append(separator);
            }
        }
        return builder.toString();
    }

    /**
     * sleep， 不会抛出异常
     *
     * @param millis 毫秒数目
     */
    public static void sleep(int millis) {
        if (millis > 0) {
            try {
                Thread.sleep(millis);
            } catch (InterruptedException ignored) {
            }
        }
    }

    /**
     * 执行 trim
     *
     * @param value 要 trim 的值
     * @return 返回 trim 之后的值
     */
    public static String trim(String value) {
        return null == value ? null : value.trim();
    }

    public static boolean isNotEmpty(Collection<?> cols) {
        return null != cols && !cols.isEmpty();
    }

    /**
     * 删除不识别的属性
     *
     * @param configMap Map配置项
     * @param className 类名称
     */
    public static Map<String, String> filterUnRecordedField(Map<String, String> configMap, String className) {
        return filterUnRecordedField(configMap, ReflectUtil.loadClass(className, null));
    }

    /**
     * 删除不识别的属性
     *
     * @param configMap Map配置项
     * @param clazz     类对象
     */
    public static Map<String, String> filterUnRecordedField(Map<String, String> configMap, Class<?> clazz) {

        if (configMap == null || configMap.isEmpty() || null == clazz) {
            return configMap;
        }

        Set<String> fieldNames = configMap.keySet();

        Method[] allMethods = clazz.getMethods();

        Map<String, Method> getMethodMap = new HashMap<>();
        Map<String, Method> setMethodMap = new HashMap<>();

        for (Method method : allMethods) {
            String methodName = method.getName();
            String fieldName = firstLetterToLowerCase(methodName.replaceFirst("is|get|set", ""));
            if (methodName.startsWith("is") || methodName.startsWith("get")) {
                getMethodMap.put(fieldName, method);
            } else if (methodName.startsWith("set")) {
                setMethodMap.put(fieldName, method);
            }
        }

        Set<String> needRemoveFieldNames = new HashSet<>();

        for (String fieldName : fieldNames) {
            if (!getMethodMap.containsKey(fieldName) || !setMethodMap.containsKey(fieldName)) {
                needRemoveFieldNames.add(fieldName);
            }
        }

        if (!needRemoveFieldNames.isEmpty()) {
            for (String needRemoveFieldName : needRemoveFieldNames) {
                configMap.remove(needRemoveFieldName);
            }
        }

        return configMap;
    }

    /**
     * 过滤重复的实例对象
     *
     * @param instanceList 实例列表
     * @param <T>          实例类型
     * @return 返回去掉重复的实例列表
     */
    public static <T> List<T> filterDuplicateInstance(List<T> instanceList) {

        if (instanceList == null || instanceList.isEmpty()) {
            return instanceList;
        }

        List<T> resultList = new ArrayList<T>();

        for (T instance : instanceList) {
            if (!existsLogicEqualInstance(resultList, instance)) {
                resultList.add(instance);
            }
        }

        return resultList;
    }

    /**
     * 是否包含实例，判断逻辑为： class相同即可
     *
     * @param instanceList 实例列表
     * @param instance     实例
     * @param <T>          类型
     * @return 返回是否包含
     */
    private static <T> boolean existsLogicEqualInstance(List<T> instanceList, T instance) {
        if (instanceList == null || instanceList.isEmpty()) {
            return false;
        }
        if (instance == null) {
            return true;
        }
        Class<?> instanceClass = instance.getClass();
        for (T instanceTemp : instanceList) {
            if (instanceTemp != null && instanceTemp.getClass().equals(instanceClass)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否都是为null
     *
     * @param objects 判断数组是否都为null
     * @return 全部为 null 则返回 true
     */
    public static boolean isAllNull(Object[] objects) {
        if (objects == null || objects.length < 1) {
            return true;
        }
        for (Object object : objects) {
            if (null != object) {
                return false;
            }
        }
        return true;
    }

    /**
     * 是否至少包含一个不为 null 的对象
     *
     * @param objects 对象数组
     * @return 有一个不为null就返回true
     */
    public static boolean isAnyNotNull(Object[] objects) {
        if (objects == null || objects.length < 1) {
            return false;
        }

        for (Object object : objects) {
            if (null != object) {
                return true;
            }
        }
        return false;
    }

    /**
     * 划分出来
     *
     * @param value      值
     * @param splitRegex 默认是 [,，;，\\s]+
     * @return 返回非 null 集合
     */
    public static Set<String> splitAsStringSet(String value, String splitRegex) {

        if (StringUtils.isBlank(value)) {
            return new HashSet<>();
        }

        splitRegex = StringUtils.isBlank(splitRegex) ? "[,，;；\\s]+" : splitRegex;

        String[] array = value.split(splitRegex);

        Set<String> stringSet = new HashSet<>();

        for (String str : array) {
            if (StringUtils.isNotBlank(str)) {
                stringSet.add(str);
            }
        }

        return stringSet;
    }

    /**
     * 划分出来
     *
     * @param value      值
     * @param splitRegex 默认是 [,，;，\\s]+
     * @return 返回非 null 集合
     */
    public static List<String> splitAsStringList(String value, String splitRegex) {

        if (StringUtils.isBlank(value)) {
            return new ArrayList<>();
        }

        splitRegex = StringUtils.isBlank(splitRegex) ? "[,，;；\\s]+" : splitRegex;

        String[] array = value.split(splitRegex);

        List<String> stringList = new ArrayList<>();

        for (String str : array) {
            if (StringUtils.isNotBlank(str)) {
                stringList.add(str);
            }
        }

        return stringList;
    }

    /**
     * 追加列表
     *
     * @param sourceList     源列表
     * @param subCollections 子集合，要
     * @param <T>            对象类型
     * @return 返回 sourceList
     */
    public static <T> List<T> appendList(List<T> sourceList, Collection<T> subCollections) {

        if (sourceList == null || subCollections == null || subCollections.isEmpty()) {
            return sourceList;
        }

        sourceList.addAll(subCollections);

        return sourceList;
    }


    /**
     * 追加集合
     *
     * @param sourceSet      源集合
     * @param subCollections 子集合，要
     * @param <T>            对象类型
     * @return 返回 sourceList
     */
    public static <T> Set<T> appendSet(Set<T> sourceSet, Collection<T> subCollections) {

        if (sourceSet == null || subCollections == null || subCollections.isEmpty()) {
            return sourceSet;
        }

        sourceSet.addAll(subCollections);

        return sourceSet;
    }

    /**
     * 给定的字符串是否满足给定后缀列表中的任意一个
     *
     * @param value    字符串
     * @param suffixes 后缀
     * @return true 表示有匹配的后缀
     */
    public static boolean isAnySuffixMatch(String value, String... suffixes) {
        if (StringUtils.isBlank(value) || isAllBlank(suffixes)) {
            return false;
        }
        for (String suffix : suffixes) {
            if (value.endsWith(suffix)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 删除重复的值
     *
     * @param list 字符串列表
     * @return 返回去重后的列表
     */
    public static <T> List<T> filterDuplicateElement(List<T> list) {

        if (list == null || list.size() < 2) {
            return list;
        }

        List<T> resultList = new ArrayList<>();
        resultList.add(list.get(0));
        for (int i = 1; i < list.size(); ++i) {
            T element = list.get(i);
            boolean exists = false;
            for (T elt : resultList) {
                if (isEqual(element, elt)) {
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                resultList.add(element);
            }
        }

        return resultList;
    }

    /**
     * 判断两个对象是否相等， 同时为null 也是相等
     *
     * @param first  第一个对象
     * @param second 第二个对象
     * @return 返回是否相等
     */
    public static boolean isEqual(Object first, Object second) {
        if (first == null && second == null) {
            return true;
        }

        if (first != null && second != null) {
            return first.equals(second);
        }

        return false;
    }

    /**
     * wildcard 通配符 *
     */
    public static final String WILDCARD_START = "*";

    /**
     * 检查指定的字符串是否满足给定的通配符
     *
     * @param string          字符串
     * @param wildcardPattern 包含 * 的通配符
     * @return 返回是否匹配
     */
    public static boolean isStartWildcardMatch(String string, String wildcardPattern) {
        if (StringUtils.isAnyBlank(string, wildcardPattern)) {
            return false;
        }
        if (!wildcardPattern.contains(WILDCARD_START)) {
            return string.equals(wildcardPattern);
        }

        if (WILDCARD_START.endsWith(wildcardPattern)) {
            return true;
        }

        String regex = "^" + wildcardPattern.replaceAll("\\*", "[^\\*]*") + "$";

        return string.matches(regex);
    }

}
