package examples.chapter19;
import javax.mail.*;
import javax.mail.internet.*;
import java.util.*;
import java.io.*;

public class POP3Client {

  public static void main(String[] args) {
    
    Properties props = new Properties(); 

    String host = "utopia.poly.edu";
    String username = "eharold";
    String password = "mypassword";
    String provider = "pop3";

    try {

      // Connect to the POP3 server
      Session session = Session.getDefaultInstance(props, null);
      Store store = session.getStore(provider);
      store.connect(host, username, password);
      
      // Open the folder
      Folder inbox = store.getFolder("INBOX");
      if (inbox == null) {
        System.out.println("No INBOX");
        System.exit(1);
      }  
      inbox.open(Folder.READ_ONLY);
      
      // Get the messages from the server
      Message[] messages = inbox.getMessages();
      for (int i = 0; i < messages.length; i++) {
        System.out.println("------------ Message " + (i+1) 
         + " ------------");
        messages[i].writeTo(System.out);
      } 

      // Close the connection 
      // but don't remove the messages from the server
      inbox.close(false);
      store.close();  
    
    } 
    catch (Exception ex) {
      ex.printStackTrace();
    }    
  }
}
