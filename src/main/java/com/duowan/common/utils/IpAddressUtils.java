package com.duowan.common.utils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.net.*;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 功能：IP 工具类
 *
 * @author zengyuan on 2018/4/23.
 * @see
 */
public final class IpAddressUtils {

    private IpAddressUtils() {
    }

    private static final Pattern IP_PATTERN = Pattern.compile("^[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}$");
    private static final Pattern PRIVATE_IP_PATTERN = Pattern.compile("127\\.0\\.0\\.1");
    private static final Logger logger = LoggerFactory.getLogger(IpAddressUtils.class);

    // private static String localIp = doGetLocalIp();// 线上机器是将两个网卡绑定在一起的，故这种方式获取到的是127IP
    private static String localIp = getLocalIP(true);

    /**
     * 获取本机IP地址
     */
    public static String getLocalIp() {
        return localIp;
    }

    /**
     * 判断是否是一个IP地址格式
     */
    public static boolean isIP(String ip) {
        if (StringUtils.isBlank(ip))
            return false;
        Matcher matcher = IP_PATTERN.matcher(ip);
        return matcher.matches();
    }

    /**
     * 判断是否是一个回环IP
     */
    public static boolean isLoopbackIP(String ip) {
        if (StringUtils.isBlank(ip))
            return false;
        Matcher matcher = PRIVATE_IP_PATTERN.matcher(ip);
        return matcher.matches();
    }

    /**
     * 判断是否是一个
     */
    public static boolean isLocalIP(String ip) {
        return isLoopbackIP(ip) || localIp.equals(ip);
    }

    // ####
    // ## private func
    // ####

    protected static String doGetLocalIp() {
        String ip = null;
        try {
            String os = System.getProperty("os.name").toLowerCase();
            if (os.startsWith("windows")) {
                InetAddress localHost = InetAddress.getLocalHost();
                ip = localHost.getHostAddress();
            }
            // Linux
            else {
                ip = getLinuxIpAddress();
            }
        } catch (UnknownHostException e) {
            logger.error(e.getMessage(), e);
        }
        logger.info("The LocalIpAddress (1) Is {}", ip);
        return ip;
    }

