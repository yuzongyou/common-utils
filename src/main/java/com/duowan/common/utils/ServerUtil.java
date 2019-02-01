package com.duowan.common.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * @author Arvin
 */
public class ServerUtil {

    private static String hostAddress = null;

    public static String getServerIp() {
        if (hostAddress != null) {
            return hostAddress;
        }
        hostAddress = getServerIpNoCache();
        return hostAddress;
    }

    private static String getServerIpNoCache() {
        if (SystemUtils.IS_OS_WINDOWS) {
            return "127.0.0.1";
        } else if (SystemUtils.IS_OS_MAC) {
            return "127.0.0.1";
        } else if (SystemUtils.IS_OS_LINUX) {
            try {
                return getServerIp("eth0");
            } catch (SocketException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        } else {
            throw new RuntimeException("未知操作系统.");
        }

    }

    public static String getServerIp(String displayName) throws SocketException {
        NetworkInterface networkInterface = NetworkInterface.getByName(displayName);
        List<String> subIpList = listSubIp(networkInterface);
        List<String> ipList = listAllIp(networkInterface);
        ipList.removeAll(subIpList);
        if (ipList.size() == 0) {
            throw new RuntimeException("为什么IP列表为空?]");
        } else if (ipList.size() > 1) {
            throw new RuntimeException("怎么IP数量超过1个？[" + StringUtils.join(ipList, ",") + "]");
        }
        String serverIp = ipList.get(0);
        return serverIp;
    }

    private static List<String> listSubIp(NetworkInterface networkInterface) {
        List<String> subIpList = new ArrayList<String>();
        Enumeration<NetworkInterface> subInterfaces = networkInterface.getSubInterfaces();
        while (subInterfaces.hasMoreElements()) {
            NetworkInterface net = subInterfaces.nextElement();
            List<String> ipList = listAllIp(net);
            String name = net.getName();
            System.out.println("name:" + name + " ipList:" + ipList);
            subIpList.addAll(ipList);
        }
        return subIpList;
    }

    private static List<String> listAllIp(NetworkInterface networkInterface) {
        List<String> ipList = new ArrayList<String>();
        Enumeration<InetAddress> ias = networkInterface.getInetAddresses();
        while (ias.hasMoreElements()) {
            InetAddress inet = ias.nextElement();
            String ip = inet.getHostAddress();
            if (!ip.contains(".")) {
                // ipv6
            } else {
                ipList.add(ip);
            }
        }
        return ipList;
    }
}
