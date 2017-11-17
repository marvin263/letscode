package examples.chapter16.src.com.macfaq.net.www.protocol.finger;

import java.net.*;
import java.io.*;

public class Handler extends URLStreamHandler {

  public int getDefaultPort() {
    return 79;
  }

  protected URLConnection openConnection(URL u) throws IOException {
    return new FingerURLConnection(u);
  }

}
