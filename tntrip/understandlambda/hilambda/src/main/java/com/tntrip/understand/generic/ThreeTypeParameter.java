package com.tntrip.understand.generic;

import java.lang.reflect.Constructor;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

/**
 * Created by libing2 on 2016/6/10.
 */
public class ThreeTypeParameter<T1 extends Object & Comparable<T1> & Cloneable, T2> {
    public <C1, C2> ThreeTypeParameter(C1 a, C2 b, T1 c) {
    }

    /**
     * 注意：此 实例方法 并没有定义 type parameter
     *
     * @param a
     * @param b
     * @return
     */
    public T1 instanceMethod1(T1 a, T2 b) {
        return a;
    }

    public <I1, I2, IR> IR instanceMethod2(T1 a, T2 b, I1 c, I2 d, IR r) {
        return r;
    }

    public static <S1, S2, SR> SR staticMethod(S1 a, S2 b, SR r) {
        return r;
    }


    public static void testGenericDeclaration() throws Exception {
        TypeVariable<Class<ThreeTypeParameter>>[] classTV = ThreeTypeParameter.class.getTypeParameters();
        printTypeParameterInfos(classTV);

        TypeVariable<Constructor<ThreeTypeParameter>>[] constructorTV = ThreeTypeParameter.class.
                getConstructor(Object.class, Object.class, Object.class).getTypeParameters();
        printTypeParameterInfos(constructorTV);

        TypeVariable<Method>[] im1TV = ThreeTypeParameter.class.
                getMethod("instanceMethod1", Object.class, Object.class).getTypeParameters();
        TypeVariable<Method>[] im2TV = ThreeTypeParameter.class.
                getMethod("instanceMethod2", Object.class, Object.class, Object.class, Object.class, Object.class).getTypeParameters();
        TypeVariable<Method>[] staticMethodTV = ThreeTypeParameter.class.
                getMethod("staticMethod", Object.class, Object.class, Object.class).getTypeParameters();
        @SuppressWarnings("unchecked")
        TypeVariable<Method>[][] arrMethodTV = new TypeVariable[][]{im1TV, im2TV, staticMethodTV};
        for (TypeVariable<Method>[] mTV : arrMethodTV) {
            printTypeParameterInfos(mTV);
        }
    }

    public static void printTypeParameterInfos(TypeVariable<?>[] arrTV) {
        StringBuilder sb = new StringBuilder();
        for (TypeVariable<?> tv : arrTV) {
            GenericDeclaration gd = tv.getGenericDeclaration();
            System.out.println(tv + " is a TYPE PARAMETER of GenericDeclaration--" + gd);
            Type[] bounds = tv.getBounds();
            for (Type b : bounds) {
                System.out.println("\t" + b);
            }
        }
        System.out.println();
    }

    public static void main(String[] args) throws Exception {
        testGenericDeclaration();
    }
}
