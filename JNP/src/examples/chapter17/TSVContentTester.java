package examples.chapter17;
import java.io.*;
import java.net.*;
import java.util.*;

public class TSVContentTester {

  private static void test(URL u) throws IOException {
  
    Object content = u.getContent();
    Vector v = (Vector) content;
    for (Enumeration e = v.elements() ; e.hasMoreElements() ;) {
      String[] sa = (String[]) e.nextElement();
      for (int i = 0; i < sa.length; i++) {
        System.out.print(sa[i] + "\t");
      }
      System.out.println();
    } 

  }

  public static void main (String[] args) {  
    
    // If you uncomment these lines, then you don't have to 
    // set the java.content.handler.pkgs property from the
    // command line or your properties files.

/*    String pkgs = System.getProperty("java.content.handler.pkgs", "");
    if (!pkgs.equals("")) {
      pkgs = pkgs + "|";
    }
    pkgs += "com.macfaq.net.www.content";      
    System.setProperty("java.content.handler.pkgs", pkgs);  */  

    for (int i = 0; i < args.length; i++) {
      try {
        URL u = new URL(args[i]);
        test(u);
      }
      catch (MalformedURLException ex) {
        System.err.println(args[i] + " is not a good URL"); 
      }
      catch (Exception ex) {
        ex.printStackTrace();
      }
    }
  }
}
