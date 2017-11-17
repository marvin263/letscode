package examples.chapter18;
import java.net.*;
import java.rmi.*;

public class FibonacciServer {

  public static void main(String[] args) {

    try {
      FibonacciImpl f = new FibonacciImpl();
      Naming.rebind("fibonacci", f);
      System.out.println("Fibonacci Server ready.");
    }
     catch (RemoteException rex) {
      System.out.println("Exception in FibonacciImpl.main: " + rex);
    }
    catch (MalformedURLException ex) {
      System.out.println("MalformedURLException " + ex);
    }
    
  }

}
