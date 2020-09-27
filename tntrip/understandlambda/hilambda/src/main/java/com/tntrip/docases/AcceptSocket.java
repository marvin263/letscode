package com.tntrip.docases;

import com.tntrip.tidyfile.DateUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class AcceptSocket implements EachCase {
    private static final AtomicLong SEQ = new AtomicLong(0);
    private static final Map<Socket, Long> SOCKET_MAP = Collections.synchronizedMap(new HashMap<>());
    //    private final ExecutorService es = new ThreadPoolExecutor(5, 5,
//            60, TimeUnit.SECONDS, new LinkedBlockingDeque<>(),
//            new NamedThreadFactory("accepted-socket"));
    private static final List<Socket> SOCKET_LIST = new ArrayList<>();

    @Override
    public void doOnLine(String line) {
        String actualLine = line.substring(prefix()[0].length());
        if (actualLine.equals("view")) {
            
            int size = SOCKET_LIST.size();
            try {
                SOCKET_LIST.get(0).getOutputStream().write("nihao 0".getBytes());
                System.out.println("[ServerSocket] Writing nihao first");

                SOCKET_LIST.get(size - 1).getOutputStream().write("nihao last".getBytes());
                System.out.println("[ServerSocket] Writing nihao " + size);
            } catch (IOException e) {
                e.printStackTrace();
            }
            
        } else {
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
    }

    @Override
    public String[] prefix() {
        return new String[]{"01", "Accept Socket"};
    }

    /**
     * os中添加下面两行：
     * sudo vim /etc/security/limits.conf
     * 
     * *    hard nofile  1006154
     * *    soft nofile  1006154
     * # End of file
     * 
     * @throws Exception
     */
    public void accept() throws Exception {
        int port = 16333;
        int backlog = 175;
        ServerSocket ss = new ServerSocket(port, backlog);
        System.out.println("[ServerSocket] Now listening at: " + ss);
        ss.setReuseAddress(true);
        while (true) {
            Socket socket = ss.accept();
            SOCKET_MAP.put(socket, SEQ.incrementAndGet());
            SOCKET_LIST.add(socket);
            System.out.println("[ServerSocket] Socket accepted--" + SOCKET_MAP.get(socket).intValue() + ", socket=" + socket);
            writeSocket(socket);
        }
    }

    public void writeSocket(Socket socket) {
        try {
            OutputStream os = socket.getOutputStream();
            String str = "Server Socket Time--" + SOCKET_MAP.get(socket) + ": " + DateUtil.date2String(new Date(), DateUtil.YYYY_MM_DD_HH_MM_SS_SSS);
            os.write(str.getBytes());
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readSocket(Socket socket) {
        try (InputStream is = socket.getInputStream()) {
            byte[] buf = new byte[256];
            int count;
            while ((count = is.read(buf)) != -1) {
                System.out.println(Thread.currentThread().getName() + "--Server Socket Received--" + SOCKET_MAP.get(socket) + ": " + new String(buf, 0, count));
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
