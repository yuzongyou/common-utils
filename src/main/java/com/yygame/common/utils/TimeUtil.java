package com.yygame.common.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 日期工具类
 *
 * @author yzy
 */
public class TimeUtil {

    public static final String UNIT_DAY = "d";
    public static final String UNIT_HOUR = "h";
    public static final String UNIT_MINUTE = "m";
    public static final String UNIT_SECOND = "s";

    /**
     * 是否是24制的时间
     *
     * @param hour 小时 ， 0-23
     * @return true 表示正确， false 表示错误
     */
    public static boolean is24Hour(int hour) {
        return hour > -1 && hour < 24;
    }

    /**
     * 检查 24 小时制格式是否正确
     *
     * @param hour 24 格式小时， 0-23
     */
    public static void check24Hour(int hour) {
        if (!is24Hour(hour)) {
            throw new IllegalArgumentException("时间格式错误，24小时制，请输入 0-23的值");
        }
    }

    /**
     * 是否是合法的分钟时间
     *
     * @param minute 分钟， 0-59
     * @return true 表示正确， false 表示错误
     */
    public static boolean isMinute(int minute) {
        return minute > -1 && minute < 60;
    }

    /**
     * 是否是合法的分钟时间
     *
     * @param minute 分钟， 0-59
     */
    public static void checkMinute(int minute) {
        if (!isMinute(minute)) {
            throw new IllegalArgumentException("分钟格式错误，请输入 0-59的值");
        }
    }

    /**
     * 是否是合法的秒时间
     *
     * @param second 秒， 0-59
     * @return true 表示正确， false 表示错误
     */
    public static boolean isSecond(int second) {
        return second > -1 && second < 60;
    }

    /**
     * 是否是合法的秒时间
     *
     * @param second 秒时间， 0-59
     */
    public static void checkSecond(int second) {
        if (!isSecond(second)) {
            throw new IllegalArgumentException("秒钟格式错误，请输入 0-59的值");
        }
    }

    /**
     * 时分秒格式检查
     *
     * @param hour24 24小时制
     * @param minute 分钟
     * @param second 秒钟
     */
    public static void checkHourMinuteSecond(int hour24, int minute, int second) {
        check24Hour(hour24);
        checkMinute(minute);
        checkSecond(second);
    }

    /**
     * 将时间单位转成毫秒数， 单位可以是 天-d, 小时-h， 分钟-m， 秒钟 - s
     *
     * @param timeStr 时间字符串，如 1h --> 1 小时， 1m --> 1分钟
     * @return 返回毫秒数
     */
    public static int toMilliseconds(String timeStr) {

        String regex = "(?i)^([0-9]+)([hms])?$";
        if (StringUtils.isBlank(timeStr) || !timeStr.matches(regex)) {
            throw new IllegalArgumentException("时间参数不正确：" + timeStr);
        }

        String ns = timeStr.replaceFirst(regex, "$1");
        String us = timeStr.replaceFirst(regex, "$2").toLowerCase();

        int number = Integer.parseInt(ns);
        if (UNIT_DAY.equals(us)) {
            return number * 24 * 60 * 60 * 1000;
        }
        if (UNIT_HOUR.equals(us)) {
            return number * 60 * 60 * 1000;
        }
        if (UNIT_MINUTE.equals(us)) {
            return number * 60 * 1000;
        }
        if (UNIT_SECOND.equals(us)) {
            return number * 1000;
        }
        // 毫秒
        return number;
    }

    /**
     * 转成时间间隔，单位是毫秒
     *
     * @param intervals 时间间隔字符串，如 1h,2h,3h
     * @return 返回毫秒形式的时间间隔
     */
    public static List<Integer> toIntervalMillisList(String intervals) {

        if (!isValidIntervals(intervals)) {
            throw new IllegalArgumentException("时间间隔字符串错误：[" + intervals + "]");
        }

        String[] array = intervals.trim().split(",");

        List<Integer> result = new ArrayList<>();

        for (String intervalStr : array) {
            if (StringUtils.isNotBlank(intervalStr)) {
                intervalStr = intervalStr.trim();
                result.add(toMilliseconds(intervalStr));
            }
        }

        return result;
    }

    public static boolean isValidIntervals(String intervals) {

        String regex = "(?i)^([0-9]+[hms]?,)*([0-9]+[hms]?)?$";

        if (StringUtils.isBlank(intervals) || !intervals.matches(regex) || intervals.endsWith(",")) {
            return false;
        }

        return true;
    }

    /**
     * 将毫秒数转成可读性好的字符串表示法,只会解析到天
     * d h m s
     *
     * @param millis 好描述
     * @return 返回可读性好的字符串
     */
    public static String toReadableFormat(int millis) {

        if (millis % 1000 != 0) {
            return String.valueOf(millis);
        }

        int seconds = millis / 1000;
        if (seconds % 60 != 0) {
            return seconds + "s";
        }
        int minute = seconds / 60;
        if (minute % 60 != 0) {
            return minute + "m";
        }
        int hour = minute / 60;
        if (hour % 24 != 0) {
            return hour + "h";
        }
        int day = hour / 24;
        return day + "d";
    }

    /**
     * 将毫秒数列表转换成可读性好的字符串
     *
     * @param millisList 毫秒数列表
     * @return 返回可读性好的字符串，中间用英文逗号分隔
     */
    public static String toReadableFormat(List<Integer> millisList) {

        if (null == millisList || millisList.isEmpty()) {
            return null;
        }

        StringBuilder builder = new StringBuilder();
        for (Integer millis : millisList) {
            builder.append(toReadableFormat(millis)).append(",");
        }

        builder.setLength(builder.length() - 1);
        return builder.toString();

    }
}
