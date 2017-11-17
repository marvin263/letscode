package examples.chapter16.src.com.macfaq.net.www.protocol.daytime;

import java.net.*;
import java.io.*;

public class Handler extends URLStreamHandler {

  public int getDefaultPort() {
    return 13;
  }

  protected URLConnection openConnection(URL u) throws IOException {
    return new DaytimeURLConnection(u);
  }
}
