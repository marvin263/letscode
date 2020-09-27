package com.tntrip.docases;

import com.tntrip.TnNetUtil;

import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class CreateManyClientSockets implements EachCase {
    private static final AtomicLong SEQ = new AtomicLong(0);
    private static final Map<Socket, Long> SOCKET_MAP = Collections.synchronizedMap(new HashMap<>());

    public void prepareReadSocket(String host, int port) {
        try {
            final Socket s = new Socket(host, port);
            SOCKET_MAP.put(s, SEQ.incrementAndGet());
            System.out.println("[ClientSocket] Create client socket, SEQ=" + SEQ.get() + ", s=" + s);
            new OutputStreamWriter(s.getOutputStream()).write("Client socket: " + SEQ.get());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void doOnLine(String line) {
        String actualLine = line.substring(prefix()[0].length());
        String remoteIP = null;
        if (actualLine == null || actualLine.trim().equals("")) {
            try {
                remoteIP = TnNetUtil.getLocalIpAddr().get(0);
            } catch (UnknownHostException e) {
                e.printStackTrace();
                return;
            }
        } else {
            remoteIP = actualLine;
        }
        for (int i = 0; i < 25000; i++) {
            try {
                prepareReadSocket(remoteIP, 16333);
            } catch (Exception e) {
                System.out.println("Error when creating socket" + SEQ.get());
                e.printStackTrace();
                break;
            }
        }
    }

    @Override
    public String[] prefix() {
        return new String[]{"03", "Create many sockets"};
    }
}
