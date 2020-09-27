package com.tntrip.docases;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

public class GetPidCase implements EachCase {
    @Override
    public void doOnLine(String line) {
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        System.out.println(runtimeMXBean.getName().split("@")[0]);
        System.out.println();
    }

    @Override
    public String[] prefix() {
        return new String[]{"p", "Get pid"};
    }
}
