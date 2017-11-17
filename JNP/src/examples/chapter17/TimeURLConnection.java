package examples.chapter17;

import java.net.*;
import java.io.*;
import com.macfaq.net.www.content.application.*;

public class TimeURLConnection extends URLConnection {

  private Socket connection = null;
  public final static int DEFAULT_PORT = 37;

  public TimeURLConnection (URL u) {
    super(u);
  }

  public String getContentType() {
    return "application/x-time";
  }

  public Object getContent() throws IOException {
    ContentHandler ch = new x_time();
    return ch.getContent(this);
  }

  public Object getContent(Class[] classes) throws IOException { 
    ContentHandler ch = new x_time();
    return ch.getContent(this, classes);
  }

  public InputStream getInputStream() throws IOException {
    if (!connected) this.connect();
	  return this.connection.getInputStream();
  }

  public synchronized void connect() throws IOException {
  
    if (!connected) {
      int port = url.getPort();
      if ( port < 0) {
        port = DEFAULT_PORT;
      }
      this.connection = new Socket(url.getHost(), port);
      this.connected = true;
    } 
  }
}
