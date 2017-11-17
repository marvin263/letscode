package examples.chapter13;
import java.net.*;
import java.io.*;
import java.util.*;


public class UDPDaytimeServer extends UDPServer {

  public final static int DEFAULT_PORT = 13;

  public UDPDaytimeServer() throws SocketException {
    super(DEFAULT_PORT); 
  }

  public void respond(DatagramPacket packet) {

    try {
      Date now = new Date();
      String response = now.toString() + "\r\n";
      byte[] data = response.getBytes("ASCII");
      DatagramPacket outgoing = new DatagramPacket(data, 
       data.length, packet.getAddress(), packet.getPort());
      socket.send(outgoing);
    }
    catch (IOException ex) {
      System.err.println(ex);
    }
    
  }

  public static void main(String[] args) {
 
   try {
     UDPServer server = new UDPDaytimeServer();
     server.start();
   }
   catch (SocketException ex) {
     System.err.println(ex);
   }
 
  }

}
