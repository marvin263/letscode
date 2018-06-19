package org.apache.activemq.book.ch13;

import java.util.Objects;
import java.util.Scanner;

public abstract class ReadConsoleAndRun {
    protected Object runRst;

    public void endlesslyReadConsoleAndRun() {
        Scanner scn = new Scanner(System.in);
        while (true) {
            String line = scn.nextLine();
            if ("quit".equals(line)) {
                System.out.println("quit");
                System.out.println();
                break;
            }
            if ("gc".equals(line)) {
                System.gc();
                System.out.println("gc done");
                System.out.println();
                continue;
            }
            runRst = runWithConsoleInput(line);
            System.out.println("Accept: " + line + " --> " + Objects.toString(runRst, "null"));
        }
    }

    protected abstract Object runWithConsoleInput(String line);

    public static void startMain(ReadConsoleAndRun rcr) {
        rcr.endlesslyReadConsoleAndRun();
    }
}
