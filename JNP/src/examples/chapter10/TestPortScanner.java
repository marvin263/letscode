package examples.chapter10;
import java.net.*;
import java.io.*;


public class TestPortScanner {

  public static void main(String[] args) {
    
    String host = "localhost";

    if (args.length > 0) {
      host = args[0];
    }
Socket theSocket = null;
    try {
      InetAddress theAddress = InetAddress.getByName(host);
      for (int i = 130; i < 140; i++) {
        try {
          theSocket = new Socket(host, i);
          System.out.println("There is a server on port " + i + " of " + host);
          System.out.println(theSocket);
          theSocket.close();   
        }
        catch (IOException e) {
          // must not be a server on this port
          try {
            theSocket.close();
          }
          catch (Exception ex) {
            System.err.println("close failure"); 
          }
        }
      } // end for
    } // end try
    catch (UnknownHostException e) {
      System.err.println(e);
    }

  }  // end main
  
}  // end PortScanner
