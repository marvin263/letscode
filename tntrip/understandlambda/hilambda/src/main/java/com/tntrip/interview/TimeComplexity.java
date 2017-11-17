package com.tntrip.interview;

/**
 * Created by libing2 on 2016/10/13.
 */
public class TimeComplexity {
    int foo(int n) {
        if (n <= 1) {
            return 1;
        }
        return n * foo(n - 1);
    }
}
