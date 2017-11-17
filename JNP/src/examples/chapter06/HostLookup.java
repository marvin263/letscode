package examples.chapter06;
import java.net.*;
import java.io.*;

public class HostLookup {

  public static void main (String[] args) {

    if (args.length > 0) { // use command line
      for (int i = 0; i < args.length; i++) {
        System.out.println(lookup(args[i]));
      }
    }
    else {
      BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
      System.out.println("Enter names and IP addresses. Enter \"exit\" to quit.");
      try {
        while (true) {
          String host = in.readLine();
          if (host.equalsIgnoreCase("exit") || host.equalsIgnoreCase("quit")) {
            break;
          }
          System.out.println(lookup(host));
        }
      }
      catch (IOException ex) {
        System.err.println(ex);
      }

   }

  } /* end main */


  private static String lookup(String host) {

    InetAddress node;

    // get the bytes of the IP address
    try {
      node = InetAddress.getByName(host);
    }
    catch (UnknownHostException ex) {
      return "Cannot find host " + host;
    }

    if (isHostname(host)) {
      return node.getHostAddress();
    }
    else {  // this is an IP address
      return node.getHostName();
    }

  }  // end lookup

  private static boolean isHostname(String host) {

    // Is this an IPv6 address?
    if (host.indexOf(':') != -1) return false;
      
    char[] ca = host.toCharArray();
    // if we see a character that is neither a digit nor a period
    // then host is probably a hostname
    for (int i = 0; i < ca.length; i++) {
      if (!Character.isDigit(ca[i])) {
        if (ca[i] != '.') return true;
      }
    }

    // Everything was either a digit or a period
    // so host looks like an IPv4 address in dotted quad format
    return false;

   }  // end isHostName

 } // end HostLookup
