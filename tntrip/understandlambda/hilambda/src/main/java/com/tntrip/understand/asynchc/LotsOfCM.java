package com.tntrip.understand.asynchc;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.nio.reactor.ConnectingIOReactor;
import org.apache.http.nio.reactor.IOReactorException;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by nuc on 2016/8/25.
 */
public class LotsOfCM {
    public static final String URL_163 = "http://war.163.com/16/0825/10/BVABSAVQ000181KT.html?";
    public static CloseableHttpAsyncClient httpclient = create();
    public static ExecutorService es = Executors.newFixedThreadPool(120, new NamedThreadFactory("commit-task"));

    private static CloseableHttpAsyncClient create() {
        try {
            int maxtotal = 5000;
            int dp = 1000;
            ConnectingIOReactor ior = new DefaultConnectingIOReactor(IOReactorConfig.custom().
                    setIoThreadCount(4).
                    build());
            PoolingNHttpClientConnectionManager cm = new PoolingNHttpClientConnectionManager(ior);
            cm.setDefaultMaxPerRoute(100);
            cm.setMaxTotal(500);
            CloseableHttpAsyncClient httpclient = HttpAsyncClients.custom().setConnectionManager(cm).build();
            return httpclient;
        } catch (IOReactorException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void launchInvocation() {
        int count = 10000;
        for (int i = 0; i < count; i++) {
            int x = i;
            es.submit(() -> invokeEach(x));
        }
    }

    private static void invokeEach(int sequence) {
        long begin = System.currentTimeMillis();
        String rst = null;
        rst = callUrl(sequence);
        String finalRst = (rst == null || rst.trim().isEmpty()) ? "null" : "good";
        System.out.println(sequence + " " + finalRst + ", cost: " + (System.currentTimeMillis() - begin) + ", " + Thread.currentThread().getName());
    }

    public static String callUrl(int sequence) {
//        CloseableHttpAsyncClient httpclient = HttpAsyncClients.createDefault();
        Future<HttpResponse> future = null;
        String rst = null;
        try {
            httpclient.start();
            HttpGet request = new HttpGet(URL_163 + sequence);
            future = httpclient.execute(request, null);
            HttpResponse response = future.get(5, TimeUnit.SECONDS);
            rst = EntityUtils.toString(response.getEntity());
            System.out.println(rst);
        } catch (InterruptedException | ExecutionException | TimeoutException | IOException e) {
            e.printStackTrace();
            //future.cancel(true);
            httpclient = create();
        } finally {
            try {
//                httpclient.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return rst;
    }

    public static void main(String[] args) {
        launchInvocation();
    }
}
