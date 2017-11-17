package examples.chapter06;
import java.net.*; 
import java.io.*;

public class ReverseLookup extends Thread {

  private String entry;
  ThreadedWeblog log;   // used for callbacks
  
  public ReverseLookup(String entry, ThreadedWeblog log) {
    this.entry = entry;
    this.log = log;
  }

  public void run() {
    
    int index = entry.indexOf(' ', 0);
    String remoteHost = entry.substring(0, index);
    String theRest = entry.substring(index, entry.length());
    
    try {
      remoteHost = InetAddress.getByName(remoteHost).getHostName();
    }
    catch (Exception e) {
      // remoteHost remains in dotted quad format
    }
    
    try {
      log.log(remoteHost + theRest);
    }
    catch (IOException e) {
    } 
  
  }

}