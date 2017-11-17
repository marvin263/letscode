package org.openjdk.jmh.samples;

/**
 * Created by nuc on 2016/12/8.
 */
public class SynchSB {
    StringBuilder sb = new StringBuilder();

    public synchronized int length() {
        return sb.length();
    }
    public synchronized StringBuilder append(String str) {
        return sb.append(str);
    }
    public synchronized StringBuilder delete(int start, int end) {
        return sb.delete(start, end);
    }
}
