package examples.chapter05;
import java.io.*;
import java.security.*;


public class ReturnDigest1 implements Runnable {

  private File input;
  private byte[] digest;

  public ReturnDigest1(File input) {
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

}
