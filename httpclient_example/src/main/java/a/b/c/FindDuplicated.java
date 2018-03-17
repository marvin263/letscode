package a.b.c;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class FindDuplicated {
    public static final int DEEPEST_PAGE = 20;
    public static final CloseableHttpClient httpclient = HttpClients.createDefault();
    // http://api.tuniu.com/discovery/newHome/getWaterfallFlow?c={"v":"9.33.0","cc":"40169"}&d={"resType":"recommend","imgH":0,"deviceNum":"f4fea0623d1037a20846255fb32cfdf12f13dfec","sessionId":"d","limit":10,"page":1,"locationPoiId":"40169","imgW":640}
    public static final String URL_WITHOUT_QUERY_STRING = "http://api.tuniu.com/discovery/newHome/getWaterfallFlow";
    public static final String PARAM_C = "{\"v\":\"9.33.0\",\"cc\":\"40169\"}";
    public static final String PARAM_D = "{\"resType\":\"recommend\",\"imgH\":0,\"deviceNum\":\"f4fea0623d1037a20846255fb32cfdf12f13dfec\",\"sessionId\":\"d\",\"limit\":10,\"page\":$page,\"locationPoiId\":\"40169\",\"imgW\":640}";

    private static String getWholeURL(int page) {
        try {
            return URL_WITHOUT_QUERY_STRING + "?c=" + URLEncoder.encode(PARAM_C, "utf-8") + "&d=" + URLEncoder.encode(PARAM_D.replace("$page", page + ""), "utf-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String extractQueryJson(Map<String, Object> wholeRtn) {
        Map debugMap = (Map) ((Map) wholeRtn.get("data")).get("debugInfo");
        if (debugMap == null) {
            return "";
        }
        JSONArray listEachSearch = (JSONArray) debugMap.get("listEachSearch");
        String queryJson = (String) ((Map) (listEachSearch).get(0)).get("queryJson");
        String prettyQueryJson = JSON.toJSONString(JSON.parseObject(queryJson), SerializerFeature.PrettyFormat);
        return prettyQueryJson;
    }

    public static List<ContentInfo> extractContentInfo(Map<String, Object> wholeRtn) {
        List list = (List) ((Map) wholeRtn.get("data")).get("list");
        if (list == null) {
            return Collections.emptyList();
        }

        List<ContentInfo> listContentInfo = new ArrayList<>();
        for (Object o : list) {
            Map<String, Object> ele = (Map<String, Object>) o;
            Object contentInfo = ele.get("contentInfo");
            listContentInfo.add(JSON.parseObject(JSON.toJSONString(contentInfo), ContentInfo.class));
        }
        return listContentInfo;
    }

    private static Map<String, Object> httpGetContent(int page) throws Exception {
        String wholeURL = getWholeURL(page);
        HttpGet httpGet = new HttpGet(wholeURL);
        httpGet.addHeader(new BasicHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.167 Safari/537.36"));
        CloseableHttpResponse response = httpclient.execute(httpGet);
        try {
            HttpEntity entity = response.getEntity();
            String strReturn = EntityUtils.toString(entity);
            return JSON.parseObject(strReturn);
        } finally {
            response.close();
        }
    }

    public static final int NO_DATA_LEFT = -1;
    public static final int ALREADY_FOUND_DUPLICATED = -2;
    public static final int HIT_DEEPEST_PAGE = -3;

    private static int addPage2Round(int curPageNumber, EachRound eachRound, Map<String, Object> wholeRtn) {
        List<ContentInfo> listContentInfo = extractContentInfo(wholeRtn);
        String queryJson = extractQueryJson(wholeRtn);
        if (listContentInfo == null || listContentInfo.isEmpty()) {
            return NO_DATA_LEFT;//no data left, should not fetch next page
        } else {
            EachPage eachPage = EachPage.create(curPageNumber, listContentInfo, queryJson);
            boolean alreadyFoundDuplicated = eachRound.addPage(eachPage);
            if (alreadyFoundDuplicated) {
                return ALREADY_FOUND_DUPLICATED;//already found duplicated page
            } else {
                if (curPageNumber < DEEPEST_PAGE) {
                    return curPageNumber + 1;
                } else {
                    return HIT_DEEPEST_PAGE;//already explore deepest page, we must stop
                }
            }
        }
    }

    private static int launchARound(EachRound eachRound) throws Exception {
        int curPageNumber = 1;
        while (true) {
            String info = TnStringUtils.format("Fetching round={0}, pageNumber={1}", eachRound.getRound(), curPageNumber);
            System.out.println(info);
            Map<String, Object> wholeRtn = httpGetContent(curPageNumber);
            curPageNumber = addPage2Round(curPageNumber, eachRound, wholeRtn);
            if (curPageNumber < 0) {
                break;
            }
            Thread.sleep(3000);
        }
        return curPageNumber;
    }

    public static void main(String[] args) throws Exception {
        int launchResult = Integer.MAX_VALUE;
        while (launchResult != ALREADY_FOUND_DUPLICATED) {
            launchResult = launchARound(EachRound.create());
            System.out.println();
        }
    }
}
