package examples.chapter08;
import javax.swing.text.*;
import javax.swing.text.html.*;
import javax.swing.text.html.parser.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class Outliner extends HTMLEditorKit.ParserCallback {

  private Writer out;
  private int level = 0;
  private boolean inHeader=false;
  private static String lineSeparator
    = System.getProperty("line.separator", "\r\n");
  
  public Outliner(Writer out) {
    this.out = out;
  }

  public void handleStartTag(HTML.Tag tag, 
   MutableAttributeSet attributes, int position) {
    
    int newLevel = 0;
    if (tag == HTML.Tag.H1) newLevel = 1;
    else if (tag == HTML.Tag.H2) newLevel = 2;
    else if (tag == HTML.Tag.H3) newLevel = 3;
    else if (tag == HTML.Tag.H4) newLevel = 4;
    else if (tag == HTML.Tag.H5) newLevel = 5;
    else if (tag == HTML.Tag.H6) newLevel = 6;
    else return;
    
    this.inHeader = true;
    try {
      if (newLevel > this.level) {
        for (int i =0; i < newLevel-this.level; i++) {
          out.write("<ul>" + lineSeparator + "<li>");
        }
      }
      else if (newLevel < this.level) {
        for (int i =0; i < this.level-newLevel; i++) {
          out.write(lineSeparator + "</ul>" + lineSeparator);
        }
        out.write(lineSeparator + "<li>");
      }
      else {
        out.write(lineSeparator + "<li>"); 
      }
      this.level = newLevel;
      out.flush();
    }
    catch (IOException ex) {
      System.err.println(ex);
    }
    
  }
  
  public void handleEndTag(HTML.Tag tag, int position) {

    if (tag == HTML.Tag.H1 || tag == HTML.Tag.H2 
     || tag == HTML.Tag.H3 || tag == HTML.Tag.H4
     || tag == HTML.Tag.H5 || tag == HTML.Tag.H6) {
      inHeader = false;
    }
    
    // work around bug in the parser that fails to call flush
    if (tag == HTML.Tag.HTML) this.flush();
    
  }
  
  
  public void handleText(char[] text, int position) { 
    
    if (inHeader) {
      try { 
        out.write(text);
        out.flush();
      }
      catch (IOException ex) {
        System.err.println(ex);
      }
    }
    
  }
  
  public void flush() {
    try {
      while (this.level-- > 0) {
        out.write(lineSeparator + "</ul>");   
      } 
      out.flush();
    }
    catch (IOException e) {
      System.err.println(e);
    }
  } 
  
  private static void parse(URL url, String encoding) throws IOException {
      ParserGetter kit = new ParserGetter();
      HTMLEditorKit.Parser parser = kit.getParser();
      InputStream in = url.openStream();
      InputStreamReader r = new InputStreamReader(in, encoding);
      HTMLEditorKit.ParserCallback callback = new Outliner
       (new OutputStreamWriter(System.out));
      parser.parse(r, callback, true);
  }
  
  public static void main(String[] args) { 

    ParserGetter kit = new ParserGetter();
    HTMLEditorKit.Parser parser = kit.getParser();    
  
    String encoding = "ISO-8859-1";
    URL url = null;
    try {
      url = new URL(args[0]);
      InputStream in = url.openStream();
      InputStreamReader r = new InputStreamReader(in, encoding);
      // parse once just to detect the encoding 
      HTMLEditorKit.ParserCallback doNothing 
       = new HTMLEditorKit.ParserCallback();
      parser.parse(r, doNothing, false);
    }
    catch (MalformedURLException ex) {
      System.out.println("Usage: java Outliner url"); 
      return;
    }
    catch (ChangedCharSetException ex) {
      String mimeType = ex.getCharSetSpec();
      encoding = mimeType.substring(mimeType.indexOf("=") + 1).trim();
    }
    catch (IOException ex) {
      System.err.println(ex); 
    }
    catch (ArrayIndexOutOfBoundsException ex) {
      System.out.println("Usage: java Outliner url"); 
      return;
    }
    
    try {
      parse(url, encoding);
    }
    catch(IOException ex) {
      System.err.println(ex);
    }
          
  }
  
}
