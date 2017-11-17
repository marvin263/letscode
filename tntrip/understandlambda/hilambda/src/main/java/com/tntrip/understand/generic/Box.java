package com.tntrip.understand.generic;

/**
 * Created by libing2 on 2016/6/8.
 */
public class Box<T extends Comparable<T> & Cloneable>

        implements Comparable<Box<T>>, Cloneable {

    private T theObject;

    public Box(T arg) {
        theObject = arg;
    }

    public Box(Box<? extends Runnable> box) {
        box.theObject.run();
    }

    @Override
    public Box<T> clone() {
        try {
            @SuppressWarnings("unchecked")
            Box<T> b = (Box<T>) super.clone();
            return b;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int compareTo(Box<T> o) {
        return 0;
    }
}
