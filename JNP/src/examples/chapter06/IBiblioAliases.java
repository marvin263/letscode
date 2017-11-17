package examples.chapter06;
import java.net.*;

public class IBiblioAliases {

  public static void main (String args[]) {

    try {
      InetAddress ibiblio = InetAddress.getByName("www.ibiblio.org");
      InetAddress helios = InetAddress.getByName("helios.metalab.unc.edu");
      if (ibiblio.equals(helios)) {
        System.out.println
         ("www.ibiblio.org is the same as helios.metalab.unc.edu");
      }
      else {
        System.out.println
         ("www.ibiblio.org is not the same as helios.metalab.unc.edu");
      }
    }
    catch (UnknownHostException ex) {
      System.out.println("Host lookup failed.");
    }

  }

}
