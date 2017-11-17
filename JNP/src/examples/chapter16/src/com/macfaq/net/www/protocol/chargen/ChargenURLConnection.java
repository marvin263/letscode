package examples.chapter16.src.com.macfaq.net.www.protocol.chargen;

import java.net.*;
import java.io.*;
import com.macfaq.io.*;

public class ChargenURLConnection extends URLConnection {

  private Socket connection = null;
  
  public final static int DEFAULT_PORT = 19;

  public ChargenURLConnection(URL u) {
    super(u);
  }

  public synchronized InputStream getInputStream() throws IOException {
  
    if (!connected) this.connect();
    return new FiniteInputStream(this.connection.getInputStream());
    
  }

  public String getContentType() {
    return "text/plain";
  }
  
  public synchronized void connect() throws IOException {
  
    if (!connected) {
      int port = url.getPort();
      if ( port < 1 || port > 65535) {
        port = DEFAULT_PORT;
      }
      this.connection = new Socket(url.getHost(), port);
      this.connected = true;
    } 
  }
}
