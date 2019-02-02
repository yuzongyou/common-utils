package com.yygame.common.utils;


import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author yzy
 */
public class CommonUtilTest {

    @Test
    public void testIsStartWildcardMatch() throws Exception {

        assertTrue(CommonUtil.isStartWildcardMatch("ABCD", "*"));
        assertTrue(CommonUtil.isStartWildcardMatch("ABCD", "A*"));
        assertTrue(CommonUtil.isStartWildcardMatch("ABCD", "A*D"));
        assertTrue(CommonUtil.isStartWildcardMatch("ABCD", "A*C*D"));
        assertTrue(CommonUtil.isStartWildcardMatch("ABCD", "A*B*C*D"));
        assertTrue(CommonUtil.isStartWildcardMatch("ABCD", "*D"));
        assertTrue(CommonUtil.isStartWildcardMatch("ABCD", "ABCD*"));
        assertTrue(CommonUtil.isStartWildcardMatch("ABCD", "*ABCD*"));
        assertTrue(CommonUtil.isStartWildcardMatch("userxxx", "user*"));
        assertTrue(CommonUtil.isStartWildcardMatch("中国人民解放军", "中国*"));
        assertTrue(CommonUtil.isStartWildcardMatch("中国人民解放军", "*人民*军"));
    }
}