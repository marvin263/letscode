package com.tntrip.docases;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class ReadWriteSocket implements EachCase {
    public static class LetsSocket {
        private OutputStreamWriter osw;
        private volatile boolean broken = false;

        public LetsSocket(String host, int port) {
            try {
                Socket s = new Socket(host, port);
                InputStreamReader isr = new InputStreamReader(s.getInputStream());
                new Thread(() -> {
                    try {
                        readSocket(isr);
                        markAsBroken();
                    } catch (Exception e) {
                        e.printStackTrace();
                        markAsBroken();
                    }
                }).start();
                osw = new OutputStreamWriter(s.getOutputStream());
            } catch (IOException e) {
                throw new RuntimeException();
            }
        }

        private void markAsBroken() {
            broken = true;
        }

        public boolean isBroken() {
            return broken;
        }

        private void readSocket(InputStreamReader isr) throws Exception {
            char[] buf = new char[256];
            int round = 0;
            while (true) {
                if (broken) {
                    break;
                }
                int count = isr.read(buf);
                if (count == -1) {
                    System.out.println("round=" + (++round) + ", count=" + count + "--------the end of the stream has been reached. Done");
                    break;
                }
                System.out.println("round=" + (++round) + ", count=" + count + "--------" + new String(buf, 0, count));
            }
            System.out.println("read done");
        }

        public void writeSocket(String str) {
            if (broken) {
                return;
            }
            try {
                if (str == null || str.length() == 0) {
                    osw.write("\r\n");
                } else if (str.startsWith("1")) {
                    osw.write(str.substring(1));
                } else {
                    osw.write(str + "\r\n");
                }
                osw.flush();
            } catch (IOException e) {
                e.printStackTrace();
                markAsBroken();
            }
        }
    }

    private LetsSocket ls = null;
    //new LetsSocket("192.168.0.7", 8080);

    @Override
    public void doOnLine(String line) {
        String actualLine = line.substring(prefix()[0].length());
        if (ls.isBroken()) {
            ls = new LetsSocket("192.168.0.7", 8080);
            System.out.println("Create socket done. ls=" + ls);
        }
        ls.writeSocket(actualLine);
    }

    @Override
    public String[] prefix() {
        return new String[]{"w", "Write socket"};
    }
}
