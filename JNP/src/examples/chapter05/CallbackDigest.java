package examples.chapter05;
import java.io.*;
import java.security.*;

public class CallbackDigest implements Runnable {

  private File input;

  public CallbackDigest(File input) {
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
      byte[] digest = sha.digest();
      CallbackDigestUserInterface.receiveDigest(digest, 
       input.getName());
    }
    catch (IOException ex) {
      System.err.println(ex);
    }
    catch (NoSuchAlgorithmException ex) {
      System.err.println(ex);
    }
    
  }

}
