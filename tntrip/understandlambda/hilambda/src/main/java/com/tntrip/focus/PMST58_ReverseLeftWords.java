package com.tntrip.focus;

public class PMST58_ReverseLeftWords {
    public String reverseLeftWords(String s, int n) {
        return s.substring(n) + s.substring(0, n);
    }

    public static void main(String[] args) {
        PMST58_ReverseLeftWords p = new PMST58_ReverseLeftWords();
        System.out.println(p.reverseLeftWords("abcdefg", 2));//cdefgab
        System.out.println(p.reverseLeftWords("lrloseumgh", 6));//umghlrlose
    }

}
