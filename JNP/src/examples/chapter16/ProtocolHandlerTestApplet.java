package examples.chapter16;
import java.applet.*;
import java.awt.*;
import java.net.*;
import java.util.*;
// import com.macfaq.net.www.protocol.chargen.*;

// file:/D:/Java/JNP2/examples/15/ProtocolHandlerTestApplet.html

public class ProtocolHandlerTestApplet extends Applet
 implements URLStreamHandlerFactory {

  TextArea results = new TextArea(80, 40);

  public void init() {
    this.setLayout(new BorderLayout());
    this.add("Center", results);    
  }
  
  public URLStreamHandler createURLStreamHandler(String scheme) {
    return null; 
  }
  
  public void start() {
    try {
      Properties p = System.getProperties();
      Enumeration list = p.elements();
      String s = "";
      while (list.hasMoreElements()) {
        Object o = list.nextElement();
        s += o.toString();
        s += "\r\n"; 
      }

      results.append(s);  
    }
    catch (Throwable e) {
      results.append(e.toString() + "\r\n");  
    } 
    try {
      results.append("Trying to get java.version\r\n");  
      String s = System.getProperty("java.version",".");
      if (s == null) s = "no such property";
      s += "\r\n";
      results.append(s);  
    }
    catch (Throwable e) {
      results.append(e.toString() + "\r\n");  
    }   
    try {
      results.append("Trying to get classpath\r\n");  
      String s = System.getProperty("java.class.path",".");
      if (s == null) s = "no such property";
      s += "\r\n";
      results.append(s);  
    }
    catch (Throwable e) {
      results.append(e.toString() + "\r\n");  
    }
    try {
      results.append("Trying to get java.protocol.handler.pkgs\r\n");  
      String s = System.getProperty("java.protocol.handler.pkgs");
      if (s == null) s = "no such property";
      s += "; get succeeded\r\n";
      results.append(s);  
    }
    catch (Throwable e) {
      results.append(e.toString() + "\r\n");  
    }
    try {
      results.append("Trying to get java.content.handler.pkgs\r\n");  
      String s = System.getProperty("java.content.handler.pkgs");
      if (s == null) s = "no such property";
      s += "; get succeeded\r\n";
      results.append(s);  
    }
    catch (Throwable e) {
      results.append(e.toString() + "\r\n");  
    }
    try {
      results.append("Trying to set java.protocol.handler.pkgs\r\n");  
      System.setProperty("java.protocol.handler.pkgs", "com.macfaq.net.www.protocol"); 
    }
    catch (Throwable e) {
      results.append(e.toString() + "\r\n");  
    }
    try {
      results.append("Trying to set URLStreamHandlerFactory\r\n");  
      URL.setURLStreamHandlerFactory(this);      
    }
    catch (Throwable e) {
      results.append(e.toString() + "\r\n");  
    }
    try {
      results.append("Trying to construct chargen URL\r\n");  
      URL u = new URL("chargen://vision.poly.edu");      
      results.append(u + "\r\n");  
    }
    catch (Throwable e) {
      results.append(e.toString() + "\r\n");  
    }
    try {
      results.append("Trying to construct impgen URL\r\n");  
      URL u = new URL("impgen://vision.poly.edu");      
      results.append(u + "\r\n");  
    }
    catch (Throwable e) {
      results.append(e.toString() + "\r\n");  
    }
    
  }




}