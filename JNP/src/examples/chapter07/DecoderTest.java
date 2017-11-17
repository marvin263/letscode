package examples.chapter07;
import java.io.*;
import java.net.*;

public class Test2 {

  public static void main(String[] args)
    throws Exception {
    String url = "http://www.%a4test.com/";
    try {
      String decoded = URLDecoder.decode(url, "UTF-8");
      System.out.println(decoded);
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
     
  }

}