package examples.chapter06;
import java.net.*;

class AllAddressesOfApple {

  public static void main (String[] args) {

    try {
      InetAddress[] addresses = InetAddress.getAllByName("www.apple.com");
      for (int i = 0; i < addresses.length; i++) {
        System.out.println(addresses[i]);
      }
    }
    catch (UnknownHostException e) {
      System.out.println("Could not find www.apple.com");
    }

  }

}
