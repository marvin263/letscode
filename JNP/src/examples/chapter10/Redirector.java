package examples.chapter10;
import java.net.*;
import java.io.*;
import java.util.*;

public class Redirector implements Runnable {

  private int port;
  private String newSite;
  
  public Redirector(String site, int port) {
    this.port = port;
    this.newSite = site;
  }

  public void run() {
    
    try {
      
      ServerSocket server = new ServerSocket(this.port); 
      System.out.println("Redirecting connections on port " 
        + server.getLocalPort() + " to " + newSite);
        
      while (true) {
        
        try {
          Socket s = server.accept();
          Thread t = new RedirectThread(s);
          t.start();
        }  // end try
        catch (IOException ex) {   
        }
        
      } // end while
      
    } // end try
    catch (BindException ex) {
      System.err.println("Could not start server. Port Occupied");
    }         
    catch (IOException ex) {
      System.err.println(ex);
    }         

  }  // end run

  class RedirectThread extends Thread {
        
    private Socket connection;
        
    RedirectThread(Socket s) {
      this.connection = s;    
    }
        
    public void run() {
      
      try {
        
        Writer out = new BufferedWriter(
                      new OutputStreamWriter(
                       connection.getOutputStream(), "ASCII"
                      )
                     );
        Reader in = new InputStreamReader(
                     new BufferedInputStream( 
                      connection.getInputStream()
                     )
                    );
                    
        // read the first line only; that's all we need
        StringBuffer request = new StringBuffer(80);
        while (true) {
          int c = in.read();
          if (c == '\r' || c == '\n' || c == -1) break;
          request.append((char) c);
        }
        // If this is HTTP/1.0 or later send a MIME header
        String get = request.toString();
        int firstSpace = get.indexOf(' ');
        int secondSpace = get.indexOf(' ', firstSpace+1);
        String theFile = get.substring(firstSpace+1, secondSpace);
        if (get.indexOf("HTTP") != -1) {
          out.write("HTTP/1.0 302 FOUND\r\n");
          Date now = new Date();
          out.write("Date: " + now + "\r\n");
          out.write("Server: Redirector 1.0\r\n");
          out.write("Location: " + newSite + theFile + "\r\n");        
          out.write("Content-type: text/html\r\n\r\n");                 
          out.flush();                
        }
        // Not all browsers support redirection so we need to 
        // produce HTML that says where the document has moved to.
        out.write("<HTML><HEAD><TITLE>Document moved</TITLE></HEAD>\r\n");
        out.write("<BODY><H1>Document moved</H1>\r\n");
        out.write("The document " + theFile  
         + " has moved to\r\n<A HREF=\"" + newSite + theFile + "\">" 
         + newSite  + theFile 
         + "</A>.\r\n Please update your bookmarks<P>");
        out.write("</BODY></HTML>\r\n");
        out.flush();

      } // end try
      catch (IOException ex) {
      }
      finally {
        try {
          if (connection != null) connection.close();
        }
        catch (IOException ex) {}  
      }     
  
    }  // end run
    
  }

  public static void main(String[] args) {

    int thePort;
    String theSite;
    
    try {
      theSite = args[0];
      // trim trailing slash
      if (theSite.endsWith("/")) {
        theSite = theSite.substring(0, theSite.length()-1);
      }
    }
    catch (Exception ex) {
      System.out.println(
       "Usage: java Redirector http://www.newsite.com/ port");
      return;
    }
    
    try {
      thePort = Integer.parseInt(args[1]);
    }  
    catch (Exception ex) {
      thePort = 80;
    }  
      
    Thread t = new Thread(new Redirector(theSite, thePort));
    t.start();
  
  }  // end main

}
