package examples.chapter08;
import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


public class IngredientsApplet extends JApplet {

  JTextField t;
  float price = 7.00f;

  public void init() {
  
    JCheckBox c;
    this.getContentPane().setLayout(new GridLayout(14, 1));
    this.getContentPane().add(new JLabel("What do you want on your pizza?"));
      
    this.t = new JTextField(String.valueOf(price));
    // so people can't change the price of the pizza
    t.setEditable(false); 

    c = new JCheckBox("<html><i>Pepperoni</i></html>");
    this.getContentPane().add(c);

    c = new JCheckBox("Olives");

    this.getContentPane().add(c);
    c = new JCheckBox("Onions");

    this.getContentPane().add(c);
    c = new JCheckBox("Sausage");

    this.getContentPane().add(c);
    c = new JCheckBox("Peppers");

    this.getContentPane().add(c);
    c = new JCheckBox("Extra Cheese");

    this.getContentPane().add(c);
    c = new JCheckBox("Ham");

    this.getContentPane().add(c);
    c = new JCheckBox("Pineapple");

    this.getContentPane().add(c);
    c = new JCheckBox("Anchovies");

    this.getContentPane().add(c);
    this.getContentPane().add(t);
    
  }

}