package com.tntrip.focus;

public class P0044_WildcardMatching {
    public boolean isMatch(String s, String p) {
        return false;
    }


    public static void main(String[] args) {
        P0044_WildcardMatching p = new P0044_WildcardMatching();
        System.out.println(p.isMatch("aa", "a"));//false
        System.out.println(p.isMatch("aa", "*"));//true
        System.out.println(p.isMatch("cb", "?a"));//false
        System.out.println(p.isMatch("adceb", "*a*b"));//true
        System.out.println(p.isMatch("acdcb", "a*c?b"));//false
    }

}
