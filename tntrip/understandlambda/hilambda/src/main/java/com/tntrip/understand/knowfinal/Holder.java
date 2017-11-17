package com.tntrip.understand.knowfinal;

import java.util.Random;

/**
 * Holder
 * <p>
 * Class at risk of failure if not properly published
 *
 * @author Brian Goetz and Tim Peierls
 */
public class Holder {
    private int n;

    public Holder(int n) {
        int i = 0;
        while(i < new Random().nextInt(800000)){
            this.n = n;
        }
    }

    public void assertSanity() {
        if (n != n)
            throw new AssertionError("This statement is false.");
    }
}
