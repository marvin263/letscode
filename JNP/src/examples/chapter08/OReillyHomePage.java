package examples.chapter08;
import javax.swing.text.*;
import javax.swing.*;
import java.io.*;
import java.awt.*;

public class OReillyHomePage {

  public static void main(String[] args) {
        
     JEditorPane jep = new JEditorPane();
     jep.setEditable(false);   
     
     try {
       jep.setPage("http://www.oreilly.com");
     }
     catch (IOException ex) {
       jep.setContentType("text/html");
       jep.setText("<html>Could not load http://www.oreilly.com </html>");
     } 
      
     JScrollPane scrollPane = new JScrollPane(jep);     
     JFrame f = new JFrame("O'Reilly & Associates");
     f.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
     f.setContentPane(scrollPane);
     f.setSize(512, 342);
     f.show();
    
  }

} 
