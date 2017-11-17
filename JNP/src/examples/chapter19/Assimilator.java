package examples.chapter19;
import javax.mail.*;
import javax.mail.internet.*;
import java.util.*;

public class Assimilator {

  public static void main(String[] args) {

    try {
      Properties props = new Properties();
      props.put("mail.host", "mail.cloud9.net");
       
      Session mailConnection = Session.getInstance(props, null);
      Message msg = new MimeMessage(mailConnection);

      Address bill = new InternetAddress("god@microsoft.com", 
       "Bill Gates");
      Address elliotte = new InternetAddress("elharo@metalab.unc.edu");
    
      msg.setContent("Resistance is futile. You will be assimilated!", 
       "text/plain");
      msg.setFrom(bill);
      msg.setRecipient(Message.RecipientType.TO, elliotte);
      msg.setSubject("You must comply.");
      
      Transport.send(msg);
      
    }
    catch (Exception ex) {
      ex.printStackTrace(); 
    }
    
  }
}
