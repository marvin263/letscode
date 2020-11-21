package com.tntrip.docases;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class ReadWriteSocket implements EachCase {
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

    @Override
    public void doOnLine(String line) {
        String actualLine = line.substring(prefix()[0].length());
        if (isBroken()) {
            try {
                //String host = TnNetUtil.getLocalIpAddr().get(0);
                //int port = 16333;
                String host = "127.0.0.1";
                int port = 10393;
//                String host = "a.com";
//                int port = 80;
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
        doWriteSocket(actualLine, osw);
    }

    @Override
    public String[] prefix() {
        return new String[]{"02", "Write socket"};
    }
}
