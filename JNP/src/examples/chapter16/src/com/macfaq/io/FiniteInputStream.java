package examples.chapter16.src.com.macfaq.io;

import java.io.*;

public class FiniteInputStream extends FilterInputStream {

  private int limit = 8192;
  private int bytesRead = 0;
  
  public FiniteInputStream(InputStream in) {
    this(in, 8192); 
  }

  public FiniteInputStream(InputStream in, int limit) {
    super(in);
    this.limit = limit; 
  }

  public int read() throws IOException {
    
    if (bytesRead >= limit) return -1;
    int c = in.read();
    bytesRead++;
    return c; 
    
  }
  
  public int read(byte[] data) throws IOException {
    return this.read(data, 0, data.length); 
  }
  
  public int read(byte[] data, int offset, int length) 
   throws IOException {
    
	if (data == null) throw new NullPointerException();
	else if ((offset < 0) || (offset > data.length) || (length < 0) ||
	 ((offset + length) > data.length) || ((offset + length) < 0)) {
	  throw new IndexOutOfBoundsException();
	} 
	else if (length == 0) {
	  return 0;
	}

    if (bytesRead >= limit) return -1;
    else if (bytesRead + length > limit) {
      int numToRead = bytesRead + length - limit; 
      int numRead = in.read(data, offset, numToRead);  
      if (numRead == -1) return -1;  
      bytesRead += numRead;
      return numRead; 
    }
    else { // will not exceed limit
      int numRead = in.read(data, offset, length);
      if (numRead == -1) return -1;  
      bytesRead += numRead; 
      return numRead; 
    } 
  }  

  public int available() throws IOException {
    if (bytesRead >= limit) return 1;
    else return in.available();
  }
}
