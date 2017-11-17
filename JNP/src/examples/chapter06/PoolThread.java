package examples.chapter06;
import java.net.*; 
import java.io.*;
import java.util.*;

public class PoolThread extends Thread {

  private Stack entries;
  PooledWeblog log;   // used for callbacks
  
  public PoolThread(Stack entries, PooledWeblog log) {
    this.entries = entries;
    this.log = log;
  }

  public void run() {
  
    String entry;
    try {
    while (true) {
    
      // could lock this next one on entries if it proves a problem
      // but Vector is synchronized so shouldn't be necessary     
      try {
        entry = (String) entries.pop();
      }
      catch (EmptyStackException e) {
        if (log.isFinished()) break;
        this.yield();
        continue;
      }
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
      this.yield();
    }
    System.err.println("****************Thread finishing*****************");
      }  catch (NullPointerException e) {
          System.out.println("Exception 3: " + e);
          e.printStackTrace();
        }

  }

}