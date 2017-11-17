package com.tntrip.understand.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by nuc on 2015/12/13.
 */
public class ReflectUtils {

    public static void printMembers(Object obj) {
        Set<Method> methods = new LinkedHashSet<>();
        // 所有public method，包括继承的来的
        methods.addAll(Arrays.asList(obj.getClass().getMethods()));
        // 所有 public/protected/private/friendly method，但，不包括继承的来的
        methods.addAll(Arrays.asList(obj.getClass().getDeclaredMethods()));

        for (Method method : methods) {
            System.out.print(method);
            System.out.println("-----method synthetic: " + method.isSynthetic());
        }

        Set<Field> fields = new LinkedHashSet<>();
        // 所有public fields，包括继承的来的
        fields.addAll(Arrays.asList(obj.getClass().getFields()));
        // 所有 public/protected/private/friendly fields，但，不包括继承的来的
        fields.addAll(Arrays.asList(obj.getClass().getDeclaredFields()));
        System.out.println();
        for (Field field : fields) {
            System.out.print(field);
            System.out.println("-----field synthetic: " + field.isSynthetic());
        }
    }

    public static void doSerialize(Object obj, String fullPath) {
        try {
            FileOutputStream fos = new FileOutputStream(fullPath);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T doDeserialize(String fullPath) {
        try {
            FileInputStream fis = new FileInputStream(fullPath);

            ObjectInputStream is = new ObjectInputStream(fis);
            Object obj = is.readObject();
            return (T) obj;
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