    private static String getLinuxIpAddress() {
        String ip = null;
        try {
            Enumeration<NetworkInterface> interfaces = (Enumeration<NetworkInterface>) NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface ni = (NetworkInterface) interfaces.nextElement();
//				if (ni.getName().equals("eth0")) {
                Enumeration<InetAddress> addresses = ni.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress address = (InetAddress) addresses.nextElement();
                    if (address instanceof Inet6Address)
                        continue;
                    ip = address.getHostAddress();
                    if (null == ip || !isIP(ip) || !isLoopbackIP(ip))
                        continue;
                    return ip;
                }
//					break;
//				}
            }
        } catch (SocketException e) {
            logger.error(e.getMessage(), e);
        }
        return ip;
    }

    // ####################################

    /**
     * 将IP地址转换成InetAddress，系统底层不会产生域名服务调用
     *
     * @param rawIP IP地址的串，比如"172.18.16.36"
     * @return null if rawIP error
     */
    public static InetAddress ip2Addr(String rawIP) {
        byte[] addr = textToNumericFormatV4(rawIP);
        try {
            return InetAddress.getByAddress(rawIP, addr);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static byte[] textToNumericFormatV4(String paramString) {
        if (paramString.length() == 0) {
            return null;
        }

        byte[] arrayOfByte = new byte[4];
        // String[] arrayOfString = paramString.split("\\.", -1);
        String[] arrayOfString = StringUtils.split(paramString, ".");
        try {
            long l;
            int i;
            switch (arrayOfString.length) {
                case 1:
                    l = Long.parseLong(arrayOfString[0]);
                    if ((l < 0L) || (l > 4294967295L))
                        return null;
                    arrayOfByte[0] = (byte) (int) (l >> 24 & 0xFF);
                    arrayOfByte[1] = (byte) (int) ((l & 0xFFFFFF) >> 16 & 0xFF);
                    arrayOfByte[2] = (byte) (int) ((l & 0xFFFF) >> 8 & 0xFF);
                    arrayOfByte[3] = (byte) (int) (l & 0xFF);
                    break;
                case 2:
                    l = Integer.parseInt(arrayOfString[0]);
                    if ((l < 0L) || (l > 255L))
                        return null;
                    arrayOfByte[0] = (byte) (int) (l & 0xFF);
                    l = Integer.parseInt(arrayOfString[1]);
                    if ((l < 0L) || (l > 16777215L))
                        return null;
                    arrayOfByte[1] = (byte) (int) (l >> 16 & 0xFF);
                    arrayOfByte[2] = (byte) (int) ((l & 0xFFFF) >> 8 & 0xFF);
                    arrayOfByte[3] = (byte) (int) (l & 0xFF);
                    break;
                case 3:
                    for (i = 0; i < 2; ++i) {
                        l = Integer.parseInt(arrayOfString[i]);
                        if ((l < 0L) || (l > 255L))
                            return null;
                        arrayOfByte[i] = (byte) (int) (l & 0xFF);
                    }
                    l = Integer.parseInt(arrayOfString[2]);
                    if ((l < 0L) || (l > 65535L))
                        return null;
                    arrayOfByte[2] = (byte) (int) (l >> 8 & 0xFF);
                    arrayOfByte[3] = (byte) (int) (l & 0xFF);
                    break;
                case 4:
                    for (i = 0; i < 4; ++i) {
                        l = Integer.parseInt(arrayOfString[i]);
                        if ((l < 0L) || (l > 255L))
                            return null;
                        arrayOfByte[i] = (byte) (int) (l & 0xFF);
                    }
                    break;
                default:
                    return null;
            }
        } catch (NumberFormatException localNumberFormatException) {
            return null;
        }
        return arrayOfByte;
    }

    public static long textToLongFormatV4(String paramString) {
        byte[] ip = textToNumericFormatV4(paramString);
        if (ip == null) {
            return -1;
        } else {
            return ((ip[0] << 24) & 0xFFFFFFFFL) + ((ip[1] << 16) & 0xFFFFFF) + ((ip[2] << 8) & 0xFFFF) + (ip[3] & 0xFF);
        }
    }

    public static boolean isIPv4LiteralAddress(String paramString) {
        return (textToNumericFormatV4(paramString) != null);
    }

    private static String getLocalIP(boolean innerNet) {
        String ip = doGetLocalIP(innerNet);
        logger.info("The LocalIpAddress (2) Is {}", ip);
        return ip;
    }

    /**
     * 获取本机的内网或外网IP(第一个匹配马上返回)
     *
     * @param innerNet true:内网IP false:外网IP
     * @return
     */
    private static String doGetLocalIP(boolean innerNet) {
        try {
            Enumeration<NetworkInterface> netInterfaces = NetworkInterface.getNetworkInterfaces();
            while (netInterfaces.hasMoreElements()) {
                NetworkInterface ni = netInterfaces.nextElement();
                Enumeration<InetAddress> address = ni.getInetAddresses();
                while (address.hasMoreElements()) {
                    InetAddress ip = address.nextElement();
                    String ipStr = ip.getHostAddress();
                    if (ip.isAnyLocalAddress() //
                            || ip.isLinkLocalAddress() //
                            || ip.isLoopbackAddress() //
                            || ip.isMCNodeLocal() //
                            || ip.isMCLinkLocal() //
                            || ip.isMCNodeLocal() //
                            || ip.isMCOrgLocal() //
                            || ip.isMCSiteLocal() //
                            || ip.isMulticastAddress() //
                            || ipStr.contains(":")) {// 以上都是：先把特殊的IP过滤掉
                        continue;
                    } else if (ip.isSiteLocalAddress()) {// 内网IP
                        if (innerNet)
                            return ipStr;
                    } else {// 外网IP
                        return ipStr;
                    }
                }
            }
        } catch (SocketException e) {
            return null;
        }
        return null;
    }

    // ipv4 的格式, ten 2015/9/23
    private static final Pattern IPV4_PATTERN = Pattern
            .compile("^(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}$");

    /**
     * 获取正确的客户端ip地址
     *
     * @param request HttpServletRequest
     * @return 返回客户端的IP
     */
    public static String getClientIpAddr(HttpServletRequest request) {
        String ipAddress = null;
        ipAddress = request.getHeader("x-forwarded-for");
        if (ipAddress == null || ipAddress.length() == 0
                || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0
                || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0
                || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
            if ("127.0.0.1".equals(ipAddress)
                    || "0:0:0:0:0:0:0:1".equals(ipAddress)) {
                // 本机配置的IP
                ipAddress = OSUtil.getLocalIp();
            }

        }
        if (ipAddress != null) {
            String[] ips = ipAddress.split(",");
            for (String ip : ips) {
                if (isIPv4Address(ip.trim())) {
                    ipAddress = ip.trim();
                    break;
                }
            }
        }

        return ipAddress;

    }

    /**
     * 测试传入的参数是否正确的ipv4格式
     *
     * @param ip
     * @return true=是IPV4
     */
    public static boolean isIPv4Address(String ip) {
        if (ip == null)
            return false;

        return IPV4_PATTERN.matcher(ip).matches();
    }

    public static void main(String[] args) {
        System.out.println(getLocalIp());
    }

}
