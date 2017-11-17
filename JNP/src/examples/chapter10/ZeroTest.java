package examples.chapter10;
import java.net.*;

public class ZeroTest {

  public static void main(String[] args) throws Exception {
    
    Socket s = new Socket("metalab.unc.edu", 0);
    System.out.println(s.getPort());
  
  }

}