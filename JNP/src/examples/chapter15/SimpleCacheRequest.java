package examples.chapter15;
import java.net.*;
import java.io.*;
import java.util.*;

public class SimpleCacheRequest extends CacheRequest {
  
  ByteArrayOutputStream out = new ByteArrayOutputStream();
  
  public OutputStream getBody() throws IOException {
    return out;
  }

  public void abort() {
    out = null; 
  }

  public byte[] getData() {
    if (out == null) return null;
    else return out.toByteArray();
  }
  
}
