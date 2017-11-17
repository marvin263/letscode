package examples.chapter08;
import javax.swing.text.*;
import javax.swing.text.html.*;
import javax.swing.text.html.parser.*;
import java.io.*;
import java.net.*;


public class ReportTags extends HTMLEditorKit.ParserCallback {

  int count = 0;

  public void handleStartTag(HTML.Tag tag, MutableAttributeSet attributes,
   int position) {
    System.out.println(tag);
    count++;
  }
  
  public void handleEndTag(HTML.Tag tag, int position) {
    System.out.println(tag);
    count--;
    
  }
  
  public void handleSimpleTag(HTML.Tag tag, MutableAttributeSet attributes, 
   int position) {
    
    System.out.println(tag);
 
  }

  public void flush() {
    System.out.println(count); 
  }

  public static void main(String[] args) {
    
    ParserGetter kit = new ParserGetter();
    HTMLEditorKit.Parser parser = kit.getParser();
    HTMLEditorKit.ParserCallback callback 
     = new ReportTags();
    
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