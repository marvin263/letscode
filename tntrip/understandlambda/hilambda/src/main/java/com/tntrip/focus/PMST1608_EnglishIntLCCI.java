package com.tntrip.focus;

public class PMST1608_EnglishIntLCCI {
    public String numberToWords(int num) {
        return null;
    }

    public static void main(String[] args) {
        PMST1608_EnglishIntLCCI p = new PMST1608_EnglishIntLCCI();
        System.out.println(p.numberToWords(123));//One Hundred Twenty Three
        System.out.println(p.numberToWords(12345));//Twelve Thousand Three Hundred Forty Five

        //One Million Two Hundred Thirty Four Thousand Five Hundred Sixty Seven
        System.out.println(p.numberToWords(1234567));

        //One Billion Two Hundred Thirty Four Million Five Hundred Sixty Seven Thousand Eight Hundred Ninety One
        System.out.println(p.numberToWords(1234567891));
    }

}
