package examples.chapter09;
import java.net.*;
import java.io.*;

public class DaytimeClient {

  public static void main(String[] args) {

    String hostname;

    if (args.length > 0) {
      hostname = args[0];
    }
    else {
      hostname = "time.nist.gov";
    }

    try {
      Socket theSocket = new Socket(hostname, 13);
      InputStream timeStream = theSocket.getInputStream();
      StringBuffer time = new StringBuffer();
      int c;
      while ((c = timeStream.read()) != -1) time.append((char) c);
      String timeString = time.toString().trim();  
      System.out.println("It is " + timeString + " at " + hostname);
    }  // end try
    catch (UnknownHostException ex) {
      System.err.println(ex);
    }
    catch (IOException ex) {
      System.err.println(ex);
    }

  }  // end main

} // end DaytimeClient
