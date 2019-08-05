package com.tntrip;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


public class MyClassLoader extends ClassLoader {
    private static final AtomicInteger IDX = new AtomicInteger(0);

    public MyClassLoader() {
        System.out.println("Creating ClassLoader: " + IDX.getAndIncrement());
    }

    //用于寻找类文件
    @Override
    public Class findClass(String name) {
        byte[] b = loadClassData(name);
        return defineClass(name, b, 0, b.length);
    }

    //用于加载类文件
    private byte[] loadClassData(String name) {
        //包名转成文件路径
        String path = pathForClassName(name);
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(path);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            int len = 0;
            byte[] buffer = new byte[1024];
            while ((len = fis.read(buffer)) != -1) {
                bos.write(buffer, 0, len);
            }
            bos.flush();
            return bos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    private String pathForClassName(String name) {
        return Thread.currentThread().getContextClassLoader().getResource("").getPath() + name.replace(".", File.separator) + ".class";
    }

    public static void main(String[] args) {
        List<Class> d = new ArrayList<>();
        while (true) {
            MyClassLoader m = new MyClassLoader();
            Class c = m.findClass("com.tntrip.HelloWorld");
            d.add(c);
            System.out.println(c.getClassLoader());
        }
    }
}
