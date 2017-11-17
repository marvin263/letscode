package examples.chapter07;
import java.net.*;

public class URLDecoderExample {

  public static void main(String[] args) {
  
    String input = "http://www.altavista.com/cgi-bin/query?pg=q&kl=XX&stype=stext&q=%2B%22Java+Network+Programming%22&search.x=30&search.y=7";
    System.out.println(input);
    try {
      String output = URLDecoder.decode(input);
      System.out.println(output);
    }
    catch (Exception e) {
      System.err.println("Malformed URL");
    }
    
  }



}