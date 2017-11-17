package com.tntrip.interview;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by libing2 on 2016/10/9.
 */
public class PrintList {
    List<String> list;

    public PrintList() {
        list = new ArrayList<>();
        list.add("One");
    }

    public void print() {
        addNewElement(list, "A");
        for (String s : list) {
            System.out.println(s);
        }
    }

    private void addNewElement(List<String> theList, String str) {
        theList = new ArrayList<>();
        theList.add(str);
    }

    public static void main(String[] args) {
        PrintList pl = new PrintList();
        pl.print();//???
    }
}
