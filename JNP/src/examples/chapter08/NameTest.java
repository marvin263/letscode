package examples.chapter08;
import javax.swing.text.html.parser.*;
import javax.swing.text.html.*;
import java.io.IOException;


public class NameTest {

  public static void main(String[] args) {
    
 /*   HTMLEditorKit.Parser = ParserGetter.getParser();
    System.out.println();  */
    
    try {
      DTD d1 = DTD.getDTD("html");
      for (int i = 0; i < 100; i++) {
        System.out.println(d1.getElement(i).getName());
      }

    }
    catch (IOException e) {
      System.err.println(e);
      e.printStackTrace();    
    }
    

  }

}