package com.duowan.common.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 每周星期
 *
 * @author Arvin
 */
public enum WeekDay {

    /** 周日 */
    SUNDAY("星期日") {
        @Override
        public int getDay() {
            return Calendar.SUNDAY;
        }

        @Override
        public WeekDay getNextDay() {
            return WeekDay.MONDAY;
        }

        @Override
        public WeekDay getPrevDay() {
            return WeekDay.SATURDAY;
        }
    },

    /** 周一 */
    MONDAY("星期一") {
        @Override
        public int getDay() {
            return Calendar.MONDAY;
        }

        @Override
        public WeekDay getNextDay() {
            return WeekDay.TUESDAY;
        }

        @Override
        public WeekDay getPrevDay() {
            return WeekDay.SUNDAY;
        }
    },

    /** 周二 */
    TUESDAY("星期二") {
        @Override
        public int getDay() {
            return Calendar.TUESDAY;
        }

        @Override
        public WeekDay getNextDay() {
            return WeekDay.WEDNESDAY;
        }

        @Override
        public WeekDay getPrevDay() {
            return WeekDay.MONDAY;
        }
    },

    /** 周三 */
    WEDNESDAY("星期三") {
        @Override
        public int getDay() {
            return Calendar.WEDNESDAY;
        }

        @Override
        public WeekDay getNextDay() {
            return WeekDay.THURSDAY;
        }

        @Override
        public WeekDay getPrevDay() {
            return WeekDay.TUESDAY;
        }
    },

    /** 周四 */
    THURSDAY("星期四") {
        @Override
        public int getDay() {
            return Calendar.THURSDAY;
        }

        @Override
        public WeekDay getNextDay() {
            return WeekDay.FRIDAY;
        }

        @Override
        public WeekDay getPrevDay() {
            return WeekDay.WEDNESDAY;
        }
    },

    /** 周五 */
    FRIDAY("星期五") {
        @Override
        public int getDay() {
            return Calendar.FRIDAY;
        }

        @Override
        public WeekDay getNextDay() {
            return WeekDay.SATURDAY;
        }

        @Override
        public WeekDay getPrevDay() {
            return WeekDay.THURSDAY;
        }
    },

    /** 周六 */
    SATURDAY("星期六") {
        @Override
        public int getDay() {
            return Calendar.SATURDAY;
        }

        @Override
        public WeekDay getNextDay() {
            return WeekDay.SUNDAY;
        }

        @Override
        public WeekDay getPrevDay() {
            return WeekDay.FRIDAY;
        }
    };

    private static Map<Integer, WeekDay> MAP = new HashMap<>();

    static {
        WeekDay[] weekDays = WeekDay.values();

        for (WeekDay weekDay : weekDays) {
            MAP.put(weekDay.getDay(), weekDay);
        }
    }

    /**
     * 计算指定时间是周几
     *
     * @param date 日期
     * @return 返回星期几
     */
    public static WeekDay getWeekDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return MAP.get(cal.get(Calendar.DAY_OF_WEEK));
    }

    public static boolean isSubday(Date date) {
        return WeekDay.SUNDAY.equals(getWeekDay(date));
    }

    public static boolean isMonday(Date date) {
        return WeekDay.MONDAY.equals(getWeekDay(date));
    }

    public static boolean isTuesday(Date date) {
        return WeekDay.TUESDAY.equals(getWeekDay(date));
    }

    public static boolean isWednesday(Date date) {
        return WeekDay.WEDNESDAY.equals(getWeekDay(date));
    }

    public static boolean isThursday(Date date) {
        return WeekDay.THURSDAY.equals(getWeekDay(date));
    }

    public static boolean isFriday(Date date) {
        return WeekDay.FRIDAY.equals(getWeekDay(date));
    }

    public static boolean isSaturday(Date date) {
        return WeekDay.SATURDAY.equals(getWeekDay(date));
    }

    WeekDay(String chdesc) {
        this.chdesc = chdesc;
    }

    /** 中文格式 */
    private String chdesc;

    public abstract int getDay();

    /** 获取下一天 */
    public abstract WeekDay getNextDay();

    /** 获取前一天 */
    public abstract WeekDay getPrevDay();

    public String getChdesc() {
        return chdesc;
    }

    @Override
    public String toString() {
        return super.toString() + "(" + chdesc + ")";
    }
}
