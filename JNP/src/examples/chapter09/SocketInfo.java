package examples.chapter09;
import java.net.*;
import java.io.*;

public class SocketInfo {

  public static void main(String[] args) {

    for (int i = 0; i < args.length; i++) {
      try {
        Socket theSocket = new Socket(args[i], 80);
        System.out.println("Connected to " + theSocket.getInetAddress() 
         + " on port "  + theSocket.getPort() + " from port " 
         + theSocket.getLocalPort() + " of " 
         + theSocket.getLocalAddress());
      }  // end try
      catch (UnknownHostException ex) {
        System.err.println("I can't find " + args[i]);
      }
      catch (SocketException ex) {
        System.err.println("Could not connect to " + args[i]);
      }
      catch (IOException ex) {
        System.err.println(ex);
      }

    } // end for

  }  // end main

}  // end SocketInfo
