package examples.chapter13;
import java.net.*;
import java.io.*;

public class EchoInputThread extends Thread {

  private InetAddress server;
  private DatagramSocket socket;
  private boolean stopped = false;
  private int port;
  
  public EchoInputThread(InetAddress ia, int port) 
   throws SocketException {
    this.server = ia;
    this.socket = new DatagramSocket();
    this.port = port;
  }
  
  public void halt() {
    this.stopped = true; 
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
        DatagramPacket theOutput 
         = new DatagramPacket(data, data.length, server, port);
        socket.send(theOutput);
        Thread.yield();
      }
    }  // end try
    catch (IOException e) {
      System.err.println(e);
    }

  }  // end run

}
