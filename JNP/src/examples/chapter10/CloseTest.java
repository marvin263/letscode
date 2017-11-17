package examples.chapter10;
import java.net.*;
import java.io.*;

public class CloseTest {

  public static void main(String[] args) {
  
    try {
      Socket s = new Socket("metalab.unc.edu", 80);
      OutputStream out = s.getOutputStream();
      out.close();
      InputStream in = s.getInputStream();
      
    }
    catch (IOException e) {
      System.err.println(e);   
    }
  
  }

}