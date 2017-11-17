package examples.chapter13;
import java.net.*;
import java.io.*;

public abstract class UDPServer extends Thread {

  private int bufferSize; // in bytes
  protected DatagramSocket ds;
   
  public UDPServer(int port, int bufferSize) 
   throws SocketException {
    this.bufferSize = bufferSize;
    this.ds = new DatagramSocket(port);
  }
  
  public UDPServer(int port) throws SocketException {
    this(port, 8192);
  }
  
  public void run() {
  
    byte[] buffer = new byte[bufferSize];
    while (true) {
      DatagramPacket incoming = new DatagramPacket(buffer, buffer.length);
      try {
        ds.receive(incoming);
        this.respond(incoming);
      }
      catch (IOException e) {
        System.err.println(e);
      }      
    } // end while

  }  // end run
  
  public abstract void respond(DatagramPacket request);

}
