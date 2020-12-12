package com.tntrip.docases;

//import com.tntrip.HelloWorld;

import com.tntrip.MyClassLoader;

import java.util.ArrayList;
import java.util.List;

public class AllocateMetaspaceCase implements EachCase {
    private Object[] objArray8 = new Object[8];
    private Object[] objArray9 = new Object[9];
    private Object[] objArray10 = new Object[10];
    private Object obj = new Object();

    private List<Object> myObjects = new ArrayList<>();

    public AllocateMetaspaceCase() {
        objArray10[0] = new byte[2 * 1024 * 1024];
        objArray10[1] = new byte[3 * 1024 * 1024];
    }

    @Override
    public void doOnLine(String line) {
        int expectedCount = Integer.parseInt(line.substring(prefix()[0].length()));
        HappyDoCases.keepLeftmostArrays(myObjects, expectedCount, () -> {
            Object obj = null;
            try {
                Class cls = new MyClassLoader().loadClass("com.tntrip.HelloWorld");
                obj = cls.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return obj;
        });
        //HappyDoCases.keepLeftmostArrays(metaspaceMemory, expectedCount, () -> new MyClassLoader().findClass("com.tntrip.HelloWorld"));
    }

    @Override
    public String[] prefix() {
        return new String[]{"mt", "Allocate metaspace"};
    }
}
