package examples.chapter16.src.com.macfaq.net.www.protocol.finger;

import java.net.*;
import java.io.*;

public class FingerURLConnection extends URLConnection {

  private Socket connection = null;
  
  public final static int DEFAULT_PORT = 79;

  public FingerURLConnection(URL u) {
    super(u);
  }

  public synchronized InputStream getInputStream() throws IOException {
  
    if (!connected) this.connect();
    InputStream in = this.connection.getInputStream();
    return in;
    
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
      OutputStream out = this.connection.getOutputStream();
      String names = url.getFile();
      if (names != null && !names.equals("")) {
        // delete initial /
        names = names.substring(1);
        names = URLDecoder.decode(names);
        byte[] result;
        try {
          result = names.getBytes("ASCII");
        }
        catch (UnsupportedEncodingException ex) {
          result = names.getBytes();  
        }
        out.write(result);
      }
      out.write('\r');
      out.write('\n');
      out.flush();
      this.connected = true;
    } 
  }
}
