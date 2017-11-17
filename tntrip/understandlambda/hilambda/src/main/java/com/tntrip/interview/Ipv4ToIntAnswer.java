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
 *
 * Created by libing2 on 2016/10/10.
 */
public class Ipv4ToIntAnswer {

    /**
     * Convert an integer to a IPV4
     * <p>
     * <code>iIPV4</code>'s highest byte represents the first ip seg
     *
     * @param iIPV4
     * @return
     */
    public static String integer2IPV4(int iIPV4) {
        StringBuilder sb = new StringBuilder();
        for (int i = 3; i >= 0; i--) {
            // right shift each byte of iIPV4 to the rightest side
            // and then use 0xFF to mask this new-gotten integer.
            // So we get the integer value of the rightest byte
            int aSeg = (iIPV4 >> (8 * i)) & 0xFF;
            if (i == 0) {
                sb.append(aSeg);
            } else {
                sb.append(aSeg + ".");
            }
        }
        System.out.println(iIPV4 + "-->" + sb.toString());
        return sb.toString();
    }

    /**
     * Convert a String IPV4 to an integer
     * <p>
     * <code>strIPV4</code>'s first seg is the highest byte of the returned
     * integer
     *
     * @param strIPV4
     * @return
     */
    public static int IPV42Integer(String strIPV4) {
        int iIPV4 = 0x00;
        String[] segArray = strIPV4.split("\\.");
        for (int i = 0; i < segArray.length; i++) {
            // seg <=255, so all the bits except rightest 8 ones are 0
            int seg = Integer.parseInt(segArray[i]);
            // left shift the current iIPV4 1-byte-distance, then "append" the
            // strIPV4Seg to rightest side
            iIPV4 = (iIPV4 << 8) | seg;
        }
        System.out.println(strIPV4 + "-->" + iIPV4);
        return iIPV4;
    }

    public static void main(String[] args) {
        integer2IPV4(IPV42Integer("0.0.0.0"));
        integer2IPV4(IPV42Integer("255.255.255.255"));
        integer2IPV4(IPV42Integer("127.0.0.1"));
        integer2IPV4(IPV42Integer("192.168.1.1"));
        integer2IPV4(IPV42Integer("192.168.0.100"));
        integer2IPV4(IPV42Integer("255.168.0.100"));
    }
}
