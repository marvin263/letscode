package com.tntrip.understand.generic;

import java.util.Date;

/**
 * Created by nuc on 2017/1/10.
 */
public class DoNotSuppressWarning {
    public static void main(String[] args) {
        ApiResponse<String> apiStr = new ApiResponse<>();
        apiStr.setData("I'm String");

        //ApiResponse<Date> apiDate1 = (ApiResponse<Date>)apiStr;



        ApiResponse<?> apiAnything = apiStr;

        ApiResponse<Date> apiDate2 = (ApiResponse<Date>)apiAnything;


        System.out.println(apiDate2);


        Date theDate = apiDate2.getData();
    }
}
