package com.tntrip.docases;

public class ExitCase implements EachCase {
    @Override
    public void doOnLine(String line) {
        System.exit(0);
    }

    @Override
    public String[] prefix() {
        return new String[]{"exit", "Exit"};
    }
}
