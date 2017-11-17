package examples.chapter10;
import java.net.*;
import java.io.*;

public class LocalPortScanner {

  public static void main(String[] args) {
    
    for (int port = 1; port <= 65535; port++) {

      try {
        // the next line will fail and drop into the catch block if
        // there is already a server running on the port
        ServerSocket server = new ServerSocket(port);
      }
      catch (IOException ex) {
        System.out.println("There is a server on port " + port + ".");
      } // end catch

    } // end for

  }
  
}
