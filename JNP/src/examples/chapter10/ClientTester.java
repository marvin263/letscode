package examples.chapter10;
import java.net.*;
import java.io.*;
import com.macfaq.io.SafeBufferedReader; // from Chapter 4

public class ClientTester {

  public static void main(String[] args) {

    int port;
    
    try {
      port = Integer.parseInt(args[0]);
    }  
    catch (Exception ex) {
      port = 0;
    }
    
    try {
      ServerSocket server = new ServerSocket(port, 1);
      System.out.println("Listening for connections on port " 
       + server.getLocalPort());

      while (true) {
        Socket connection = server.accept();
        try {
          System.out.println("Connection established with " 
           + connection);
          Thread input = new InputThread(connection.getInputStream());
          input.start();
          Thread output 
           = new OutputThread(connection.getOutputStream());
          output.start();
          // wait for output and input to finish 
          try {
            input.join();
            output.join();
          }
          catch (InterruptedException ex) {
          }
        }
        catch (IOException ex) {
          System.err.println(ex); 
        }
        finally {
          try {
            if (connection != null) connection.close();
          }
          catch (IOException ex) {}
        }
      }
    }
    catch (IOException ex) {
      e.printStackTrace();
    }
  
  }

}

class InputThread extends Thread {
  
  InputStream in;
  
   public InputThread(InputStream in) {
     this.in = in;
   }

   public void run()  {
   
     try {     
       while (true) {
         int i = in.read();
         if (i == -1) break;
         System.out.write(i);
       }
     }
     catch (SocketException ex) {
       // output thread closed the socket
     }
     catch (IOException ex) {
       System.err.println(ex);
     }
     try {
       in.close();
     }
     catch (IOException ex) { 
     } 

  }

}

class OutputThread extends Thread {
  
  private Writer out;
    
  public OutputThread(OutputStream out) {
    this.out = new OutputStreamWriter(out);
  }

  public void run() {

    String line;
    BufferedReader in 
     = new SafeBufferedReader(new InputStreamReader(System.in));
    try {
      while (true) {
        line = in.readLine();
        if (line.equals(".")) break;
        out.write(line +"\r\n");
        out.flush();
      }   
    }
    catch (IOException ex) { 
    } 
    try {
      out.close();
    }
    catch (IOException ex) { 
    } 
    
   }

}
