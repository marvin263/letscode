package com.tntrip;

public class P44_WildcardMatching {
    public boolean isMatch(String s, String p) {
        return false;
    }


    public static void main(String[] args) {
        P44_WildcardMatching p = new P44_WildcardMatching();
        System.out.println(p.isMatch("aa", "a"));//false
        System.out.println(p.isMatch("aa", "*"));//true
        System.out.println(p.isMatch("cb", "?a"));//false
        System.out.println(p.isMatch("adceb", "*a*b"));//true
        System.out.println(p.isMatch("acdcb", "a*c?b"));//false
    }

}
