package examples.chapter07;
import com.macfaq.net.*;

import java.net.*;
import java.io.*;

public class DMoz {

  public static void main(String[] args) {
  
    String target = "";
    
    for (int i = 0; i < args.length; i++) {
      target += args[i] + " ";
    }
    target = target.trim();
    QueryString query = new QueryString("search", target);
    try {
      URL u = new URL("http://search.dmoz.org/cgi-bin/search?" + query);
      InputStream in = new BufferedInputStream(u.openStream());
      InputStreamReader theHTML = new InputStreamReader(in);
      int c;
      while ((c = theHTML.read()) != -1) {
        System.out.print((char) c);
      } 
    }
    catch (MalformedURLException ex) {
      System.err.println(ex);
    }
    catch (IOException ex) {
      System.err.println(ex);
    }
    
  }

}
