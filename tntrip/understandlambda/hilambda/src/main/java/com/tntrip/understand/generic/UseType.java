package com.tntrip.understand.generic;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import java.lang.reflect.Type;
import java.net.IDN;

/**
 * Created by libing2 on 2016/6/10.
 */
public class UseType {
    private static String getIdNameRsp() {
        ApiResponse<IdName> rsp = new ApiResponse<>();
        rsp.setup(true, "IdName 2 String", 0, IdName.create(1, "Tom"));
        return JSON.toJSONString(rsp);
    }

    private static String getCustomersRsp() {
        ApiResponse<Customers> rsp = new ApiResponse<>();
        rsp.setup(true, "IdName 2 String", 0,
                Customers.create(new IdName[]{IdName.create(1, "Tom"), IdName.create(2, "Jerry")}));
        return JSON.toJSONString(rsp);
    }



    public static void printStrings() {
        System.out.println(getIdNameRsp());
        System.out.println(getCustomersRsp());

        ApiResponse<IdName> a = JSON.parseObject(getIdNameRsp(), new TypeReference<ApiResponse<IdName>>(){}.getType());
        ApiResponse<String> c = JSON.parseObject(getCustomersRsp(), new TypeReference<ApiResponse<Customers>>(){}.getType());

        ApiResponse<Customers> c2 = JSON.parseObject(getCustomersRsp(), new TypeReference<ApiResponse<Customers>>(){});
        System.out.println(c2);
    }

    public static void main(String[] args) {
        printStrings();
    }
}
