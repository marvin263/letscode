package examples.chapter13;
import java.net.*;
import java.io.*;


public class ReflectorTest {

  public static void main(String[] args) {
  
    int port = 7;

    String hostname = "vision.poly.edu";
    if (args.length > 0) hostname = args[0];
    if (args.length > 1) port = Integer.parseInt(args[1]);

    try {
      InetAddress host = InetAddress.getByName(hostname);
      DatagramSocket ds = new DatagramSocket();
      ds.connect(host, port);
      ds.setSoTimeout(15000);
      ds.setReceiveBufferSize(32768);
      ds.setSendBufferSize(32768);
      System.out.println(ds.getReceiveBufferSize());
      System.out.println(ds.getSendBufferSize());
      DatagramPacket incoming = new DatagramPacket(new byte[65507], 65507);
      for (int i = 8000; i < 65507; i++) {
        incoming.setLength(i);
        byte[] data = new byte[i];
        for (int j = 0; j < i; j++) {
          data[j]=(byte) (j % 256); 
        }
        DatagramPacket outgoing = new DatagramPacket(data, i);
        ds.send(outgoing);
        try {
          ds.receive(incoming);
          byte[] response = new byte[incoming.getLength()];
          System.arraycopy(incoming.getData(), 0, response, 0, incoming.getLength());
          if (outgoing.getLength() != incoming.getLength()) {
            System.out.println("Inconsistency at " + i + ";incoming length=" 
             + incoming.getLength() + ";outgoing length=" + outgoing.getLength()); 
          }
          else {
            for (int j = 0; j < response.length; j++) {
              if (response[j] != data[j]) {
                System.out.println("Inconsistency at packet " 
                 + i + " index " + j + ";response=" + response[j]); 
                break; 
              } 
            } 
          }
        }
        catch (InterruptedIOException e) {
          System.out.println("Lost packet " + i ); 
        }
        System.out.flush();
      }
      
      
    }
    catch (IOException e) {
      System.err.println(e); 
    }
  
  }


}