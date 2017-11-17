package com.tntrip.understand.generic;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

/**
 * Created by libing2 on 2016/7/10.
 */
public class MyBean {

    public static <T extends Comparable<? super T>> T max(Collection<? extends T> coll) {
        Iterator<? extends T> i = coll.iterator();
        T candidate = i.next();

        while (i.hasNext()) {
            T next = i.next();
            if (next.compareTo(candidate) > 0)
                candidate = next;
        }
        return candidate;
    }

    public static void main(String[] args) {
        List<ScheduledFuture<?>> list = null;

        ScheduledFuture<?> maxSF = max(list);

    }



}
