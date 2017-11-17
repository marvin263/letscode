package examples.chapter18;
import java.rmi.*;
import java.net.*;
import java.math.BigInteger;

public class FibonacciClient {

  public static void main(String args[]) {
        
    if (args.length == 0 || !args[0].startsWith("rmi:")) {
      System.err.println(
        "Usage: java FibonacciClient rmi://host.domain:port/fibonacci number");
      return;   
    }
            
    try {      
      Object o = Naming.lookup(args[0]);
      Fibonacci calculator = (Fibonacci) o;
      for (int i = 1; i < args.length; i++) {
        try {
          BigInteger index = new BigInteger(args[i]);
          BigInteger f = calculator.getFibonacci(index);
          System.out.println("The " + args[i] + "th Fibonacci number is " 
           + f);
        }
        catch (NumberFormatException e) {
          System.err.println(args[i] + "is not an integer.");
        }
      } 
    }
    catch (MalformedURLException ex) {
      System.err.println(args[0] + " is not a valid RMI URL");
    }
    catch (RemoteException ex) {
      System.err.println("Remote object threw exception " + ex);
    }
    catch (NotBoundException ex) {
      System.err.println(
       "Could not find the requested remote object on the server");
    } 
  }
}
