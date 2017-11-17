package examples.chapter05;
import java.io.*;
import java.security.*;


public class SynchronizedDigestRunnable implements Runnable {

  File input;

  public SynchronizedDigestRunnable(File input) {
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
      synchronized (System.out) {
        System.out.print(input + ": ");
        for (int i = 0; i < digest.length; i++) {
          System.out.print(digest[i] + " ");
        }
        System.out.println();
      }
    }
    catch (IOException e) {
      System.err.println(e);
    }
    catch (NoSuchAlgorithmException e) {
      System.err.println(e);
    }
    
  }
  
  public static void main(String[] args) {
  
    for (int i = 0; i < args.length; i++) {
      File f = new File(args[i]);
      SynchronizedDigestRunnable dr = new SynchronizedDigestRunnable(f);
      Thread t = new Thread(dr);
      t.start();
    }
  
  }

}