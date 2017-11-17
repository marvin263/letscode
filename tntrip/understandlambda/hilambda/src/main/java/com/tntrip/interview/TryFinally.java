package com.tntrip.interview;

/**
 * Created by libing2 on 2016/10/11.
 */
public class TryFinally {
    public static int ret() {
        try {
            int i = 1 / 0;
            return 0;
        } catch (Exception e) {
            return 1;
        } finally {
            return 2;
        }
    }


    public static void main(String[] args) {
        System.out.println(ret());
        int i = 1000;
        Integer iii = new Integer(1000);
        boolean bb = i == iii;
        boolean cc = iii == i;
        System.out.println(bb);
        System.out.println(cc);
    }
}
