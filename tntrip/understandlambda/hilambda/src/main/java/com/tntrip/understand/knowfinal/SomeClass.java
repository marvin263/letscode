package com.tntrip.understand.knowfinal;

import java.util.LinkedHashMap;

/**
 * Created by libing2 on 2016/2/23.
 */
public class SomeClass {
    private final LinkedHashMap map = new LinkedHashMap();

    public SomeClass() {
        init();
    }

    private void init() {
        // just create objects and map.put() --
        // no tricks here, no this escaping, etc
    }

    // A read method
    public Object get(Object key) {
        return map.get(key);
    }
    // etc…
}
