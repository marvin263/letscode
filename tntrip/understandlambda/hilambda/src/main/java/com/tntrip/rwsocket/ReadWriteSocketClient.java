package com.tntrip.rwsocket;

import com.tntrip.tidyfile.TnStringUtils;

import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class ReadWriteSocketClient {
    private volatile boolean broken = true;
    private volatile OutputStream os;

    public void prepareReadSocket(String host, int port) {
        try {
            new Thread(() -> {
                try {
                    Socket s = new Socket(host, port);
                    os = s.getOutputStream();
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

    public void sendRequestOnLine(String line) throws Exception {
        if (isBroken()) {
            try {
                //String host = TnNetUtil.getLocalIpAddr().get(0);
                //int port = 16333;
//                String host = "192.168.86.204";
//                int port = 8080;
                String host = "a.com";
                int port = 80;
                prepareReadSocket(host, port);
                Thread.sleep(2000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        doWriteSocket(line);
    }


    private static final int MAXIN = 1024;
    private static final int MAXOUT = 1024;
    protected ByteBuffer numArray = ByteBuffer.allocate(50);

    protected ByteBuffer input = ByteBuffer.allocate(MAXIN);
    protected ByteBuffer output = ByteBuffer.allocate(MAXOUT);

    public void doWriteSocket(String line) throws Exception {
        if (broken) {
            return;
        }

        if (line == null || TnStringUtils.isBlank(line)) {
            System.err.println("Input nothing, so do nothing, return");
            return;
        }


        // byte[] bytes = Files.readAllBytes(Paths.get("/home/marvin/eden/happydocases/req.txt"));
        byte[] bytes = Files.readAllBytes(Paths.get("d://req.txt"));
        //byte[] bytes = Files.readAllBytes(Paths.get(this.getClass().getResource("/req.txt").toURI()));
        boolean $mode = false;
        for (int i = 0; i < bytes.length; i++) {
            byte b = bytes[i];

            if (b == (byte) '$') {
                $mode = true;
                numArray.clear();
                continue;
            }

            if (!$mode) {
                if (b == '\r') {
                    continue;
                }
                if (b == '\n') {
                    output.put((byte) '\r');
                    output.put((byte) '\n');
                    continue;
                }
                output.put(b);
            } else {
                if (b == (byte) '{') {
                } else if (b == (byte) '}') {
                    $mode = false;

                    numArray.flip();
                    String strNum = new String(numArray.array(), 0, numArray.limit(), StandardCharsets.UTF_8);
                    int num = Integer.parseInt(strNum);
                    numArray.clear();

                    //写出
                    letwrite();
                    Thread.sleep(num);

                } else {
                    numArray.put(b);
                }
            }
        }

        //写出
        letwrite();
    }

    private void letwrite() {
        try {
            output.flip();
            if (output.hasRemaining()) {
                os.write(output.array(), 0, output.limit());
                String str = new String(output.array(), 0, output.limit(), StandardCharsets.UTF_8);
                System.out.print(str);
                os.flush();
            }
            output.clear();
        } catch (Exception e) {
            e.printStackTrace();
            markAsBroken();
        }
    }

    private void endlesslyRun() {
        System.out.println("Type to send request");
        Scanner scn = new Scanner(System.in);
        while (true) {
            String line = scn.nextLine();
            try {
                sendRequestOnLine(line);
            } catch (Exception e) {
                System.out.println("Exception happened. line=" + line);
                e.printStackTrace();
                break;
            }
        }
    }

    public static void main(String[] args) {
        ReadWriteSocketClient tnps = new ReadWriteSocketClient();
        tnps.endlesslyRun();
    }

}
