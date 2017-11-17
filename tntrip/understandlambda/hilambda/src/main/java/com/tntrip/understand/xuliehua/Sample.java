package com.tntrip.understand.xuliehua;

import com.tntrip.understand.util.ReflectUtils;

import java.io.Serializable;

/**
 * Created by libing2 on 2015/12/14.
 */

interface SampleSerializableInterface extends Runnable, Serializable {
}

public class Sample implements Serializable{
    private String str;

    public String getStr() {
        return str;
    }

    public void setStr(String str) {
        this.str = str;
    }

    public static void serializeLambda() {
        Sample s = new Sample();
        s.setStr("Smallest");

        SampleSerializableInterface r = () -> {
            System.out.println("Hello " + s.getStr());
        };
        ReflectUtils.doSerialize(r, "Runnable.lambda");
        r.run();
        System.out.println(r.getClass());
        s.setStr("colobu");
    }

    public static void deserializeLambda() {
        SampleSerializableInterface r = ReflectUtils.doDeserialize("Runnable.lambda");
        r.run();
        System.out.println(r.getClass());
    }

    public static void main(String[] args) {
        serializeLambda();// Hello Smallest
        deserializeLambda();// Hello Smallest
    }
}
