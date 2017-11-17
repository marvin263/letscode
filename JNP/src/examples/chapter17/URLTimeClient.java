package examples.chapter17;
import java.net.*;
import java.util.*;
import java.io.*;

public class URLTimeClient {

  public static void main(String[] args) {
  
    System.setProperty("java.protocol.handler.pkgs", 
     "com.macfaq.net.www.protocol");
  
    try {
      // You can replace this with your own time server
      URL u = new URL("time://tock.usno.navy.mil/");
      Class[] types = {String.class, Date.class, 
       Calendar.class, Long.class};
      Object o = u.getContent(types);
      System.out.println(o);
    }
    catch (IOException ex) {
     // Let's see what went wrong
     ex.printStackTrace(); 
    }
  }
}
