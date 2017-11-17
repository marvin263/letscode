package examples.chapter10;
import java.net.*;
import java.io.*;
import java.util.*;

public class JHTTP extends Thread {

  private File documentRootDirectory;
  private String indexFileName = "index.html";
  private ServerSocket server;
  private int numThreads = 50;
    
  public JHTTP(File documentRootDirectory, int port, 
   String indexFileName) throws IOException {
    
    if (!documentRootDirectory.isDirectory()) {
      throw new IOException(documentRootDirectory 
       + " does not exist as a directory"); 
    }
    this.documentRootDirectory = documentRootDirectory;
    this.indexFileName = indexFileName;
    this.server = new ServerSocket(port);
  }

  public JHTTP(File documentRootDirectory, int port) 
   throws IOException {
    this(documentRootDirectory, port, "index.html");
  }

  public JHTTP(File documentRootDirectory) throws IOException {
    this(documentRootDirectory, 80, "index.html");
  }

  public void run() {
  
    for (int i = 0; i < numThreads; i++) {
      Thread t = new Thread(
       new RequestProcessor(documentRootDirectory, indexFileName));
      t.start();   
    }
    System.out.println("Accepting connections on port " 
     + server.getLocalPort());
    System.out.println("Document Root: " + documentRootDirectory);
    while (true) {
      try {
        Socket request = server.accept();
        RequestProcessor.processRequest(request);
      }
      catch (IOException ex) { 
      }   
    }
    
  }
  
  public static void main(String[] args) {

    // get the Document root
    File docroot;
    try {
      docroot = new File(args[0]);
    }
    catch (ArrayIndexOutOfBoundsException ex) {
      System.out.println("Usage: java JHTTP docroot port indexfile");
      return;
    }
    
    // set the port to listen on
    int port;
    try {
      port = Integer.parseInt(args[1]);
      if (port < 0 || port > 65535) port = 80;
    }  
    catch (Exception ex) {
      port = 80;
    }  
    
    try {            
      JHTTP webserver = new JHTTP(docroot, port);
      webserver.start();
    }
    catch (IOException ex) {
      System.out.println("Server could not start because of an " 
       + e.getClass());
      System.out.println(e);
    }
  
  }

}
