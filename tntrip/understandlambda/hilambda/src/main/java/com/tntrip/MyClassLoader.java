package com.tntrip;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class MyClassLoader extends ClassLoader {

    private String path;   //类的加载路径

    public MyClassLoader(String path) {
        this.path = path;
    }


    //用于寻找类文件
    @Override
    public Class findClass(String name) {
        byte[] b = loadClassData(name);
        return defineClass(name, b, 0, b.length);
    }

    //用于加载类文件
    private byte[] loadClassData(String name) {

        String fullPath = path + "\\" + name.replaceAll("\\.", "\\\\") + ".class";
        //使用输入流读取类文件
        InputStream in = null;
        //使用byteArrayOutputStream保存类文件。然后转化为byte数组
        ByteArrayOutputStream out = null;
        try {
            in = new FileInputStream(new File(fullPath));
            out = new ByteArrayOutputStream();
            int i = 0;
            while ((i = in.read()) != -1) {
                out.write(i);
            }

        } catch (Exception e) {
        } finally {
            try {
                out.close();
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return out.toByteArray();
    }

    public static void main(String[] args) {
        List<Class> d = new ArrayList<>();
        while (true) {
            MyClassLoader m = new MyClassLoader("D:\\eden\\gitworkspace\\letscode\\tntrip\\understandlambda\\hilambda\\build\\classes\\main\\");
            Class c = m.findClass("com.tntrip.HelloWorld");
            d.add(c);
            System.out.println(c.getClassLoader());
        }
    }
}
