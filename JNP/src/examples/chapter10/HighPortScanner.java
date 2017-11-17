package examples.chapter10;
import java.net.*;
import java.io.*;


public class HighPortScanner {

  public static void main(String[] args) {
    
    String host = "localhost";

    if (args.length > 0) {
      host = args[0];
    }

    try {
      InetAddress theAddress = InetAddress.getByName(host);
      for (int i = 1024; i < 65536; i++) {
        try {
          Socket theSocket = new Socket(theAddress, i);
          System.out.println("There is a server on port " + i + " of " + host);
        }
        catch (IOException e) {
          // must not be a server on this port
        }
      } // end for
    } // end try
    catch (UnknownHostException e) {
      System.err.println(e);
    }

  }  // end main
  
}  // end HighPortScanner
