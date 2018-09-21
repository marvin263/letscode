package com.tntrip.interview;

import java.util.Arrays;
import java.util.Random;

public class FindInteger {
    private static final int BITS_COUNT = 0x01 << 27;
    private static final int[] BITS = new int[BITS_COUNT];

    private static void setBit(int value) {
        // 去掉最前面的5位
        int slotIndex = 0x07ffffff & value;

        // <code>value</code> 处于 slot中存放的int值的哪一个bit上（最右侧bit位置是0，最左侧bit位置是31）
        // posInsideSlot的值是 [0, 31]中的某个值
        int posInsideSlot = value >>> 27;

        // 把 slot中存放的int值的 posInsideSlot位置的bit 设置成1
        int reservedBit = (1 << posInsideSlot);

        int newSlotValue = BITS[slotIndex] | reservedBit;
        BITS[slotIndex] = newSlotValue;
    }

    // 0-->0, 1-->1
    private static int getBit(int value) {
        // 去掉最前面的5位
        int slotIndex = 0x07ffffff & value;

        // <code>value</code> 处于 slot中存放的int值的哪一个bit上（最右侧bit位置是0，最左侧bit位置是31）
        // posInsideSlot的值是 [0, 31]中的某个值
        int posInsideSlot = value >>> 27;


        // 把 slot中存放的int值的 posInsideSlot位置的bit值，移动到最右侧
        int curSlotValue = BITS[slotIndex];
        curSlotValue = curSlotValue >>> posInsideSlot;
        // 取得最后一个bit的值
        return curSlotValue & 1;
    }


    public static void main(String[] args) {
        int valueCount = 10;
        Random rdm = new Random();
        int[] tmp = new int[]{-1};
        int[] existed = rdm.ints(valueCount, 0, Integer.MAX_VALUE).map(e -> {
            tmp[0] = tmp[0] + 1;
            return tmp[0] % 2 == 0 ? 0 - e : e;
        }).toArray();

        int[] nonExisted = Arrays.stream(existed).map(e -> e + 1).toArray();
        //int[] nonExisted = rdm.ints(valueCount, 0, Integer.MAX_VALUE).toArray();
        for (int value : existed) {
            setBit(value);
        }
        for (int i = 0; i < existed.length; i++) {
            System.out.println("existed: " + existed[i] + " --> " + getBit(existed[i]) + ",    " + "nonExisted: " + nonExisted[i] + " --> " + getBit(nonExisted[i]));
        }
    }

}
