package examples.chapter15;
import java.net.*;
import java.io.*;
import java.util.*;

 public class LastModified {

  public static void main(String args[]) {

    for (int i=0; i < args.length; i++) {
      try {
        URL u = new URL(args[i]);
        HttpURLConnection http = (HttpURLConnection) u.openConnection();
        http.setRequestMethod("HEAD");
        System.out.println(u + "was last modified at " 
         + new Date(http.getLastModified()));
      }  // end try
      catch (MalformedURLException ex) {
        System.err.println(args[i] + " is not a URL I understand");
      }
      catch (IOException ex) {
        System.err.println(ex);
      }      
      System.out.println();       
    }  // end for
      
  }  // end main

}  // end LastModified
