package com.tntrip.understand.thegeneric._131_recover_element_type;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by libing2 on 2016/3/29.
 */

interface Contained {
}

interface Container<T extends Contained> {
    void add(T element);

    List<T> elements();
    Class<T> getElementType();
}

class MyContained implements Contained {
}

class MyContainer implements Container<MyContained> {
    List<MyContained> _elements = new ArrayList<>();

    @Override
    public void add(MyContained element) {
        _elements.add(element);
    }

    @Override
    public List<MyContained> elements() {
        return _elements;
    }

    @Override
    public Class<MyContained> getElementType() {
        return null;
    }
}

public class MetaContainer {
    private Container<? extends Contained> container;

    public void setContainer(Container<? extends Contained> container) {
        this.container = container;
    }

    public void add(Contained element) {
        //container.add(container.getElementType().cast(element));
    }
    private static <T extends Contained> void _add(Container<T> container, Contained element){
        //container.add(element);
    }
    public List<? extends Contained> elements() {
        return container.elements();
    }
}
