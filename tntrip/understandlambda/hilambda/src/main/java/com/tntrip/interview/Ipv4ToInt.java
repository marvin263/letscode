package com.tntrip.interview;

/**
 * <pre>
 * 问题：实现 ipv4 和 int数字 的相互转换
 *
 * 要求：
 * 1. 没有要求，只要实现此效果即可
 *
 * 如果你需要位操作，可以参考下面给出的备忘。（以防你忘了了位操作符，下面给出了 位操作符 的语法）
 *
 * </pre>
 * <pre>
 *
 * Java 位操作 备忘
 *
 * a. 算术左移 3位 ：i<<3
 *
 * b. 算术右移 3位（高位 补充 符号位） ：i>>3
 * c. 逻辑右移 3位 ：i>>>3
 *
 * d. 按位与 ：i & 0xFF
 *
 * e. 按位或 ：i | 0xFF
 *
 * f. 异或（Exclusive OR，xor，即 位同则得0；位不同则得1） ：i ^ 0xFF
 *
 * g. 取反 ：~i
 *
 * </pre>
 */
public class Ipv4ToInt {

    /**
     * 0-->0.0.0.0
     * -1-->255.255.255.255
     * 2130706433-->127.0.0.1
     * -1062731519-->192.168.1.1
     * -1062731676-->192.168.0.100
     * -5767068-->255.168.0.100
     *
     * @param iIPV4
     * @return
     */
    public static String integer2IPV4(int iIPV4) {
        return null;
    }

    /**
     * 0.0.0.0-->0
     * 255.255.255.255-->-1
     * 127.0.0.1-->2130706433
     * 192.168.1.1-->-1062731519
     * 192.168.0.100-->-1062731676
     * 255.168.0.100-->-5767068
     *
     * @param strIPV4
     * @return
     */
    public static int IPV42Integer(String strIPV4) {
        return -1;
    }

    public static void main(String[] args) {

    }
}
