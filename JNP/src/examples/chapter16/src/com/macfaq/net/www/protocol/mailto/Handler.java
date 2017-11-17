package examples.chapter16.src.com.macfaq.net.www.protocol.mailto;

import java.net.*;
import java.io.*;
import java.util.*;

public class Handler extends URLStreamHandler {

  protected URLConnection openConnection(URL u) throws IOException {
    return new MailtoURLConnection(u);
  }

  public void parseURL(URL u, String spec, int start, int limit) {
    
    String protocol    = u.getProtocol();
    String host        = "";
    int    port        = u.getPort();
    String file        = ""; // really username
    String userInfo    = null;
    String authority   = null;
    String query       = null;
    String fragmentID  = null;
  
    if( start < limit) {
      String address = spec.substring(start, limit);
      int atSign = address.indexOf('@');
      if (atSign >= 0) {
        host = address.substring(atSign+1);
        file = address.substring(0, atSign);
      }
    }
    
   // For Java 1.2 comment out this next line
   this.setURL(u, protocol, host, port, authority, 
                  userInfo, file, query, fragmentID );
    
    // In Java 1.2 and earlier uncomment the following line:
    // this.setURL(u, protocol, host, port, file, fragmentID );
          
  }

  protected String toExternalForm(URL u) {
  
    return "mailto:" + u.getFile() + "@" + u.getHost();;
    
  }
}
