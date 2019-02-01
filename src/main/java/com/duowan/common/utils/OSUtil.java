package com.duowan.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * 功能：IP 工具类
 *
 * @author zengyuan on 2018/4/23.
 * @see
 */
public class OSUtil {

    /**
     * IP的最后一个数字
     */
    private static String LAST_IP_NUM = null;

    /**
     * 本进程ID
     */
    private static String PROCESS_PID = null;

    /**
     * 本地IP
     */
    private static String LOCAL_IP = null;

    private static Logger logger = LoggerFactory.getLogger(OSUtil.class);

    /**
     * 判断是否为windows
     *
     * @return
     */
    public static boolean isWindowsOS() {
        boolean isWindowsOS = false;
        String osName = System.getProperty("os.name");
        if (osName.toLowerCase().indexOf("windows") > -1) {
            isWindowsOS = true;
        }
        return isWindowsOS;
    }

    /**
     * 获取本机IP
     *
     * @return
     */
    public static String getLocalIp() {
        if (LOCAL_IP == null) {
            LOCAL_IP = doGetLocalIp();
        }
        return LOCAL_IP;
    }

    /**
     * 获取本机IP
     *
     * @return
     */
    private static String doGetLocalIp() {
        try {
            if (isWindowsOS()) {
                return InetAddress.getLocalHost().getHostAddress();
            } else {
                // 本地IP，如果没有配置外网IP则返回它
                String localIp = null;
                // 外网IP
                String netIp = null;

                Enumeration<NetworkInterface> netInterfaces = NetworkInterface.getNetworkInterfaces();
                //获取所有子接口IP地址
                Set<String> subInterfaceIps = getSubInterfaceAddresses();

                InetAddress ip = null;
                // 是否找到外网IP
                boolean found = false;

                String ipAddr = null;

                while (netInterfaces.hasMoreElements() && !found) {
                    NetworkInterface ni = netInterfaces.nextElement();
                    Enumeration<InetAddress> address = ni.getInetAddresses();

                    while (address.hasMoreElements()) {
                        ip = address.nextElement();
                        ipAddr = ip.getHostAddress();

                        if (!ip.isSiteLocalAddress()
                                && !ip.isLoopbackAddress()
                                && !subInterfaceIps.contains(ipAddr) // 不是子接口的IP
                                && ipAddr.indexOf(":") == -1) {
                            // 外网IP
                            netIp = ipAddr;
                            found = true;
                            break;
                        } else if (ip.isSiteLocalAddress()
                                && !ip.isLoopbackAddress()
                                && ipAddr.indexOf(":") == -1) {
                            // 内网IP
                            localIp = ipAddr;
                        }
                    }
                }

                logger.info("netIp: " + netIp + ", localIp: " + localIp);
                if (netIp != null && !"".equals(netIp)) {
                    return netIp;
                } else {
                    return localIp;
                }
            }
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取网卡子接口（虚拟接口，例如eth0:1、eth0:cnc_246等）的IP地址
     *
     * @return
     * @throws SocketException
     */
    private static Set<String> getSubInterfaceAddresses() throws SocketException {
        //所有网络接口
        Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
        Set<String> subInterfaceAddresses = new HashSet<String>();

        while (networkInterfaces.hasMoreElements()) {
            NetworkInterface networkInterface = networkInterfaces.nextElement();

            //子接口
            Enumeration<NetworkInterface> subInterfaces = networkInterface.getSubInterfaces();

            while (subInterfaces.hasMoreElements()) {
                NetworkInterface subInterface = subInterfaces.nextElement();

                //子接口绑定的IP
                Enumeration<InetAddress> inetAddresses = subInterface.getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    subInterfaceAddresses.add(inetAddresses.nextElement().getHostAddress());
                }
            }
        }
        return subInterfaceAddresses;
    }

    /**
     * 获得IP的最后一个数字
     *
     * @return
     */
    public static String getIpLastNumber() {
        if (LAST_IP_NUM == null) {
            return doGetIpLastNumber();
        }
        return LAST_IP_NUM;
    }

    private static String doGetIpLastNumber() {
        String ip = getLocalIp();
        if (ip == null) {
            ip = "127.0.0.1";
        }
        int pos = ip.lastIndexOf(".");
        String num = "1";
        if (pos >= 0) {
            num = ip.substring(pos + 1);
        } else {
            // maybe ipv6
            num = String.valueOf(Math.abs((long) ip.hashCode()) % 1000);
        }
        if (num.length() == 1) {
            num = "00" + num;
        } else if (num.length() == 2) {
            num = "0" + num;
        }
        LAST_IP_NUM = num;
        return num;
    }

    /**
     * 获得java进程id
     *
     * @return java进程id
     */
    public static final String getPid() {
        if (PROCESS_PID == null) {
            RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
            String processName = runtimeMXBean.getName();
            String pid = null;
            if (processName.indexOf('@') != -1) {
                pid = processName.substring(0, processName.indexOf('@'));
            }

            if (pid == null) {
                logger.warn("Cannot get pid, random one ...");
                Random rand = new Random();
                pid = rand.nextInt(65536) + "";
            }
            if (pid.length() > 5) {
                pid = pid.substring(0, 5);
            }
            PROCESS_PID = pid;
            logger.info("pid = " + PROCESS_PID);
        }
        return PROCESS_PID;
    }
}
