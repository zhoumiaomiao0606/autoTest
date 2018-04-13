package com.yunche.loan.config.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class IPUtil {

    /**
     * ip地址转成long型数字
     * 将IP地址转化成整数的方法如下：
     * 1、通过String的split方法按.分隔得到4个长度的数组
     * 2、通过左移位操作（<<）给每一段的数字加权，第一段的权为2的24次方，第二段的权为2的16次方，第三段的权为2的8次方，最后一段的权为1
     * @param strIp
     * @return
     */
    public static long ipToLong(String strIp) {
        String[] ip = strIp.split("\\.");
        return (Long.parseLong(ip[0]) << 24) +
                (Long.parseLong(ip[1]) << 16) +
                (Long.parseLong(ip[2]) << 8) +
                Long.parseLong(ip[3]);
    }

    /**
     * IP完全可以由一个int类型的数字存储，不需要占用long类型大小
     * @param strIp
     * @return
     */
    public static long ipToInt(String strIp) {
        String[] ip = strIp.split("\\.");
        return (Integer.parseInt(ip[0]) << 24) +
                (Integer.parseInt(ip[1]) << 16) +
                (Integer.parseInt(ip[2]) << 8) +
                Integer.parseInt(ip[3]);
    }

    public static long ipToIntByByte(String strIp) throws UnknownHostException {
        final byte[] bytes = InetAddress.getByName(strIp).getAddress();
        int addr = bytes[3] & 0xFF;
        addr |= ((bytes[2] << 8) & 0xFF00);
        addr |= ((bytes[1] << 16) & 0xFF0000);
        addr |= ((bytes[0] << 24) & 0xFF000000);
        return addr;
    }

    /**
     * 将十进制整数形式转换成127.0.0.1形式的ip地址
     * 将整数形式的IP地址转化成字符串的方法如下：
     * 1、将整数值进行右移位操作（>>>），右移24位，右移时高位补0，得到的数字即为第一段IP。
     * 2、通过与操作符（&）将整数值的高8位设为0，再右移16位，得到的数字即为第二段IP。
     * 3、通过与操作符吧整数值的高16位设为0，再右移8位，得到的数字即为第三段IP。
     * 4、通过与操作符吧整数值的高24位设为0，得到的数字即为第四段IP。
     * @param longIp
     * @return
     */
    public static String longToIP(long longIp) {
        StringBuffer sb = new StringBuffer("");
        // 直接右移24位
        sb.append(String.valueOf((longIp >>> 24)));
        sb.append(".");
        // 将高8位置0，然后右移16位
        sb.append(String.valueOf((longIp & 0x00FFFFFF) >>> 16));
        sb.append(".");
        // 将高16位置0，然后右移8位
        sb.append(String.valueOf((longIp & 0x0000FFFF) >>> 8));
        sb.append(".");
        // 将高24位置0
        sb.append(String.valueOf((longIp & 0x000000FF)));
        return sb.toString();
    }

    /**
     * 将int类型的数字转化为IP
     * @param intIp
     * @return
     */
    public static String intToIP(int intIp) {
        StringBuffer sb = new StringBuffer("");
        // 直接右移24位
        sb.append(String.valueOf((intIp >>> 24)));
        sb.append(".");
        // 将高8位置0，然后右移16位
        sb.append(String.valueOf((intIp & 0x00FFFFFF) >>> 16));
        sb.append(".");
        // 将高16位置0，然后右移8位
        sb.append(String.valueOf((intIp & 0x0000FFFF) >>> 8));
        sb.append(".");
        // 将高24位置0
        sb.append(String.valueOf((intIp & 0x000000FF)));
        return sb.toString();
    }

    public static void main(String[] args) {
        System.out.println(ipToInt("192.168.1.234"));
        System.out.println(intToIP(-1062731286));
    }

}