package examples.chapter13;
import java.net.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;

public class UDPEchoServerWithChannels {

  public final static int DEFAULT_PORT = 7;
  public final static int MAX_PACKET_SIZE = 65507;

  public static void main(String[] args) {

    int port = DEFAULT_PORT;
    try {
      port = Integer.parseInt(args[0]);
    }
    catch (Exception ex) {
    }
    
    try {
      DatagramChannel channel = DatagramChannel.open();
      DatagramSocket socket = channel.socket();
      SocketAddress address = new InetSocketAddress(port);
      socket.bind(address);
      ByteBuffer buffer = ByteBuffer.allocateDirect(MAX_PACKET_SIZE);
      while (true) {
        SocketAddress client = channel.receive(buffer);
        buffer.flip();
        channel.send(buffer, client);
        buffer.clear();
      } // end while
    }  // end try
    catch (IOException ex) {
      System.err.println(ex);
    }  // end catch

  }  // end main

}
