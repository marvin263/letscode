package examples.chapter19;
import javax.mail.*;
import javax.mail.internet.*;
import java.util.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

public class SMTPClient extends JFrame {

  private JButton     sendButton   = new JButton("Send Message"); 
  private JLabel      fromLabel    = new JLabel("From: "); 
  private JLabel      toLabel      = new JLabel("To: "); 
  private JLabel      hostLabel    = new JLabel("SMTP Server: "); 
  private JLabel      subjectLabel = new JLabel("Subject: "); 
  private JTextField  fromField    = new JTextField(40); 
  private JTextField  toField      = new JTextField(40); 
  private JTextField  hostField    = new JTextField(40); 
  private JTextField  subjectField = new JTextField(40); 
  private JTextArea   message      = new JTextArea(40, 72); 
  private JScrollPane jsp          = new JScrollPane(message);

  public SMTPClient() {
    
    super("SMTP Client");
    Container contentPane = this.getContentPane();
    contentPane.setLayout(new BorderLayout());  
    
    JPanel labels = new JPanel();
    labels.setLayout(new GridLayout(4, 1));
    labels.add(hostLabel);
    
    JPanel fields = new JPanel();
    fields.setLayout(new GridLayout(4, 1));
    String host = System.getProperty("mail.host", "");
    hostField.setText(host);
    fields.add(hostField);
    
    labels.add(toLabel);
    fields.add(toField);

    String from = System.getProperty("mail.from", "");
    fromField.setText(from);
    labels.add(fromLabel);
    fields.add(fromField);

    labels.add(subjectLabel);
    fields.add(subjectField);
    
    Box north = Box.createHorizontalBox();
    north.add(labels);
    north.add(fields);
    
    contentPane.add(north, BorderLayout.NORTH);
    
    message.setFont(new Font("Monospaced", Font.PLAIN, 12));
    contentPane.add(jsp, BorderLayout.CENTER);

    JPanel south = new JPanel();
    south.setLayout(new FlowLayout(FlowLayout.CENTER));
    south.add(sendButton);
    sendButton.addActionListener(new SendAction());
    contentPane.add(south, BorderLayout.SOUTH);       
    
    this.pack(); 
    
  }

  class SendAction implements ActionListener {
   
    public void actionPerformed(ActionEvent evt) {
      
      try {
        Properties props = new Properties();
        props.put("mail.host", hostField.getText());
         
        Session mailConnection = Session.getInstance(props, null);
        final Message msg = new MimeMessage(mailConnection);
  
        Address to = new InternetAddress(toField.getText());
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
        // I should really bring up a more specific error dialog here.
        ex.printStackTrace(); 
      }
      
    } 
    
  }

  public static void main(String[] args) {

    SMTPClient client = new SMTPClient();
    // Next line requires Java 1.3 or later. I want to set up the
    // exit behavior here rather than in the constructor since
    // other programs that use this class may not want to exit 
    // the application when the SMTPClient window closes.
    client.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    client.show();

  }
}
