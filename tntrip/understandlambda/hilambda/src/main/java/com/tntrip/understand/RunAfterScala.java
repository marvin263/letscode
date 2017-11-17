package com.tntrip.understand;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by nuc on 2016/7/6.
 */
public class RunAfterScala {
    public static final Pattern PTN = Pattern.compile("(\\d{4}-\\d{2}-\\d{2}\\s\\d{2}:\\d{2}:\\d{2},\\d{3}).*?distributeLotteryAward\\s(.+)\\s.*?\"request_id\":\"(.+?)\"", Pattern.DOTALL);

    public static class EachLine {
        public long ts = 0L;
        public String requestId = null;
        private boolean isReq = false;
    }

    public static class CompleteRequestResponse {
        public long reqStart = 0L;
        public long rspEnd = 0L;
        public String requestId = null;

        public long getCost() {
            return rspEnd - reqStart;
        }

        public boolean hasBeenDone() {
            return reqStart != 0L && rspEnd != 0L;
        }
    }


    public static class FinalResult {
        public CompleteRequestResponse minByfar = null;
        public CompleteRequestResponse maxByfar = null;
        public long totalCost = 0L;
        public long totalRequestCount = 0L;
    }

    public static FinalResult processFileToGetFinalResult(String fullPath) throws Exception {
        long begin = System.currentTimeMillis();
        FinalResult fr = new FinalResult();
        Map<String, CompleteRequestResponse> map = new HashMap<>();
        Scanner scanner = new Scanner(new File(fullPath), "UTF-8");
        while (scanner.hasNextLine()) {
            String strLine = scanner.nextLine();
            EachLine el = parseFileLine(strLine);
            if (el == null) {
                continue;
            }
            processRequestResponse(map, el, fr);
        }
        System.out.println("Cost(ms): " + (System.currentTimeMillis() - begin));
        return fr;
    }

    private static EachLine parseFileLine(String strLine) {
        if (strLine == null) {
            return null;
        }
        Matcher matcher = PTN.matcher(strLine);
        while (matcher.find()) {
            String strtime = matcher.group(1);
            String reqOrRsp = matcher.group(2);
            String requestId = matcher.group(3);
            EachLine el = new EachLine();
            el.ts = strtime2long(strtime);
            el.requestId = requestId;
            el.isReq = reqOrRsp.equals("request");
            return el;
        }
        return null;
    }

    private static long strtime2long(String strtime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS");
        try {
            long millin = sdf.parse(strtime).getTime();
            return millin;
        } catch (ParseException e) {
            e.printStackTrace();
            return -1L;
        }
    }

    private static void processRequestResponse(Map<String, CompleteRequestResponse> map, EachLine el, FinalResult fr) {
        if (!map.containsKey(el.requestId)) {
            map.put(el.requestId, new CompleteRequestResponse());
        }

        CompleteRequestResponse completeRequestResponse = map.get(el.requestId);
        completeRequestResponse.requestId = el.requestId;

        // request
        if (el.isReq) {
            completeRequestResponse.reqStart = el.ts;
        } else {
            completeRequestResponse.rspEnd = el.ts;
        }

        if (completeRequestResponse.hasBeenDone()) {
            setupByOneRequest(completeRequestResponse, fr);
        }
    }

    private static void setupByOneRequest(CompleteRequestResponse completeRequestResponse, FinalResult fr) {
        long cost = completeRequestResponse.getCost();

        if (fr.minByfar == null) {
            fr.minByfar = completeRequestResponse;
        } else {
            if (fr.minByfar.getCost() > cost) {
                fr.minByfar = completeRequestResponse;
            }
        }
        if (fr.maxByfar == null) {
            fr.maxByfar = completeRequestResponse;
        } else {
            if (fr.maxByfar.getCost() < cost) {
                fr.maxByfar = completeRequestResponse;
            }
        }

        fr.totalRequestCount++;
        fr.totalCost += cost;
    }


    private static void outputFinalResult(FinalResult fr) {
        System.out.println("最小时间(ms)：" + fr.minByfar.getCost() + ", request_id:" + fr.minByfar.requestId);
        System.out.println("最长时间(ms)：" + fr.maxByfar.getCost() + ", request_id:" + fr.maxByfar.requestId);
        System.out.println("总请求时长(ms):" + fr.totalCost + ", 请求次数：" + fr.totalRequestCount + ", 平均时长(ms)：" + (fr.totalCost / fr.totalRequestCount));
    }

    public static void main(String[] args) throws Exception {
        FinalResult fr = processFileToGetFinalResult("D:/gateway.log");
        outputFinalResult(fr);
    }
}
