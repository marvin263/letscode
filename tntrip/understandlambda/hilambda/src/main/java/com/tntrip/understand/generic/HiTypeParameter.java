package com.tntrip.understand.generic;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.EnumMap;
import java.util.concurrent.TimeUnit;

/**
 * Created by nuc on 2016/6/10.
 */
public class HiTypeParameter {
    public static void printTypeParameter() {
        Object obj = new EnumMap<TimeUnit, Number>(TimeUnit.class);
        Class<?> clazz = obj.getClass();
        TypeVariable<?>[] params = clazz.getTypeParameters();
        if (params.length == 0) {
            System.out.println(clazz + " is a NON-GENERIC TYPE");
            return;
        }

        System.out.println(clazz + " is a GENERIC TYPE with " + params.length + " type parameters\n");
        for (TypeVariable<?> aTV : params) {
            System.out.println(aTV + " is a TYPE PARAMETER");
            System.out.println(aTV.getName() + " is type parameter of generic declaration: " + aTV.getGenericDeclaration());

            Type[] bounds = aTV.getBounds();
            for (Type b : bounds) {
                System.out.println("\t" + b + "\n");
            }
        }
    }

    public static void main(String[] args) {
        printTypeParameter();
    }
}
