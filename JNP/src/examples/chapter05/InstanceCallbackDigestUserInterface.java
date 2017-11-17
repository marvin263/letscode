package examples.chapter05;
import java.io.*;

public class InstanceCallbackDigestUserInterface {
  
  private File input;
  private byte[] digest;
  
  public InstanceCallbackDigestUserInterface(File input) {
    this.input = input;
  }
  
  public void calculateDigest() {
    InstanceCallbackDigest cb = new InstanceCallbackDigest(input, this);
    Thread t = new Thread(cb);
    t.start(); 
  }
  
  void receiveDigest(byte[] digest) {  
    this.digest = digest;
    System.out.println(this);
  }
  
  public String toString() {
    String result = input.getName() + ": ";
    if (digest != null) {
      for (int i = 0; i < digest.length; i++) {
        result += digest[i] + " ";
      }  
    }
    else {
      result += "digest not available";
    }
    return result;
  }
  
  public static void main(String[] args) {
  
    for (int i = 0; i < args.length; i++) {    
      // Calculate the digest
      File f = new File(args[i]);
      InstanceCallbackDigestUserInterface d
       = new InstanceCallbackDigestUserInterface(f);
      d.calculateDigest();
    } 
    
  }

}
