package com.tntrip.understand.targettyping;

import java.security.PrivilegedAction;
import java.util.concurrent.Callable;

/**
 * Created by nuc on 2015/11/19.
 */
public class TargetType {
    public static void sameLambdaDifferentType() throws Exception {
        // Callable 的一个实例
        Callable<String> c = () -> "done";
        
        // PrivilegedActioni 的一个实例
        PrivilegedAction<String> p = () -> "done";

        System.out.println(c.getClass());
        System.out.println(p.getClass());
    }

    public static void main(String[] args) throws Exception {
        sameLambdaDifferentType();
    }
}
