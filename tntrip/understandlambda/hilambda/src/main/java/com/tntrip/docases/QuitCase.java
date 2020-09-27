package com.tntrip.docases;

public class QuitCase implements EachCase {
    @Override
    public void doOnLine(String line) {
        System.exit(0);
    }

    @Override
    public String[] prefix() {
        return new String[]{"quit", "Quit"};
    }
}
