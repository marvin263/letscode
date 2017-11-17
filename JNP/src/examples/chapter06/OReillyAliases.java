package examples.chapter06;
import java.net.*;

public class OReillyAliases {

  public static void main (String args[]) {

    try {
      InetAddress oreilly = InetAddress.getByName("www.oreilly.com");
      InetAddress helio = InetAddress.getByName("helio.ora.com");
      if (oreilly.equals(helio)) {
        System.out.println
          ("www.oreilly.com is the same as helio.ora.com");
      }
      else {
        System.out.println
          ("www.oreilly.com is not the same as helio.ora.com");
      }
    }
    catch (UnknownHostException e) {
      System.out.println("Host lookup failed.");
    }

  }

}
