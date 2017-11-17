package com.tntrip.understand.exception;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

/**
 * Created by nuc on 2016/11/22.
 */
interface SupplierWithException<T> {
    T get() throws FileNotFoundException;
}

public class LambdaException {
    public static void main(String[] args) {
        List<Boolean> booleanList = Arrays.asList(Boolean.TRUE, Boolean.FALSE);
        int cnt = 0;
        for (Boolean aBoolean : booleanList) {
            cnt = aBoolean ? cnt++ : cnt;
        }



        System.out.println(cnt);
    }

    public static void aaaaa(String[] args) {
        execSupplier(new Supplier<String>() {
            @Override
            public String get() {
                try {
                    return readFile();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    return "error";
                }
            }
        });

        execSupplier(() -> {
            try {
                return readFile();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return "";
            }
        });

        //execSupplier(LambdaException::readFile);


        execSupplier(LambdaException::readFileNoException);
        //如果 FI中抛出异常，则，引用的方法 也可以抛出异常
        execSupplierWithException(LambdaException::readFile);
        execSupplierWithException(LambdaException::readFileNoException);
    }

    private static String readFile() throws FileNotFoundException {
        FileInputStream fis = new FileInputStream("");
        return fis.toString();
    }

    private static String readFileNoException() {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream("");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return "";
        }
        return fis.toString();
    }

    private static void execSupplier(Supplier<String> sp) {
        String str = sp.get();
        System.out.println(str);
    }

    private static void execSupplierWithException(SupplierWithException<String> sp) {
        String str = null;
        try {
            str = sp.get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(str);
    }
}
