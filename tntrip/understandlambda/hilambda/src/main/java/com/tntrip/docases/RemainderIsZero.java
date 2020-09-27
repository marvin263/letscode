package com.tntrip.docases;

public class RemainderIsZero implements EachCase {
    @Override
    public void doOnLine(String line) {
        int n = Integer.parseInt(line.substring(prefix()[0].length()));
        int middle = n / 2;
        int count = 0;
        for (int i = 1; i <= middle; i++) {
            if (n % i == 0) {
                System.out.println(i);
                count++;
            }
        }
        System.out.println(n + "--> finds " + count + " factors");
    }

    @Override
    public String[] prefix() {
        return new String[]{"f", "Factors whose remainder is 0"};
    }
}
