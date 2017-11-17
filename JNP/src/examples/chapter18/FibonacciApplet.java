package examples.chapter18;
import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;
import java.rmi.*;
import java.math.BigInteger;

public class FibonacciApplet extends Applet {

  private TextArea  resultArea 
   = new TextArea("", 20, 72, TextArea.SCROLLBARS_BOTH);
  private TextField inputArea  = new TextField(24);
  private Button calculate = new Button("Calculate");
  private String server;

  public void init() {
    
    this.setLayout(new BorderLayout());
    
    Panel north = new Panel();
    north.add(new Label("Type a non-negative integer"));
    north.add(inputArea);
    north.add(calculate);
    this.add(resultArea, BorderLayout.CENTER);
    this.add(north, BorderLayout.NORTH);
    Calculator c = new Calculator();
    inputArea.addActionListener(c);
    calculate.addActionListener(c);
    resultArea.setEditable(false);
    
    server = "rmi://" + this.getCodeBase().getHost() + "/fibonacci";
    
  }

  class Calculator implements ActionListener {
    
    public void actionPerformed(ActionEvent evt) {
      
      try {
        String input = inputArea.getText();
        if (input != null) {
          BigInteger index = new BigInteger(input);            
          Fibonacci f = (Fibonacci) Naming.lookup(server);
          BigInteger result =  f.getFibonacci(index);
          resultArea.setText(result.toString());
        }
      } 
      catch (Exception ex) {
        resultArea.setText(ex.getMessage());
      }          
    }
  }
}
