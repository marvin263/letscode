package com.tntrip.docases;

import com.tntrip.tidyfile.DateUtil;
import com.tntrip.understand.asynchc.NamedThreadFactory;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class AcceptSocket implements EachCase {
    private static final AtomicLong SEQ = new AtomicLong(0);
    private static final Map<Socket, Long> SOCKET_MAP = Collections.synchronizedMap(new HashMap<>());
    private final ExecutorService es = new ThreadPoolExecutor(5, 5,
            60, TimeUnit.SECONDS, new ArrayBlockingQueue<>(10),
            new NamedThreadFactory("accepted-socket"));

    @Override
    public void doOnLine(String line) {
        try {
            new Thread(() -> {
                try {
                    accept();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        } catch (Exception e) {
            System.out.println("Error when accept");
            e.printStackTrace();
        }
    }

    @Override
    public String[] prefix() {
        return new String[]{"01", "Accept Socket"};
    }

    public void accept() throws Exception {
        int port = 16333;
        int backlog = 175;
        ServerSocket ss = new ServerSocket(port, backlog);
        System.out.println("Now listening at: " + ss);
        ss.setReuseAddress(true);
        while (true) {
            Socket socket = ss.accept();
            SOCKET_MAP.put(socket, SEQ.incrementAndGet());
            es.submit(() -> readWriteSocket(socket));
        }
    }

    public void readWriteSocket(Socket socket) {
        try (OutputStream os = socket.getOutputStream(); InputStream is = socket.getInputStream()) {
            byte[] buf = new byte[256];
            int count;
            while ((count = is.read(buf)) != -1) {
                System.out.println(Thread.currentThread().getName() + "--Server Socket Received--" + SOCKET_MAP.get(socket) + ": " + new String(buf, 0, count));

                String str = "Server Socket Time--" + SOCKET_MAP.get(socket) + ": " + DateUtil.date2String(new Date(), DateUtil.YYYY_MM_DD_HH_MM_SS_SSS);
                os.write(str.getBytes());
                os.flush();
            }
            System.out.println(SOCKET_MAP.get(socket) + "--Eof");
        } catch (Exception e) {
            System.out.println(SOCKET_MAP.get(socket) + "--Exception");
            e.printStackTrace();
        } finally {
            SOCKET_MAP.remove(socket);
        }
    }
}
