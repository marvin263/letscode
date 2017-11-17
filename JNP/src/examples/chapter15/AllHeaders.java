package examples.chapter15;
import java.net.*;
import java.io.*;

public class AllHeaders {

  public static void main(String args[]) {

    for (int i=0; i < args.length; i++) {
      try {
        URL u = new URL(args[i]);
        URLConnection uc = u.openConnection();
        for (int j = 1; ; j++) {
          String header = uc.getHeaderField(j);
          if (header == null) break;
          System.out.println(uc.getHeaderFieldKey(j) + ": " + header);
        }  // end for
      }  // end try
      catch (MalformedURLException ex) {
        System.err.println(args[i] + " is not a URL I understand.");
      }
      catch (IOException ex) {
        System.err.println(ex);
      }
      System.out.println();
    }  // end for

  }  // end main

}  // end AllHeaders
