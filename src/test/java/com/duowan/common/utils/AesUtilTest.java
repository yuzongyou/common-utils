package com.duowan.common.utils;

import org.junit.Test;

/**
 * @author Arvin
 */
public class AesUtilTest {

    @Test
    public void testEncryptAndDecrypt() {

        String key = "2d9472f8-18bb-4291-8d0b-7851e8088f3b";
        String content = "B2A9EEC49B2C841B1D722046378F11DB";
        String encode = AesUtil.encrypt(content, key);
        System.out.println("编码后: " + encode);
        String decode = AesUtil.encrypt(encode, key);
        System.out.println("解码后: " + decode);
    }

}