package examples.chapter06;
import java.net.*;
import java.util.*;

public class InterfaceLister {

    public static void main(String[] args) throws Exception {
        
      Enumeration interfaces = NetworkInterface.getNetworkInterfaces();
      while (interfaces.hasMoreElements()) {
        NetworkInterface ni = (NetworkInterface) interfaces.nextElement();
        System.out.println(ni);               
      }  
          
    }

}
