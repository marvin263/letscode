package examples.chapter13;
import java.net.*;
import java.io.*;

public class SenderThread extends Thread {

  private InetAddress server;
  private DatagramSocket socket;
  private boolean stopped = false;
  private int port;
  
  public SenderThread(InetAddress address, int port) 
   throws SocketException {
    this.server = address;
    this.port = port;
    this.socket = new DatagramSocket();
    this.socket.connect(server, port);
  }
  
  public void halt() {
    this.stopped = true; 
  }
  
  public DatagramSocket getSocket() {
    return this.socket; 
  }

  public void run() {

    try {
      BufferedReader userInput 
       = new BufferedReader(new InputStreamReader(System.in));
      while (true) {
        if (stopped) return;
        String theLine = userInput.readLine();
        if (theLine.equals(".")) break;
        byte[] data = theLine.getBytes();
        DatagramPacket output 
         = new DatagramPacket(data, data.length, server, port);
        socket.send(output);
        Thread.yield();
      }
    }  // end try
    catch (IOException ex) {
      System.err.println(ex);
    }

  }  // end run

}
