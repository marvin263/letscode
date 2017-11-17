package com.tntrip.understand.generic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by libing2 on 2016/6/10.
 */
public class Customers {
    private List<IdName> customers = new ArrayList<>();

    public static Customers create(IdName[] arr) {
        Customers c = new Customers();
        c.setCustomers(Arrays.asList(arr));
        return c;
    }

    public List<IdName> getCustomers() {
        return customers;
    }

    public void setCustomers(List<IdName> customers) {
        this.customers = customers;
    }
}
