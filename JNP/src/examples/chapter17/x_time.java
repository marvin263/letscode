package examples.chapter17;

import java.net.*;
import java.io.*;
import java.util.*;

public class x_time extends ContentHandler {

  public Object getContent(URLConnection uc) throws IOException {

    Class[] classes = new Class[1];
    classes[0] = Date.class;
    return this.getContent(uc, classes); 

  }

  public Object getContent(URLConnection uc, Class[] classes)
   throws IOException {
    
    InputStream in = uc.getInputStream();
    for (int i = 0; i < classes.length; i++) {
      if (classes[i] == InputStream.class) {
        return in;  
      } 
      else if (classes[i] == Long.class) {
        long secondsSince1900 = readSecondsSince1900(in);
        return new Long(secondsSince1900);
      }
      else if (classes[i] == Date.class) {
        long secondsSince1900 = readSecondsSince1900(in);
        Date time = shiftEpochs(secondsSince1900);
        return time;
      }
      else if (classes[i] == Calendar.class) {
        long secondsSince1900 = readSecondsSince1900(in);
        Date time = shiftEpochs(secondsSince1900);
        Calendar c = Calendar.getInstance();
        c.setTime(time);
        return c;
      }
      else if (classes[i] == String.class) {
        long secondsSince1900 = readSecondsSince1900(in);
        Date time = shiftEpochs(secondsSince1900);
        return time.toString();
      }      
    }
    
    return null; // no requested type available
    
  }
  
  private long readSecondsSince1900(InputStream in) 
   throws IOException {
    
    long secondsSince1900 = 0;
    for (int j = 0; j < 4; j++) {
      secondsSince1900 = (secondsSince1900 << 8) | in.read();
    }
    return secondsSince1900;
    
  }
  
  private Date shiftEpochs(long secondsSince1900) {
  
    // The time protocol sets the epoch at 1900, the Java Date class
    //  at 1970. This number converts between them.
    long differenceBetweenEpochs = 2208988800L;
    
    long secondsSince1970 = secondsSince1900 - differenceBetweenEpochs;       
    long msSince1970 = secondsSince1970 * 1000;
    Date time = new Date(msSince1970);
    return time;
    
  }
}
