package examples.chapter13;
import java.net.*;
import java.io.*;


public class BigPacket {

  public static void main(String[] args) {
    
    int port = 9;

    String hostname = "localhost";
    if (args.length > 0) hostname = args[0];
    if (args.length > 1) port = Integer.parseInt(args[1]);

    try {
      String s = "";
      for (int i = 0; i < 8192*2; i += 74) {
        s += "6789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{\r\n";
      }
      byte[] data = s.getBytes("ASCII");
      InetAddress host = InetAddress.getByName(hostname);
      DatagramSocket ds = new DatagramSocket();
      ds.connect(host, port);
      ds.setSoTimeout(15000);
      ds.setReceiveBufferSize(32768);
      ds.setSendBufferSize(32768);
      System.out.println(ds.getReceiveBufferSize());
      System.out.println(ds.getSendBufferSize());
      DatagramPacket incoming = new DatagramPacket(new byte[65507], 65507);
      DatagramPacket outgoing = new DatagramPacket(data, data.length);
      ds.send(outgoing);      
    }
    catch (IOException e) {
      System.err.println(e); 
    }  
  
  }

}