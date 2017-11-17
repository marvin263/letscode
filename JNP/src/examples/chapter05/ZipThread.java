package examples.chapter05;
import java.io.*;
import java.util.*;
import java.util.zip.*;


public class ZipThread extends Thread {

  private List pool;

  public ZipThread(List pool) {
    this.pool = pool;
  }

  public void run() {
    
    while (true) {
    
      File input = null;
      
      synchronized (pool) {         
        while (pool.isEmpty()) {
          try {
            pool.wait();
          }
          catch (InterruptedException e) {
          }
        }

        input = (File) pool.remove(pool.size()-1); 
      
      }
    
      // don't compress an already compressed file
      if (!input.getName().endsWith(".gz")) { 
      
        try {
          InputStream in = new FileInputStream(input);
          in = new BufferedInputStream(in);
          
          // possible NullPointerException here????
          File output = new File(input.getParent(), input.getName() + ".gz");
          if (!output.exists()) { // Don't overwrite an existing file
            OutputStream out = new FileOutputStream(output);
            out = new GZIPOutputStream(out);
            out = new BufferedOutputStream(out);
            int b;
            while ((b = in.read()) != -1) out.write(b);
            out.flush();
            out.close();
            in.close();
          }
        }
        catch (IOException e) {
          System.err.println(e);
        }
        
      } // end if 
  
    } // end while
    
  } // end run

} // end ZipThread