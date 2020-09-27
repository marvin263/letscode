package com.tntrip.docases;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AllocateInternedStringCase implements EachCase {
    private List<String> internedString = new ArrayList<>();

    @Override
    public void doOnLine(String line) {
        int expectedCount = Integer.parseInt(line.substring(prefix()[0].length()));
        HappyDoCases.keepLeftmostArrays(internedString, expectedCount, () -> {
            StringBuilder sb = new StringBuilder();
            while (sb.length() < (HappyDoCases.M / 2)) {
                sb.append(UUID.randomUUID().toString());
            }
            return sb.toString().intern();
        });
    }

    @Override
    public String[] prefix() {
        return new String[]{"str", "Intern String"};
    }
}
