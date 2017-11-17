package examples.chapter07;
import java.net.*;
import javax.swing.text.html.parser.*;
import javax.swing.text.*;
import java.io.*;


public class AbsoluteConverter extends DocumentParser {

  public static void main(String[] args) {
  
    try {
      URL u = new URL(args[0]);
      OutputStream out = new FileOutputStream(args[1]);
      InputStream in = u.openStream();
      DTD html = DTD.getDTD("html");
      
      in.close();
      out.flush();
      out.close();
    }
    catch (ArrayIndexOutOfBoundsException e) {
      System.err.println("Usage: java PageSaver url local_file");  
    }
    catch (MalformedURLException e) {
      System.err.println("Usage: java PageSaver url local_file");  
    }
    catch (IOException e) {
      System.err.println("Usage: java PageSaver url local_file");  
    }
  
  }




}