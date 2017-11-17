package com.tntrip.understand.generic;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.List;
import java.util.Set;

/**
 * Created by nuc on 2016/6/11.
 */
public class AnalyzeType {

    public static boolean isParameterizedType(Type type) {
        return type instanceof ParameterizedType;
    }

    /**
     * Only Class object provides the information whether the type in question has type parameters or not (is generic)
     * <p>
     * 只有 Class 对象才知道 此 type 是否有 type parameter（即 是否 generic type）
     *
     * @param clazz
     * @return
     */
    public static boolean isGenericType(Class<?> clazz) {
        return clazz.getTypeParameters().length > 0;
    }

    public static boolean isGenericType(Type type) {
        if (!(type instanceof Class)) {
            return false;
        }
        return isGenericType((Class) type);
    }

    public static <T> void testType(T t, Set<T> set, List<? extends Number> list) {
    }

    public static void analyzeType(Type type) {
        System.out.print("analyzeType, type : " + type);
        if (type instanceof Class) {
            // regular type. String, Date[]
            System.out.println(" is regular type");
        } else if (type instanceof ParameterizedType) {
            // List<String>, List<E>, List<? extends Number>
            System.out.println(" is ParameterizedType");
            ParameterizedType pt = (ParameterizedType) type;
            Type[] actualTypeArguments = pt.getActualTypeArguments();
            for (Type ata : actualTypeArguments) {
                analyzeType(ata);
            }

        } else if (type instanceof TypeVariable) {
            // T, E
            System.out.println(" is TypeVariable");
        } else if (type instanceof GenericArrayType) {
            // List<String>[], List<E>[], T[]
            System.out.println(" is GenericArrayType");
        } else if (type instanceof WildcardType) {
            // ?, ? extends Number, ? super Long
            System.out.println(" is WildcardType");
        } else {
            throw new InternalError("unknown type representation " + type);
        }
    }

    private static void printClass(Class<?> cls) {
    }

    private static void printParameterizedType(ParameterizedType pt) {
    }

    private static void printTypeVariable(TypeVariable<?> tv) {
    }

    private static void printGenericArrayType(GenericArrayType gat) {
    }

    private static void printWildcardType(WildcardType wt) {
    }

    public static void main(String[] args) throws Exception {
        Method m = AnalyzeType.class.getMethod("testType", Object.class, Set.class, List.class);
        Type[] genericParameterTypes = m.getGenericParameterTypes();
        for (Type gpt : genericParameterTypes) {
            analyzeType(gpt);
        }
    }
}
