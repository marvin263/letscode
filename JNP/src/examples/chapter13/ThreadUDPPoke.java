package examples.chapter13;
import java.net.*;
import java.io.*;

public class UDPPoke {

  private int bufferSize; // in bytes
  private int timeout;    // in milliseconds
  private DatagramSocket ds;
  private DatagramPacket outgoing;
   
  public UDPPoke(InetAddress host, int port, int bufferSize, 
   int timeout) throws SocketException {
    outgoing = new DatagramPacket(new byte[1], 1, host, port);
    this.timeout = timeout;
    this.bufferSize = bufferSize;
    ds = new DatagramSocket(0);
    ds.connect(host, port); // requires Java 2
  }
  
  public UDPPoke(InetAddress host, int port, int bufferSize) 
   throws SocketException {
    this(host, port, bufferSize, 120000);
  }
  
  public UDPPoke(InetAddress host, int port) throws SocketException {
    this(host, port, 8192, 120000);
  }
  
  public byte[] poke() throws IOException {
  	
    PokeThread pt = new PokeThread();
    pt.start();
    try {
      pt.join(timeout);
    }
    catch (InterruptedException e) {
    } 
    pt.stop();
    // may return null 
    return pt.getResponse();  	
  }
  
  // We need to put this in a separate thread to allow timeouts
  // We make the thread an inner class so it can only be started 
  // through the public poke() method. This also makes synchronization
  // issues easier to handle.
  class PokeThread extends Thread {
  	
    byte[] response = null;
    
    public void run() {
      try {
        DatagramPacket incoming 
         = new DatagramPacket(new byte[bufferSize], bufferSize);
        ds.send(outgoing);
        // next line blocks until the response is received
        ds.receive(incoming);
        int numBytes = incoming.getLength();
        response = new byte[numBytes];
        System.arraycopy(incoming.getData(), 0, response, 0, numBytes); 
      }
      catch (IOException e) {
      	// response will be null
      } 
    }
    
    public byte[] getResponse() {
      return this.response;	
    }
  	
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
