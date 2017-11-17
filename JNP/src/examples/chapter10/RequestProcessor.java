package examples.chapter10;
import java.net.*;
import java.io.*;
import java.util.*;
    
public class RequestProcessor implements Runnable {
    
  private static List pool = new LinkedList();
  private File documentRootDirectory;
  private String indexFileName = "index.html";
  
  public RequestProcessor(File documentRootDirectory, 
   String indexFileName) {
        
    if (documentRootDirectory.isFile()) {
      throw new IllegalArgumentException(
       "documentRootDirectory must be a directory, not a file");   
    }
    this.documentRootDirectory = documentRootDirectory;
    try {
      this.documentRootDirectory 
       = documentRootDirectory.getCanonicalFile();
    }
    catch (IOException ex) {
    }
    if (indexFileName != null) this.indexFileName = indexFileName;
  }
  
  public static void processRequest(Socket request) {
    
    synchronized (pool) {
      pool.add(pool.size(), request);
      pool.notifyAll();
    }

  }  
  
  public void run() {
        
    // for security checks
    String root = documentRootDirectory.getPath();
      
    while (true) {       
      Socket connection;
      synchronized (pool) {         
        while (pool.isEmpty()) {
          try {
            pool.wait();
          }
          catch (InterruptedException ex) {
          }
        }
        connection = (Socket) pool.remove(0); 
      }

      try {            
        String filename;
        String contentType;   
        OutputStream raw = new BufferedOutputStream(
                            connection.getOutputStream()
                           );         
        Writer out = new OutputStreamWriter(raw);
        Reader in = new InputStreamReader(
                     new BufferedInputStream(
                      connection.getInputStream()
                     ),"ASCII"
                    );
        StringBuffer requestLine = new StringBuffer();
        int c;
        while (true) {
          c = in.read();
          if (c == '\r' || c == '\n') break;
          requestLine.append((char) c);
        }
        
        String get = requestLine.toString();
        
        // log the request 
        System.out.println(get);
        
        StringTokenizer st = new StringTokenizer(get);
        String method = st.nextToken();
        String version = "";
        if (method.equals("GET")) {
          filename = st.nextToken();
          if (filename.endsWith("/")) filename += indexFileName;
          contentType = guessContentTypeFromName(filename);
          if (st.hasMoreTokens()) {
            version = st.nextToken();
          }

          File theFile = new File(documentRootDirectory, 
           filename.substring(1,filename.length()));
          if (theFile.canRead() 
              // Don't let clients outside the document root
           && theFile.getCanonicalPath().startsWith(root)) {
            DataInputStream fis = new DataInputStream(
                                   new BufferedInputStream(
                                    new FileInputStream(theFile)
                                   )
                                  );
            byte[] theData = new byte[(int) theFile.length()];
            fis.readFully(theData);
            fis.close();
            if (version.startsWith("HTTP ")) {  // send a MIME header
              out.write("HTTP/1.0 200 OK\r\n");
              Date now = new Date();
              out.write("Date: " + now + "\r\n");
              out.write("Server: JHTTP/1.0\r\n");
              out.write("Content-length: " + theData.length + "\r\n");
              out.write("Content-type: " + contentType + "\r\n\r\n");
              out.flush();
            }  // end if
        
            // send the file; it may be an image or other binary data 
            // so use the underlying output stream 
            // instead of the writer
            raw.write(theData);
            raw.flush();
          }  // end if
          else {  // can't find the file
            if (version.startsWith("HTTP ")) {  // send a MIME header
              out.write("HTTP/1.0 404 File Not Found\r\n");
              Date now = new Date();
              out.write("Date: " + now + "\r\n");
              out.write("Server: JHTTP/1.0\r\n");
              out.write("Content-type: text/html\r\n\r\n");
            } 
            out.write("<HTML>\r\n");
            out.write("<HEAD><TITLE>File Not Found</TITLE>\r\n");
            out.write("</HEAD>\r\n");
            out.write("<BODY>");
            out.write("<H1>HTTP Error 404: File Not Found</H1>\r\n");
            out.write("</BODY></HTML>\r\n");
            out.flush();
          }
        }
        else {  // method does not equal "GET"
          if (version.startsWith("HTTP ")) {  // send a MIME header
            out.write("HTTP/1.0 501 Not Implemented\r\n");
            Date now = new Date();
            out.write("Date: " + now + "\r\n");
            out.write("Server: JHTTP 1.0\r\n");
            out.write("Content-type: text/html\r\n\r\n"); 
          }       
          out.write("<HTML>\r\n");
          out.write("<HEAD><TITLE>Not Implemented</TITLE>\r\n");
          out.write("</HEAD>\r\n");
          out.write("<BODY>");
          out.write("<H1>HTTP Error 501: Not Implemented</H1>\r\n");
          out.write("</BODY></HTML>\r\n");
          out.flush();
        }
      }
      catch (IOException ex) {
      }
      finally {
        try {
          connection.close();        
        }
        catch (IOException ex) {} 
      }
      
    } // end while

  } // end run

  public static String guessContentTypeFromName(String name) {
    if (name.endsWith(".html") || name.endsWith(".htm")) {
      return "text/html";
    }
    else if (name.endsWith(".txt") || name.endsWith(".java")) {
      return "text/plain";
    }
    else if (name.endsWith(".gif")) {
      return "image/gif";
    }
    else if (name.endsWith(".class")) {
      return "application/octet-stream";
    }
    else if (name.endsWith(".jpg") || name.endsWith(".jpeg")) {
      return "image/jpeg";
    }
    else return "text/plain";
  }  

} // end RequestProcessor
