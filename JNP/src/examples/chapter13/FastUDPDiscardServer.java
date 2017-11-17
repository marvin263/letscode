package examples.chapter13;
import java.net.*;

public class FastUDPDiscardServer extends UDPServer {

  public final static int DEFAULT_PORT = 9;
  
  public FastUDPDiscardServer() throws SocketException {
    super(DEFAULT_PORT);
  }
  
  public void respond(DatagramPacket packet) {}

  public static void main(String[] args) {
 
   try {
     UDPServer server = new FastUDPDiscardServer();
     server.start();
   }
   catch (SocketException ex) {
     System.err.println(ex);
   }
 
  }

}
