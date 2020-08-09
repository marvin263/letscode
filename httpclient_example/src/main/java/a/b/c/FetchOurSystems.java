/*
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */
package a.b.c;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import javax.management.relation.RelationTypeNotFoundException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

/**
 * A example that demonstrates how HttpClient APIs can be used to perform
 * form-based logon.
 */
public class FetchOurSystems {
    private static CloseableHttpClient httpclient = HttpClients.custom().build();
    private static final String TREE_LIST_URL = "http://opscloud.aabbcc.cn/api_v1/cmdb/core/service_tree_list/";
    private static final String A_SYSTEM_URL = "http://opscloud.aabbcc.cn/api_v1/cmdb/core/system/${uid}/?uid=${uid}";

    private static final String[][] HEADERS = new String[][]{
            new String[]{"User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.89 Safari/537.36"},
            new String[]{"Accept", "*/*"},
            new String[]{"Accept-Encoding", "gzip, deflate, br"},
            new String[]{"Connection", "keep-alive"},
            new String[]{"Authorization", "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJkaXNwbGF5X25hbWUiOiJcdTY3NGVcdTUxNzUyIiwicm9sZXMiOlsiZGV2ZWxvcGVyIiwidGVzdF9yb2xlIl0sImV4cCI6MTU5Njk4OTMyMn0.Sm3SlZsvnyWd3wc_5IWqFBp-7SYhj6bHLnDlqML2fJI"},
            new String[]{"Referer", "http://opscloud.aabbcc.cn/"},
            new String[]{"Accept-Language", "zh-CN,zh;q=0.9"},
            new String[]{"Cookie", "_ga=GA1.2.312781715.1592309849; sensorsdata2015jssdkcross=%7B%22distinct_id%22%3A%22ab5ae94b-df1a-44be-9d8e-83df15605d28%22%2C%22first_id%22%3A%22172e4ed1d2d524-0e8de2a54948d7-31607402-1296000-172e4ed1d2faaa%22%2C%22props%22%3A%7B%22%24latest_traffic_source_type%22%3A%22%E7%9B%B4%E6%8E%A5%E6%B5%81%E9%87%8F%22%2C%22%24latest_search_keyword%22%3A%22%E6%9C%AA%E5%8F%96%E5%88%B0%E5%80%BC_%E7%9B%B4%E6%8E%A5%E6%89%93%E5%BC%80%22%2C%22%24latest_referrer%22%3A%22%22%7D%2C%22%24device_id%22%3A%22172e4ed1d2d524-0e8de2a54948d7-31607402-1296000-172e4ed1d2faaa%22%7D; csrftoken=sY6Oam7FutlofGnU11LeIMZGhLFWWqVcy0U5bzrNfrYr1eokr1k42iTaHimXLSjH"},
    };
    private static final String[][] COL_NAME = new String[][]{
            new String[]{"cname", "系统中文名"},
            new String[]{"name", "系统英文名"},
            new String[]{"business_cname", "系统所属业务中文名"},
            new String[]{"business", "系统所属业务英文名"},
            new String[]{"available_level", "系统可用级别"},

            new String[]{"dev_user", "研发对接人"},
            new String[]{"dev_leader_user", "研发负责人"},
            new String[]{"dev_manager_user", "研发经理"},
            new String[]{"system_type", "系统类型"},
            new String[]{"real_online_date", "系统上线时间"},
            new String[]{"ops_user", "运维负责人"},
            new String[]{"product_manager_user", "产品经理"},

    };

    private static List<String> getSysemUids() throws Exception {
        HttpGet httpget = new HttpGet(TREE_LIST_URL);
        for (String[] header : HEADERS) {
            httpget.addHeader(header[0], header[1]);
        }
        CloseableHttpResponse response1 = httpclient.execute(httpget);
        List<String> list = new ArrayList<>();
        try {
            HttpEntity entity = response1.getEntity();
            String rtn = EntityUtils.toString(entity);
            JSONObject jsonObject = JSON.parseObject(rtn);
            JSONArray deptArray = ((JSONObject) (jsonObject.getJSONArray("info").get(0))).getJSONArray("children");
            for (Object o : deptArray) {
                JSONObject dept = (JSONObject) o;
                if (dept.get("cname").equals("商城")) {
                    JSONArray childrenOfDeptArray = dept.getJSONArray("children");
                    for (Object o1 : childrenOfDeptArray) {
                        //"业务类"
                        JSONObject aChildOfDept = (JSONObject) o1;
                        JSONArray childrenOfTypeArray = aChildOfDept.getJSONArray("children");
                        for (Object o2 : childrenOfTypeArray) {
                            //"全球商品中心"
                            JSONObject aChildOfType = (JSONObject) o2;
                            JSONArray childrenOfSystemArray = aChildOfType.getJSONArray("children");
                            for (Object o3 : childrenOfSystemArray) {
                                //"商品定价中心"
                                JSONObject aChildOfSystem = (JSONObject) o3;
                                String uid = aChildOfSystem.getString("uid");
                                list.add(uid);
                            }
                        }
                    }


                }
            }
        } finally {
            response1.close();
        }
        System.out.println("Found " + list.size() + " systems");
        return list;
    }


    private static List<String> getEachSystemDetail(String uid) throws Exception {
        String systemFullUrl = getSystemUrl(uid);
        HttpGet httpget = new HttpGet(systemFullUrl);
        for (String[] header : HEADERS) {
            httpget.addHeader(header[0], header[1]);
        }
        CloseableHttpResponse response1 = httpclient.execute(httpget);
        List<String> list = new ArrayList<>();
        try {
            HttpEntity entity = response1.getEntity();
            String rtn = EntityUtils.toString(entity);

            JSONObject jsonObject = JSON.parseObject(rtn);
            JSONObject infoJsonObject = jsonObject.getJSONObject("info");
            for (String[] cols : COL_NAME) {
                String value = infoJsonObject.getString(cols[0]);
                value = value == null ? "" : value.trim();
                list.add(value);
            }
        } finally {
            response1.close();
        }
        return list;
    }


    private static String getSystemUrl(String uid) {
        return A_SYSTEM_URL.replaceAll("\\Q${uid}\\E", uid);
    }

    public static void main(String[] args) throws Exception {
        List<List<String>> list = new ArrayList<>();
        List<String> systemUids = getSysemUids();
        for (String uid : systemUids) {
            List<String> aSystemDetail = getEachSystemDetail(uid);
            list.add(aSystemDetail);
            System.out.println(aSystemDetail);
        }
        httpclient.close();
        StringBuilder sb = new StringBuilder();
        for (List<String> aSystemDetail : list) {
            for (String value : aSystemDetail) {
                sb.append(value+"\t");
            }
            sb.append("\r\n");
        }
        System.out.println(sb.toString());
    }
}
