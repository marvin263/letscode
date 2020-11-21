package com.tntrip.rwsocket;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;

public class TestNginxParameterSocket {
    private volatile boolean broken = true;
    private volatile OutputStreamWriter osw;

    public void prepareReadSocket(String host, int port) {
        try {
            new Thread(() -> {
                try {
                    Socket s = new Socket(host, port);
                    osw = new OutputStreamWriter(s.getOutputStream());
                    broken = false;
                    System.out.println("Create socket done. s=" + s);

                    doReadSocket(new InputStreamReader(s.getInputStream()));
                    markAsBroken();
                } catch (Exception e) {
                    e.printStackTrace();
                    markAsBroken();
                }
            }).start();

        } catch (Exception e) {
            markAsBroken();
            throw new RuntimeException(e);
        }
    }

    private void markAsBroken() {
        broken = true;
    }

    public boolean isBroken() {
        return broken;
    }

    private void doReadSocket(InputStreamReader isr) throws Exception {
        char[] buf = new char[256];
        int round = 0;
        while (true) {
            if (broken) {
                break;
            }
            int count = isr.read(buf);
            if (count == -1) {
                System.out.println(Thread.currentThread().getName() + "--Client socket received--round=" + (++round) + ", count=" + count + "--------End of Stream");
                break;
            }
            System.out.println(Thread.currentThread().getName() + "--Client socket received--round=" + (++round) + ", count=" + count + "--------" + new String(buf, 0, count));
        }
        System.out.println(Thread.currentThread().getName() + "--Done close client socket.");
    }

    public void doOnLine(String line) {
        if (isBroken()) {
            try {
                //String host = TnNetUtil.getLocalIpAddr().get(0);
                //int port = 16333;
//                String host = "192.168.86.204";
//                int port = 8080;
                String host = "a.com";
                int port = 80;
                prepareReadSocket(host, port);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        doWriteSocket(line, osw);
    }

    public void doWriteSocket(String actualLine, OutputStreamWriter osw) {
        if (broken) {
            return;
        }
        try {
            if (actualLine == null || actualLine.length() == 0) {
                osw.write("\r\n");
            } else if (actualLine.startsWith("1")) {
                osw.write(actualLine.substring(1));
            } else {
                osw.write(actualLine + "\r\n");
            }
            osw.flush();
        } catch (IOException e) {
            e.printStackTrace();
            markAsBroken();
        }
    }

    private void endlesslyRun() {
        Scanner scn = new Scanner(System.in);
        while (true) {
            System.out.println("Type your http request:\n");
            while (true) {
                String line = scn.nextLine();
                try {
                    doOnLine(line);
                } catch (Exception e) {
                    System.out.println("Exception happened. line=" + line);
                    e.printStackTrace();
                    break;
                }
            }
        }
    }

    public static void main(String[] args) {
        TestNginxParameterSocket tnps = new TestNginxParameterSocket();
        tnps.endlesslyRun();
    }

}
