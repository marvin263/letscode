package com.tntrip.understand.grammar;

import java.util.Random;

/**
 * Created by nuc on 2015/11/28.
 */
public class UTF8Representation {
    public static final int ONE_BYTE = 0x7F; //127
    public static final int TWO_BYTE = 0x07FF; //2047
    public static final int THREE_BYTE = 0xFFFF; //65535
    public static final int FOUR_BYTE = 0x1FFFFF; //2097151


    //通过 OR
    public static final int FIRST_BIT_TO_1_ON_RIGHT_0_BYTE = 0x80;
    public static final int FIRST_BIT_TO_1_ON_RIGHT_1_BYTE = 0x8000;
    public static final int FIRST_BIT_TO_1_ON_RIGHT_2_BYTE = 0x800000;
    public static final int FIRST_BIT_TO_1_ON_RIGHT_3_BYTE = 0x80000000;

    // 通过 AND
    public static final int SECOND_BIT_TO_0_ON_RIGHT_0_BYTE = 0xBF;
    public static final int SECOND_BIT_TO_0_ON_RIGHT_1_BYTE = 0xBFFF;
    public static final int SECOND_BIT_TO_0_ON_RIGHT_2_BYTE = 0xBFFFFF;
    public static final int SECOND_BIT_TO_0_ON_RIGHT_3_BYTE = 0xBFFFFFFF;

    // 通过 AND
    public static final int RIGHT_0_BYTE_TO_0 = 0xFFFFFF00;
    public static final int RIGHT_01_BYTE_TO_0 = 0xFFFF0000;
    public static final int RIGHT_012_BYTE_TO_0 = 0xFF000000;
    public static final int RIGHT_0123_BYTE_TO_0 = 0x00000000;

    // 通过 AND
    public static final int LEFT_0_BYTE_TO_0 = 0x00FFFFFF;
    public static final int LEFT_01_BYTE_TO_0 = 0x0000FFFF;
    public static final int LEFT_012_BYTE_TO_0 = 0x00000FF;
    public static final int LEFT_0123_BYTE_TO_0 = 0x00000000;

    public static final int LEADING_BYTE_FOR_2_BYTES_UTF8 = 0xC000;
    public static final int LEADING_BYTE_FOR_3_BYTES_UTF8 = 0xE00000;
    public static final int LEADING_BYTE_FOR_4_BYTES_UTF8 = 0xF0000000;

    public static String codePoint2utf8(int codePoint) {
        // 0 -- 127
        if (codePoint >= 0 && codePoint <= ONE_BYTE) {
            return Integer.toHexString(codePoint).toUpperCase();
        }

        int right0 = ((codePoint & LEFT_012_BYTE_TO_0) | FIRST_BIT_TO_1_ON_RIGHT_0_BYTE)
                & SECOND_BIT_TO_0_ON_RIGHT_0_BYTE;

        // 128 -- 2047
        if (codePoint <= TWO_BYTE) {
            int leadingByteFor2Bytes = ((codePoint << 2) & LEFT_01_BYTE_TO_0 & RIGHT_0_BYTE_TO_0)
                    | LEADING_BYTE_FOR_2_BYTES_UTF8;

            return Integer.toHexString(leadingByteFor2Bytes | right0).toUpperCase();
        }

        int right1 = (((codePoint << 2) & LEFT_01_BYTE_TO_0 & RIGHT_0_BYTE_TO_0) | FIRST_BIT_TO_1_ON_RIGHT_1_BYTE)
                & SECOND_BIT_TO_0_ON_RIGHT_1_BYTE;
        // 2048 -- 65535
        if (codePoint <= THREE_BYTE) {

            int leadingByteFor3Bytes = ((codePoint << 4) & LEFT_0_BYTE_TO_0 & RIGHT_01_BYTE_TO_0)
                    | LEADING_BYTE_FOR_3_BYTES_UTF8;

            return Integer.toHexString(leadingByteFor3Bytes | right1 | right0).toUpperCase();
        }
        // 65536 -- 2097151
        if (codePoint <= FOUR_BYTE) {
            int right2 = (((codePoint << 4) & LEFT_0_BYTE_TO_0 & RIGHT_01_BYTE_TO_0) | FIRST_BIT_TO_1_ON_RIGHT_2_BYTE)
                    & SECOND_BIT_TO_0_ON_RIGHT_2_BYTE;

            int leadingByteFor4Bytes = ((codePoint << 6) & RIGHT_012_BYTE_TO_0)
                    | LEADING_BYTE_FOR_4_BYTES_UTF8;
            return Integer.toHexString(leadingByteFor4Bytes | right2 | right1 | right0).toUpperCase();
        } else {
            throw new RuntimeException();
        }
    }

    public static String utf8Representation(String str) {
        if (str == null || str.equals("")) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        char[] array = str.toCharArray();
        for (int i = 0; i < array.length; i++) {
            char c = array[i];
            int codePoint = Character.codePointAt(array, i);
            if (i == array.length - 1) {
                sb.append(Integer.toHexString(codePoint).toUpperCase() + "-->" + c + "-->" + codePoint2utf8(codePoint));
            } else {
                sb.append(Integer.toHexString(codePoint).toUpperCase() + "-->" + c + "-->" + codePoint2utf8(codePoint) + "\n");
            }
        }
        return sb.toString();
    }


    public static void main(String[] args) {
        System.out.println("\u10234");
        System.out.println(utf8Representation("abcd"));
        System.out.println(0x10FFFF);

        int[] cpArray = new int[100];
        for (int i = 0; i < cpArray.length; i++) {
            cpArray[i] = new Random().nextInt(0x10FFFF);
        }
        System.out.println(utf8Representation(new String(cpArray, 0, cpArray.length)));
    }
}
