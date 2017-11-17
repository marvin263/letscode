package examples.chapter07;
import java.net.*;

public class URLEncoderExample {

  public static void main(String[] args) {
  
    String input = "http://www.altavista.com/cgi-bin/query?pg=q&kl=XX&stype=stext&q=+\"Java Network Programming\"";
    System.out.println(input);
    String output = URLEncoder.encode(input);
    System.out.println(output);
    
    String s = URLEncoder.encode("http");
    s += "://";
    s += URLEncoder.encode("www.altavista.com");
    s += "/";
    s += URLEncoder.encode("cgi-bin");
    s += "/";
    s += URLEncoder.encode("query");
    s += "?";
    s += URLEncoder.encode("pg");
    s += "=";
    s += URLEncoder.encode("q");
    s += "&";
    s += URLEncoder.encode("kl");
    s += "=";
    s += URLEncoder.encode("XX");
    s += "&";
    s += URLEncoder.encode("stype");
    s += "=";
    s += URLEncoder.encode("stext");
    s += "&";
    s += URLEncoder.encode("q");
    s += "=";
    s += URLEncoder.encode("\"Java Network Programming\"");
    System.out.println(s);
  }



}