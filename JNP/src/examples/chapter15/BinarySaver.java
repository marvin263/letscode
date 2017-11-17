package examples.chapter15;
import java.net.*;
import java.io.*;

public class BinarySaver {

  public static void main (String args[]) {

    for (int i = 0; i < args.length; i++) {

      try {
        URL root = new URL(args[i]);
        saveBinaryFile(root);
      }
      catch (MalformedURLException ex) {
        System.err.println(args[i] + " is not URL I understand.");
      }
      catch (IOException ex) {
        System.err.println(ex);
      }
    } // end for

  } // end main


  public static void saveBinaryFile(URL u) throws IOException {
  
    URLConnection uc = u.openConnection();
    String contentType = uc.getContentType();
    int contentLength = uc.getContentLength();
    if (contentType.startsWith("text/") || contentLength == -1 ) {
      throw new IOException("This is not a binary file.");
    }

    InputStream raw = uc.getInputStream();
    InputStream in  = new BufferedInputStream(raw);
    byte[] data = new byte[contentLength];
    int bytesRead = 0;
    int offset = 0;
    while (offset < contentLength) {
       bytesRead = in.read(data, offset, data.length-offset);
       if (bytesRead == -1) break;
       offset += bytesRead;
    }
    in.close();
    
    if (offset != contentLength) {
      throw new IOException("Only read " + offset 
       + " bytes; Expected " + contentLength + " bytes");
    }

    String filename = u.getFile();
    filename = filename.substring(filename.lastIndexOf('/') + 1);
    FileOutputStream fout = new FileOutputStream(filename);
    fout.write(data);
    fout.flush();
    fout.close();
  
  } 

} // end BinarySaver
