package examples.chapter05;
import java.io.*;

public class JoinDigestUserInterface {
  
  public static void main(String[] args) {

    ReturnDigest[] digestThreads = new ReturnDigest[args.length];
  
    for (int i = 0; i < args.length; i++) {

      // Calculate the digest
      File f = new File(args[i]);
      digestThreads[i] = new ReturnDigest(f);
      digestThreads[i].start();
    
    }
  
    for (int i = 0; i < args.length; i++) {

      try {      
        digestThreads[i].join();
        // Now print the result
        StringBuffer result = new StringBuffer(args[i]);
        result.append(": ");
        byte[] digest = digestThreads[i].getDigest();
        for (int j = 0; j < digest.length; j++) {
          result.append(digest[j] + " ");
        }  
        System.out.println(result);
      }
      catch (InterruptedException ex) {
        System.err.println("Thread Interrupted before completion");
      } 
    
    }     
  
  }

}
