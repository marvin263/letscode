package examples.chapter08;
import javax.swing.text.html.*;


public class ListTags {

  public static void main(String[] args) {
  
    HTML.Tag[] list = HTML.getAllTags();
    for (int i = 0; i < list.length; i++) {
      System.out.println((i+1) + ": " + list[i]);
    }
    
  }
  
  
}