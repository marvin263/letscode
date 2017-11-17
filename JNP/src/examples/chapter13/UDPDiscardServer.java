package examples.chapter13;
import java.net.*;
import java.io.*;

public class UDPDiscardServer {

  public final static int DEFAULT_PORT = 9;
  public final static int MAX_PACKET_SIZE = 65507;

  public static void main(String[] args) {

    int port = DEFAULT_PORT;
    byte[] buffer = new byte[MAX_PACKET_SIZE];

    try {
      port = Integer.parseInt(args[0]);
    }
    catch (Exception ex) {
      // use default port
    }

    try {
      DatagramSocket server = new DatagramSocket(port);
      DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
      while (true) {
        try {
          server.receive(packet);
          String s = new String(packet.getData(), 0, packet.getLength());
          System.out.println(packet.getAddress() + " at port " 
           + packet.getPort() + " says " + s);
          // reset the length for the next packet
          packet.setLength(buffer.length);
        }
        catch (IOException ex) {
          System.err.println(ex);
        }      
       } // end while
    }  // end try
    catch (SocketException  ex) {
      System.err.println(ex);
    }  // end catch

  }  // end main

}
