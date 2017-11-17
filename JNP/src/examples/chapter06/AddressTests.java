package examples.chapter06;
import java.net.*;

public class AddressTests {

  public static int getVersion(InetAddress ia) {
    
    byte[] address = ia.getAddress();
    if (address.length == 4) return 4;
    else if (address.length == 16) return 6;
    else return -1;
    
  }

}
