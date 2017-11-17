
package com.tntrip.interview;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;


public class TestJSONBug {

    public static void main(String[] args) {
//        String data1 = "{\"success\":true,\"msg\":\"success\",\"errorCode\":\"710000\",\"data\":" +
//                "{\"userId\":26545224,\"nickName\":\"whatever\",\"userName\":\"8018522731\",\"result\":true}}";
//        ApiResponse rsp1 = JSON.parseObject(data1, ApiResponse.class);
//        Object rsp1Data = rsp1.getData();
//        System.out.println(rsp1Data.getClass());

        String data2 = "{\"success\":true,\"msg\":\"success\",\"errorCode\":\"710000\",\"data\":" +
                "{\"userId\":26545224,\"nickName\":\"于龙飞\",\"userName\":\"8018522731\",\"result\":true}}";
        ApiResponse<UserAuthData> rsp2 = JSON.parseObject(data2, new TypeReference<ApiResponse<UserAuthData>>() {
        }.getType());
        UserAuthData rsp2Data = rsp2.getData();
        System.out.println(rsp2Data.getClass());
    }
}
