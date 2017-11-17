package examples.chapter08;
import javax.swing.text.*;
import javax.swing.text.html.*;
import javax.swing.text.html.parser.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class ReportAttributes extends HTMLEditorKit.ParserCallback {

  public void handleStartTag(HTML.Tag tag, MutableAttributeSet attributes,
   int position) {
    this.listAttributes(attributes);
  }
  
  private void listAttributes(AttributeSet attributes) {
    System.out.println();
    Enumeration e = attributes.getAttributeNames();
    while (e.hasMoreElements()) {
      Object name = e.nextElement();
      Object value = attributes.getAttribute(name);
      if (!attributes.containsAttribute(name.toString(), value)) {
        System.out.println("containsAttribute() fails");
      }
      if (!attributes.isDefined(name.toString())) {
        System.out.println("isDefined() fails");
      }
      System.out.println(name + "=" + value);
    }
  }
  
  public void handleSimpleTag(HTML.Tag tag, MutableAttributeSet attributes, 
   int position) {
    this.listAttributes(attributes);     
  }

  public static void main(String[] args) {
    
    ParserGetter kit = new ParserGetter();
    HTMLEditorKit.Parser parser = kit.getParser();
    HTMLEditorKit.ParserCallback callback 
     = new ReportAttributes();
    
    try {
      URL u = new URL(args[0]);
      InputStream in = u.openStream();
      InputStreamReader r = new InputStreamReader(in);
      parser.parse(r, callback, false);
    }
    catch (IOException e) {
      System.err.println(e); 
    }
    
  }
  
}