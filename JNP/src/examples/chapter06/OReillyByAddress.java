package examples.chapter06;
import java.net.*;

public class OReillyByAddress {

  public static void main (String[] args) {

    try {
      InetAddress address = InetAddress.getByName("208.201.239.37");
      System.out.println(address);
    }
    catch (UnknownHostException ex) {
      System.out.println("Could not find 208.201.239.37");
    }

  }

}
