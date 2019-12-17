package com.tntrip;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class P401_BinaryWatch {
    public static final int N_000000 = 0X00;
    public static final int N_000001 = 0X01;
    public static final int N_000010 = 0X02;
    public static final int N_000100 = 0X04;
    public static final int N_001000 = 0X08;
    public static final int N_010000 = 0X10;
    public static final int N_100000 = 0X20;
    public static final int[] MASK = {
            N_000000,
            N_000001,
            N_000010,
            N_000100,
            N_001000,
            N_010000,
            N_100000
    };

    public static class EachPerm {
        int num;
        int consumedPos;

        public static EachPerm create(int num, int consumedPos) {
            EachPerm ep = new EachPerm();
            ep.num = num;
            ep.consumedPos = consumedPos;
            return ep;
        }
    }

    public List<String> readBinaryWatch(int num) {
        List<EachPerm>[] hTable = generateEachPermTable(4);
        List<EachPerm>[] mTable = generateEachPermTable(6);
        List<String> finalRst = new ArrayList<>();
        for (int hOn = 0; hOn <= 4 && hOn <= num; hOn++) {
            int mOn = Math.min(num - hOn, 6);
            List<EachPerm> hOnPerms = hTable[hOn];
            List<EachPerm> mOnPerms = mTable[mOn];

            List<String> hours = readable(hOnPerms, 11, false);
            List<String> minutes = readable(mOnPerms, 59, true);

            for (String hour : hours) {
                for (String minute : minutes) {
                    finalRst.add(hour + ":" + minute);
                }
            }
        }
        return finalRst;
    }

    private List<EachPerm>[] generateEachPermTable(int num) {
        if (num <= 0) {
            return new List[0];
        }

        List<EachPerm>[] array = new List[num + 1];
        array[0] = Collections.emptyList();
        List<EachPerm> initialList = new ArrayList<>(num);
        for (int i = num; i >= 1; i--) {
            initialList.add(EachPerm.create(MASK[i], i));
        }
        array[1] = initialList;

        for (int i = 2; i <= num; i++) {
            array[i] = generateNext(array[i - 1]);
        }
        return array;
    }

    private List<EachPerm> generateNext(List<EachPerm> prev) {
        List<EachPerm> nextList = new ArrayList<>();
        for (EachPerm ep : prev) {
            for (int i = ep.consumedPos - 1; i >= 1; i--) {
                EachPerm next = EachPerm.create(ep.num | MASK[i], i);
                nextList.add(next);
            }
        }
        return nextList;
    }

    private List<String> readable(List<EachPerm> perms, int lteqValue, boolean leadingZero) {
        List<String> strings = new ArrayList<>(perms.size() + 1);
        if (perms.size() == 0) {
            strings.add(leadingZero ? "00" : "0");
        } else {
            for (EachPerm e : perms) {
                if (e.num <= lteqValue) {
                    strings.add((leadingZero && e.num < 10) ? "0" + e.num : "" + e.num);
                }
            }
        }
        return strings;
    }

    public static void main(String[] args) {
        P401_BinaryWatch bw = new P401_BinaryWatch();
        List<String> strings = bw.readBinaryWatch(4);
        System.out.println(strings);
    }
}
