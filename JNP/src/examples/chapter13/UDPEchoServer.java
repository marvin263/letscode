package examples.chapter13;
import java.net.*;
import java.io.*;

public class UDPEchoServer extends UDPServer {

  public final static int DEFAULT_PORT = 7;

  public UDPEchoServer() throws SocketException {
    super(DEFAULT_PORT); 
  }

  public void respond(DatagramPacket packet) {

    try {
      DatagramPacket outgoing = new DatagramPacket(packet.getData(), 
       packet.getLength(), packet.getAddress(), packet.getPort());
      socket.send(outgoing);
    }
    catch (IOException ex) {
      System.err.println(ex);
    }
    
  }

  public static void main(String[] args) {
 
   try {
     UDPServer server = new UDPEchoServer();
     server.start();
   }
   catch (SocketException ex) {
     System.err.println(ex);
   }
 
  }

}
