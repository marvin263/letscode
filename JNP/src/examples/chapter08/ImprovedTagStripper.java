package examples.chapter08;
import javax.swing.text.*;
import javax.swing.text.html.*;
import javax.swing.text.html.parser.*;
import java.io.*;
import java.net.*;


public class ImprovedTagStripper extends HTMLEditorKit.ParserCallback {

  private Writer out;
  
  public ImprovedTagStripper(Writer out) {
    this.out = out; 
  }  
  
  public void handleText(char[] text, int position) {
    try {
      out.write(text);
      out.flush();
    }
    catch (IOException e) {
      System.err.println(e); 
    }
  }
  
  public void handleStartTag(HTML.Tag tag, MutableAttributeSet attributes,
   int position) {
    try {
      out.write(' ');
    }
    catch (IOException e) {
      System.err.println(e); 
    }
     
  }
  
  public void handleEndTag(HTML.Tag tag, int position) {
    try {
      out.write(' ');
    }
    catch (IOException e) {
      System.err.println(e); 
    }
    
  }
  public void handleSimpleTag(HTML.Tag tag, MutableAttributeSet attributes, 
   int position) {
    
    try {
      out.write(' ');
    }
    catch (IOException e) {
      System.err.println(e); 
    }
 
  }

  public void flush() {
    try {
      out.flush();
      out.close(); 
    }
    catch (IOException e) {
    }
  }
  
  public static void main(String[] args) {
    
    ParserGetter kit = new ParserGetter();
    HTMLEditorKit.Parser parser = kit.getParser();
    HTMLEditorKit.ParserCallback callback 
     = new ImprovedTagStripper(new OutputStreamWriter(System.out));
    
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