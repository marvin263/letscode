package examples.chapter17;

import java.net.*;
import java.io.*;
import java.util.*;
import com.macfaq.io.SafeBufferedReader  // From Chapter 4

public class tab_separated_values extends ContentHandler {

  public Object getContent(URLConnection uc) throws IOException {

    String theLine;
    Vector lines = new Vector();

    InputStreamReader isr = new InputStreamReader(uc.getInputStream());
    SafeBufferedReader in = new SafeBufferedReader(isr);
    while ((theLine = in.readLine()) != null) {
      String[] linearray = lineToArray(theLine);
      lines.addElement(linearray);
    }

    return lines; 

  }

  private String[] lineToArray(String line)  {

    int numFields = 1;
    for (int i = 0; i < line.length(); i++) {
      if (line.charAt(i) == '\t') numFields++;
    }
    String[] fields = new String[numFields];
    int position = 0;
    for (int i = 0; i < numFields; i++) {
      StringBuffer buffer = new StringBuffer();
      while (position < line.length() && line.charAt(position) != '\t') {
        buffer.append(line.charAt(position));
        position++;
      }
      fields[i] = buffer.toString();
      position++;
    }

    return fields;

  }
}
