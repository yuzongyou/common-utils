package com.duowan.common.utils;

import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;

import static org.junit.Assert.*;

/**
 * @author Arvin
 */
public class UrlPatternUtilTest {

    @Test
    public void testIsMatch() throws Exception {

        assertFalse(UrlPatternUtil.isMatch("", new HashSet<String>()));
        assertTrue(UrlPatternUtil.isMatch("/admin/ad.do", new HashSet<String>(Arrays.asList(
                "/*.do"
        ))));

        assertTrue(UrlPatternUtil.isMatch("/admin/xa.do", new HashSet<String>(Arrays.asList(
                "*.do"
        ))));

    }
}