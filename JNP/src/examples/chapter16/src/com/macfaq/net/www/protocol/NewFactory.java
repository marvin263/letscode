package examples.chapter16.src.com.macfaq.net.www.protocol;

import java.net.*;

public class NewFactory implements URLStreamHandlerFactory {

   public URLStreamHandler createURLStreamHandler(String protocol) {
  
    if (protocol.equalsIgnoreCase("finger")) {
      return new com.macfaq.net.www.protocol.finger.Handler();
    }
    else if (protocol.equalsIgnoreCase("chargen")) {
      return new com.macfaq.net.www.protocol.chargen.Handler();
    }
    else if (protocol.equalsIgnoreCase("daytime")) {
      return new com.macfaq.net.www.protocol.daytime.Handler();
    }
    else {
      return null;
    }
  }
}
