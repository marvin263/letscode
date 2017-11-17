package examples.chapter05;
import java.io.*;


public class ReturnDigest1UserInterface {
  
  public static void main(String[] args) {
  
    for (int i = 0; i < args.length; i++) {
    
      // Calculate the digest
      File f = new File(args[i]);
      ReturnDigest1 dr = new ReturnDigest1(f);
      Thread t = new Thread(dr);
      t.start();
      
      // Now print the result
      StringBuffer result = new StringBuffer(f.toString());
      result.append(": ");
      byte[] digest = dr.getDigest();
      for (int j = 0; j < digest.length; j++) {
        result.append(digest[j] + " ");
      }  
      System.out.println(result);
      
    }
  
  }

}
