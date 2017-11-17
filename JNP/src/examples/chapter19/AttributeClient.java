package examples.chapter19;
import javax.mail.*;
import javax.mail.internet.*;
import java.util.*;

public class AttributeClient {

  public static void main(String[] args) {
    
    if (args.length == 0) {
      System.err.println(
       "Usage: java AttributeClient protocol://username@host/foldername");
      return; 
    }
    
    URLName server = new URLName(args[0]);

    try {

      Session session = Session.getDefaultInstance(new Properties(), 
       new MailAuthenticator(server.getUsername()));

      // Connect to the server and open the folder
      Folder folder = session.getFolder(server);
      if (folder == null) {
        System.out.println("Folder " + server.getFile() + " not found.");
        System.exit(1);
      }  
      folder.open(Folder.READ_ONLY);
      
      // Get the messages from the server
      Message[] messages = folder.getMessages();
      for (int i = 0; i < messages.length; i++) {
        System.out.println("------------ Message " + (i+1) 
         + " ------------");
        String from = InternetAddress.toString(messages[i].getFrom());
        if (from != null) System.out.println("From: " + from);
        String to = InternetAddress.toString(
         messages[i].getRecipients(Message.RecipientType.TO));
        if (to != null) System.out.println("To: " + to);
        String subject = messages[i].getSubject();
        if (subject != null) System.out.println("Subject: " + subject);
        Date sent = messages[i].getSentDate();
        if (sent != null) System.out.println("Sent: " + sent);
        
        System.out.println();
        // Here's the attributes...
        System.out.println("This message is approximately " 
         + messages[i].getSize() + " bytes long.");
        System.out.println("This message has approximately " 
         + messages[i].getLineCount() + " lines.");
        String disposition = messages[i].getDisposition();
        if (disposition == null) ; // do nothing
        else if (disposition.equals(Part.INLINE)) {
          System.out.println("This part should be displayed inline");
        }
        else if (disposition.equals(Part.ATTACHMENT)) {
          System.out.println("This part is an attachment");
          String fileName = messages[i].getFileName();
          if (fileName != null) {
            System.out.println("The file name of this attachment is " 
            + fileName); 
          }
        }
        String description = messages[i].getDescription();
        if (description != null) {
          System.out.println("The description of this message is " 
           + description); 
        }

      } 

      // Close the connection 
      // but don't remove the messages from the server
      folder.close(false);
      
    } 
    catch (Exception ex) {
      ex.printStackTrace();
    }  
          
    // Since we may have brought up a GUI to authenticate,
    // we can't rely on returning from main() to exit
    System.exit(0);     
    
  }
}
