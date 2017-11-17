package examples.chapter13;
import java.net.*;
import java.util.*;

public class UDPTimeClient {
  
  public final static int DEFAULT_PORT = 37;
  public final static String DEFAULT_HOST = "time-a.nist.gov";
  
  public static void main(String[] args) {
    
    InetAddress host;
    int port = DEFAULT_PORT;

    try {
      if (args.length > 0) {
        host = InetAddress.getByName(args[0]);
      }
      else {
        host = InetAddress.getByName(DEFAULT_HOST);
      } 
    }
    catch (Exception ex) {
      System.out.println("Usage: java UDPTimeClient host port");
      return;
    }

    if (args.length > 1) {
      try {
        port = Integer.parseInt(args[1]);
        if (port <= 0 || port > 65535) port = DEFAULT_PORT;;
      }
      catch (Exception ex){
      }
    }

    try {
      UDPPoke poker = new UDPPoke(host, port);
      byte[] response = poker.poke();
        if (response == null) {
        System.out.println("No response within allotted time");
        return;
        }
        else if (response.length != 4) {
        System.out.println("Unrecognized response format");
        return;         
        }
   
      
      // The time protocol sets the epoch at 1900,
      // the Java Date class at 1970. This number 
      // converts between them.
    
      long differenceBetweenEpochs = 2208988800L;

      long secondsSince1900 = 0;
      for (int i = 0; i < 4; i++) {
        secondsSince1900 
         = (secondsSince1900 << 8) | (response[i] & 0x000000FF);
      }

      long secondsSince1970 
       = secondsSince1900 - differenceBetweenEpochs;       
      long msSince1970 = secondsSince1970 * 1000;
      Date time = new Date(msSince1970);
      
      System.out.println(time);
    }
    catch (Exception ex) {
      System.err.println(ex);	
      ex.printStackTrace();
    }     
    
  } 
  
}
