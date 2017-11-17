package com.tntrip.understand.thegeneric;

/**
 * Created by libing2 on 2016/3/30.
 */
public class Cast2T {
    public <T> T pri(Class<T> t) {
        return (T) (new Integer(333));
    }
}
