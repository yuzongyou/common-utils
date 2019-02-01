package com.duowan.common.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 中国居民身份证工具类
 *
 * @author dw_xiajiqiu1
 */
public class IDCardUtil {

    private static Logger logger = LoggerFactory.getLogger(IDCardUtil.class);

    /**
     * 旧版身份证号码长度
     **/
    private static int OLD_IDCARD_LENGTH = 15;

    /**
     * 新版身份证号码长度
     **/
    private static int NEW_IDCARD_LENGTH = 18;


    /**
     * 身份证区域代码Map
     */
    private static final Map<Integer, String> AREA_MAP;

    private static int[] WI = new int[]{7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};

    /**
     * 最后一位字符
     */
    private static String[] LAST_STR = new String[]{"1", "0", "X", "9", "8", "7", "6", "5", "4", "3", "2"};

    private static String IDCARD_REGEX = "^\\d+[\\dX]$";

    static {
        Map<Integer, String> areaMap = new HashMap<Integer, String>();
        areaMap.put(11, "北京");
        areaMap.put(12, "天津");
        areaMap.put(13, "河北");
        areaMap.put(14, "山西");
        areaMap.put(15, "内蒙古");
        areaMap.put(21, "辽宁");
        areaMap.put(22, "吉林");
        areaMap.put(23, "黑龙江");
        areaMap.put(31, "上海");
        areaMap.put(32, "江苏");
        areaMap.put(33, "浙江");
        areaMap.put(34, "安徽");
        areaMap.put(35, "福建");
        areaMap.put(36, "江西");
        areaMap.put(37, "山东");
        areaMap.put(41, "河南");
        areaMap.put(42, "湖北");
        areaMap.put(43, "湖南");
        areaMap.put(44, "广东");
        areaMap.put(45, "广西");
        areaMap.put(46, "海南");
        areaMap.put(50, "重庆");
        areaMap.put(51, "四川");
        areaMap.put(52, "贵州");
        areaMap.put(53, "云南");
        areaMap.put(54, "西藏");
        areaMap.put(61, "陕西");
        areaMap.put(62, "甘肃");
        areaMap.put(63, "青海");
        areaMap.put(64, "宁夏");
        areaMap.put(65, "新疆");
        areaMap.put(71, "台湾");
        areaMap.put(81, "香港");
        areaMap.put(82, "澳门");
        areaMap.put(91, "国外");
        AREA_MAP = areaMap;
    }

    /**
     * 是否超过指定的年龄
     *
     * @param idCard 身份证
     * @param minAge 最小年龄，包含
     * @return 返回是否超过或等于指定年龄
     */
    public static boolean isOverOrEqualAge(String idCard, int minAge) {
        int age = getAge(idCard);
        return age >= minAge;
    }

    /**
     * 提取身份证号码中的年龄
     *
     * @param idCard 身份证号码
     * @return 返回身份证号码年龄
     * @throws RuntimeException 如果不是合法的身份证将会抛出异常
     */
    public static int getAge(String idCard) throws RuntimeException {
        AssertUtil.assertTrue(isValidBirthday(idCard), "身份证格式不合法");
        idCard = convertToNewFormatIdCard(idCard);
        Date birthday = getNewIdCardBirthday(idCard);
        AssertUtil.assertNotNull(birthday, "身份证格式不合法");
        Calendar calendar1 = Calendar.getInstance();
        try {
            calendar1.setTime(birthday);
            Calendar calendar2 = Calendar.getInstance();
            calendar2.setTime(new Date());

            int age = calendar2.get(Calendar.YEAR) - calendar1.get(Calendar.YEAR);
            // 还没过生日，周岁减一
            if (calendar2.get(Calendar.MONTH) <= calendar1.get(Calendar.MONTH)) {
                // 还没过生日，周岁减一
                if (calendar2.get(Calendar.DAY_OF_MONTH) < calendar1.get(Calendar.DAY_OF_MONTH)) {
                    age -= 1;
                }
            }
            return age < 1 ? 0 : age;
        } catch (Exception e) {
            logger.warn("解析身份证年龄出错， idCard=" + idCard, e);
            return 0;
        }
    }

    /**
     * 判断是否是合法的身份证
     *
     * @param idCard - 身份证
     * @return - 合法返回true
     */
    public static boolean isValidIdcard(String idCard) {

        if (StringUtils.isBlank(idCard)) {
            return false;
        }
        idCard = idCard.trim();

        int len = idCard.length();
        if (len != OLD_IDCARD_LENGTH && len != NEW_IDCARD_LENGTH) {
            return false;
        }

        // 判断身份证区域代码
        int areaCode = NumberUtils.toInt(idCard.substring(0, 2));
        if (!AREA_MAP.containsKey(areaCode)) {
            return false;
        }

        // 把15位身份证转为18位
        idCard = len == OLD_IDCARD_LENGTH ? idCard.substring(0, 6) + "19" + idCard.substring(6, 15) : idCard;

        // 判断身份证除最后一位,是否全是数字
        if (!idCard.matches(IDCARD_REGEX)) {
            return false;
        }
        if (!isValidBirthday(idCard)) {
            return false;
        }

        // 验证身份证最后一位校验码
        int ret = 0;
        for (int i = 0; i < len - 1; i++) {
            ret += NumberUtils.toInt(idCard.charAt(i) + "") * WI[i];
        }
        return idCard.equals(idCard.substring(0, len - 1) + LAST_STR[ret % 11]);
    }

    /**
     * 验证新身份证号码的年龄是否正确
     *
     * @param idCard 身份证号码
     * @return 返回是否合法的出身分日期
     */
    private static boolean isValidBirthday(String idCard) {
        Date birthday = getNewIdCardBirthday(idCard);
        if (null == birthday) {
            return false;
        }
        if (birthday.after(new Date())) {
            return false;
        }
        return true;
    }

    private static Date getNewIdCardBirthday(String idCard) {
        String birthdayString = idCard.substring(6, 14);
        DateFormat format = new SimpleDateFormat("yyyyMMdd");
        try {
            return format.parse(birthdayString);
        } catch (ParseException e) {
            return null;
        }
    }

    private static String convertToNewFormatIdCard(String idCard) {
        if (idCard.length() == OLD_IDCARD_LENGTH) {
            // 把15位身份证转为18位
            return idCard.substring(0, 6) + "19" + idCard.substring(6, 15);
        }
        return idCard;
    }

}
