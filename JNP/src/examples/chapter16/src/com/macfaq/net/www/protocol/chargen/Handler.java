package examples.chapter16.src.com.macfaq.net.www.protocol.chargen;

import java.net.*;
import java.io.*;

public class Handler extends URLStreamHandler {

  public int getDefaultPort() {
    return 19;
  }

  protected URLConnection openConnection(URL u) throws IOException {
    return new ChargenURLConnection(u);
  }
}
