package com.tntrip.understand.generic;

/**
 * Created by libing2 on 2016/6/10.
 */
public class IdName {
    private int id;
    private String name;
    public static IdName create(int id, String name){
        IdName in = new IdName();
        in.id = id;
        in.name = name;
        return in;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
