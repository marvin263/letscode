package org.openjdk.jmh.samples;

/**
 * Created by nuc on 2016/12/8.
 */
public class UnsyncSB {
    StringBuilder sb = new StringBuilder();

    public int length() {
        return sb.length();
    }

    public StringBuilder append(String str) {
        return sb.append(str);
    }

    public StringBuilder delete(int start, int end) {
        return sb.delete(start, end);
    }
}
