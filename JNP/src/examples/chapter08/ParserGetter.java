package examples.chapter08;
import javax.swing.text.html.*;

public class ParserGetter extends HTMLEditorKit {

  // purely to make this method public
  public HTMLEditorKit.Parser getParser(){
    return super.getParser();
  }
  
}
