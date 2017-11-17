package examples.chapter08;
import javax.swing.text.html.*;
import java.io.*;


public class TagStripper extends HTMLEditorKit.ParserCallback {

  private Writer out;
  
  public TagStripper(Writer out) {
    this.out = out; 
  }  
  
  public void handleText(char[] text, int position) {
    try {
      out.write(text);
      out.flush(); 
    }
    catch (IOException ex) {
      System.err.println(ex); 
    }
  }
  
}
