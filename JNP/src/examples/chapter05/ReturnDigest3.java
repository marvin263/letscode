package examples.chapter05;
import java.io.*;
import java.security.*;


public class ReturnDigest3 implements Runnable {

  private File input;
  private byte[] digest;

  public ReturnDigest3(File input) {
   this.input = input;
  }

  public void run() {
    try {
      FileInputStream in = new FileInputStream(input);
      MessageDigest sha = MessageDigest.getInstance("SHA");
      DigestInputStream din = new DigestInputStream(in, sha);
      int b;
      while ((b = din.read()) != -1) ;
      din.close();
      digest = sha.digest();
    }
    catch (IOException e) {
      System.err.println(e);
    }
    catch (NoSuchAlgorithmException e) {
      System.err.println(e);
    }
    
  }
  
  public byte[] getDigest() {
    return digest;
  }
  
  public static void main(String[] args) {
  
    ReturnDigest1[] digests = new ReturnDigest1[args.length];
  
    for (int i = 0; i < args.length; i++) {
    
      // Calculate the digest
      File f = new File(args[i]);
      digests[i] = new ReturnDigest1(f);
      Thread t = new Thread(digests[i]);
      t.start();
      
    }
    
    
    for (int i = 0; i < args.length; i++) {
      while (true) {
        // Now print the result
        byte[] digest = digests[i].getDigest();
        if (digest != null) {
          StringBuffer result = new StringBuffer(args[i]);
          result.append(": ");
          for (int j = 0; j < digest.length; j++) {
            result.append(digest[j] + " ");
          }  
          System.out.println(result);
          break;
        }
      }      
    }  
    
  }

}
