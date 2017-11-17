package org.springframework.samples.mvc.beanload;

import org.springframework.stereotype.Service;

/**
 * Created by nuc on 2015/11/25.
 */
@Service
public class Sub2 implements Superclass {
    @Override
    public void display() {
        System.out.println("Sub2");
    }
}
