package examples.chapter19;
import javax.mail.*;
import javax.mail.internet.*;
import java.util.*;
import java.io.*;

public class MailClient {

  public static void main(String[] args) {
    
    if (args.length == 0) {
      System.err.println(
       "Usage: java MailClient protocol://username:password@host/foldername");
      return; 
    }
    
    URLName server = new URLName(args[0]);

    try {

      Session session = Session.getDefaultInstance(new Properties(), 
       null);

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
        messages[i].writeTo(System.out);
      } 

      // Close the connection 
      // but don't remove the messages from the server
      folder.close(false);
      
    } 
    catch (Exception ex) {
      ex.printStackTrace();
    }        
  }
}
