package com.tntrip.understand.thegeneric;

import java.net.InetAddress;

/**
 * Created by libing2 on 2016/3/29.
 */
public class Main {
    public <T> void testCast2T(Class<T> t) {
        System.out.println(new Cast2T().<T>pri(t).getClass());
    }

    public static void main(String[] args) throws Exception{
        Main m = new Main();
        m.testCast2T(String.class);
        InetAddress inet = InetAddress.getLocalHost();
        System.out.println("本机的ip=" + inet.getHostAddress());
    }
}

