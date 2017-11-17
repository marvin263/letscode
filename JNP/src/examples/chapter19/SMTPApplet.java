package examples.chapter19;
import java.applet.*;
import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;
import java.awt.event.*;
import java.awt.*;

public class SMTPApplet extends Applet {

  private Button    sendButton   = new Button("Send Message");
  private Label     fromLabel    = new Label("From: "); 
  private Label     subjectLabel = new Label("Subject: "); 
  private TextField fromField    = new TextField(40); 
  private TextField subjectField = new TextField(40); 
  private TextArea  message      = new TextArea(30, 60); 
  
  private String toAddress = "";

  public SMTPApplet() {
    
    this.setLayout(new BorderLayout());
    
    Panel north = new Panel();
    north.setLayout(new GridLayout(3, 1));
    
    Panel n1 = new Panel();
    n1.add(fromLabel);
    n1.add(fromField);
    north.add(n1);
    
    Panel n2 = new Panel();
    n2.add(subjectLabel);
    n2.add(subjectField);
    north.add(n2);

    this.add(north, BorderLayout.NORTH);
    
    message.setFont(new Font("Monospaced", Font.PLAIN, 12));
    this.add(message, BorderLayout.CENTER);

    Panel south = new Panel();
    south.setLayout(new FlowLayout(FlowLayout.CENTER));
    south.add(sendButton);
    sendButton.addActionListener(new SendAction());
    this.add(south, BorderLayout.SOUTH);    
    
  }
  
  public void init() {
    
    String subject = this.getParameter("subject");
    if (subject == null) subject = "";
    subjectField.setText(subject);
    
    toAddress = this.getParameter("to");
    if (toAddress == null) toAddress = "";
    
    String fromAddress = this.getParameter("from");
    if (fromAddress == null) fromAddress = ""; 
    fromField.setText(fromAddress);      
    
  }

  class SendAction implements ActionListener {
   
    public void actionPerformed(ActionEvent evt) {
      
      try {
        Properties props = new Properties();
        props.put("mail.host", getCodeBase().getHost());
         
        Session mailConnection = Session.getInstance(props, null);
        final Message msg = new MimeMessage(mailConnection);
  
        Address to = new InternetAddress(toAddress);
        Address from = new InternetAddress(fromField.getText());
      
        msg.setContent(message.getText(), "text/plain");
        msg.setFrom(from);
        msg.setRecipient(Message.RecipientType.TO, to);
        msg.setSubject(subjectField.getText());
        
        // This can take a non-trivial amount of time so 
        // spawn a thread to handle it. 
        Runnable r = new Runnable() {
          public void run() {
            try {
              Transport.send(msg);
            }
            catch (Exception ex) {
              ex.printStackTrace(); 
            }
          } 
        };
        Thread t = new Thread(r);
        t.start();
        
        message.setText("");
      }
      catch (Exception ex) {
        // We should really bring up a more specific error dialog here.
        ex.printStackTrace(); 
      }
    } 
  }
}
