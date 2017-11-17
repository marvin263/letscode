package examples.chapter13;
import java.net.*;
import java.io.*;

public class UDPPoke {

  private int bufferSize; // in bytes
  private DatagramSocket ds;
  private DatagramPacket outgoing;
   
  public UDPPoke(InetAddress host, int port, int bufferSize, 
   int timeout) throws SocketException {
    outgoing = new DatagramPacket(new byte[1], 1, host, port);
    this.bufferSize = bufferSize;
    ds = new DatagramSocket(0);
    ds.connect(host, port); // requires Java 2
    ds.setSoTimeout(timeout);
  }
  
  public UDPPoke(InetAddress host, int port, int bufferSize) 
   throws SocketException {
    this(host, port, bufferSize, 30000);
  }
  
  public UDPPoke(InetAddress host, int port) 
   throws SocketException {
    this(host, port, 8192, 30000);
  }
  
  public byte[] poke() throws IOException {
  	
    byte[] response = null;
    try {
      ds.send(outgoing);
      DatagramPacket incoming 
       = new DatagramPacket(new byte[bufferSize], bufferSize);
      // next line blocks until the response is received
      ds.receive(incoming);
      int numBytes = incoming.getLength();
      response = new byte[numBytes];
      System.arraycopy(incoming.getData(), 0, response, 0, numBytes); 
    }
    catch (IOException e) {
    	// response will be null
    } 

    // may return null 
    return response;  	
  }

  public static void main(String[] args) {

    InetAddress host;
    int port = 0;

    try {
      host = InetAddress.getByName(args[0]); 
      port = Integer.parseInt(args[1]);
      if (port < 0 || port > 65535) throw new Exception();
    }
    catch (Exception e) {
      System.out.println("Usage: java UDPPoke host port");
      return;
    }

    try {
      UDPPoke poker = new UDPPoke(host, port);
      byte[] response = poker.poke();
      if (response == null) {
      	System.out.println("No response within allotted time");
      	return;
      }
      String result = "";
      try {
        result = new String(response, "ASCII");
      }
      catch (UnsupportedEncodingException e) {
      	// try a different encoding
      	result = new String(response, "8859_1");
      }
      System.out.println(result);
    }
    catch (Exception e) {
      System.err.println(e);	
      e.printStackTrace();
    }

  }  // end main

}
