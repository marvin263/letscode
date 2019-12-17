package com.tntrip;

public class P191_Numberof1Bits {
        // you need to treat n as an unsigned value
        public int hammingWeight(int n) {
            int count = 0;
            for (int i = 0; i < 32; i++) {
                if ((n & (0x80000000 >>> i)) != 0) {
                    count++;
                }
            }
            return count;
        }

    public static void main(String[] args) {
        P191_Numberof1Bits n = new P191_Numberof1Bits();
        System.out.println(n.hammingWeight(Integer.MAX_VALUE));
        System.out.println(n.hammingWeight(Integer.MIN_VALUE));
        System.out.println(n.hammingWeight(11));
    }
}
