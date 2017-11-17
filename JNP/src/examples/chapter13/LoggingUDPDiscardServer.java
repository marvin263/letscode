package examples.chapter13;
import java.net.*;

public class LoggingUDPDiscardServer extends UDPServer {

  public final static int DEFAULT_PORT = 9999;
  
  public LoggingUDPDiscardServer() throws SocketException {
    super(DEFAULT_PORT);
  }
  
  public void respond(DatagramPacket packet) {
    
    byte[] data = new byte[packet.getLength()];
    System.arraycopy(packet.getData(), 0, data, 0, packet.getLength());
    try {
      String s = new String(data, "8859_1");
      System.out.println(packet.getAddress() + " at port " 
       + packet.getPort() + " says " + s);
    }
    catch (java.io.UnsupportedEncodingException ex) {
      // This shouldn't happen
    }
  
  }

  public static void main(String[] args) {
 
   try {
     UDPServer erver = new LoggingUDPDiscardServer();
     server.start();
   }
   catch (SocketException ex) {
     System.err.println(ex);
   }
 
  }

}
