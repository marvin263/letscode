package examples.chapter06;
import java.io.*;
import java.util.*;


public class ThreadedWeblog {

  private BufferedReader in;
  private BufferedWriter out;
  private int numberOfThreads;
  private Stack entries;

  public ThreadedWeblog(InputStream in, OutputStream out, int numberOfThreads) {
    this.in = new BufferedReader(new InputStreamReader(in));
    this.out = new BufferedWriter(new OutputStreamWriter(out));
    this.numberOfThreads = numberOfThreads;
    this.entries = new Stack();
  }
  
  public void processLogFile() {
  
    try {
      boolean lookingForLineFeed = false;
outer: while (true) {

        // read the next line
        StringBuffer sb = new StringBuffer(80);
        while (true) {
          int c = in.read();
          if (c == '\n') {
            if (lookingForLineFeed) {
              lookingForLineFeed = false;
              continue; 
            }
            else break;
          }
          else if (c == '\r') {
            lookingForLineFeed = true;
            break;
          }
          else if (c == -1) {
            break outer;
          }
          else {
            sb.append((char) c);
          }
        }
        
        // separate out the IP address
        String entry = sb.toString();
        
        // find the host name and print it out
        Thread t = new ReverseLookup(entry, this);
        t.start();
        Thread.yield();
      } // end while
    }
    catch (IOException e) {
      System.out.println("Exception: " + e);
    }
    
  }
  
  public void log(String entry) throws IOException {
    out.write(entry + '\n');
    out.flush();
    // does common web logfile format specify line separator?
  }
  
  public static void main(String[] args) {

    try {
      ThreadedWeblog tw = new ThreadedWeblog(new FileInputStream(args[0]), 
       System.out);
      tw.processLogFile();
    }
    catch (FileNotFoundException e) {
      System.err.println("Usage: java ThreadedWeblog logfile_name");
    }

  }  // end main

}
