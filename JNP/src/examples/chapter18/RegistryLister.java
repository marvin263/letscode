package examples.chapter18;
import java.rmi.*;

public class RegistryLister {

  public static void main(String[] args) {
  
    int port = 1099;
    
    if (args.length == 0) {
      System.err.println("Usage: java RegistryLister host port");
      return;
    }
    
    String host = args[0];
    
    if (args.length > 1) {
      try {
        port = Integer.parseInt(args[1]);
        if (port <1 || port > 65535) port = 1099;
      }
      catch (NumberFormatException ex) {}
    
    }
  
    String url = "rmi://" + host + ":" + port + "/";
    try {
      String[] remoteObjects = Naming.list(url);
      for (int i = 0; i < remoteObjects.length; i++) {
        System.out.println(remoteObjects[i]); 
      }
    }
    catch (RemoteException ex) {
      System.err.println(ex);
    }
    catch (java.net.MalformedURLException ex) {
      System.err.println(ex);
    }
  }
}
