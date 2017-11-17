package examples.chapter10;
import java.net.*;
import java.io.*;


public class PrecisePortScanner {

  public static void main(String[] args) {
    
    String host = "localhost";

    if (args.length > 0) {
      host = args[0];
    }

    try {
      InetAddress theAddress = InetAddress.getByName(host);
      boolean[] ports = new boolean[65536];
      for (int i = 1; i < 65536; i++) {
        try {
          Socket theSocket = new Socket(host, i);
          System.out.println("There is a server on port " + i + " of " + host);
          theSocket.close();   
        }
        catch (ConnectException e) {
          // must not be a server on this port
        }
        catch (IOException e) {
          // unexpected error
        }
      } // end for
      
      for (int i = 1; i < 65536; i++) {
        if (ports[i]) {
          System.out.println("There is a server on port " + i + " of " + host);
          theSocket.close();   
        }
        catch (ConnectException e) {
          // must not be a server on this port
        }
        catch (IOException e) {
          // unexpected error
        }
      } // end for

      
    } // end try
    catch (UnknownHostException e) {
      System.err.println(e);
    }

  }  // end main
  
}  // end PortScanner
