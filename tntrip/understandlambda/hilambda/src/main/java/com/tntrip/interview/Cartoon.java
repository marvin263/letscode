package com.tntrip.interview;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by libing2 on 2016/10/9.
 */
public class Cartoon {
    public static class Character {
        public final String name;
        public final String slogan;
        public Character(final String name, final String slogan){
            this.name = name;
            this.slogan = slogan;
        }
    }

    public static void putAndGet(){
        Map<Character, Integer> charaMap = new HashMap<>();

        Character xd = new Character("XiongDa", "Stop, GuangTouQiang");
        charaMap.put(xd, 1);
        charaMap.put(new Character("HuiTaiLang", "I'll be back"), 2);

        System.out.println(charaMap.get(xd)); //???
        System.out.println(charaMap.get(new Character("HuiTaiLang", "I'll be back"))); //???
    }

    public static void main(String[] args) {
        putAndGet();
    }
}
