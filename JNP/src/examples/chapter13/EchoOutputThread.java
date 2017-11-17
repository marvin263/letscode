package examples.chapter13;
import java.net.*;
import java.io.*;

class EchoOutputThread extends Thread {

  DatagramSocket socket;
  private boolean stopped = false;

  public EchoOutputThread(DatagramSocket ds) throws SocketException {
    this.socket = ds;
  }

  public void halt() {
    this.stopped = true; 
  }

  public void run() {

    byte[] buffer = new byte[65507];
    while (true) {
      if (stopped) return;
      DatagramPacket dp = new DatagramPacket(buffer, buffer.length);
      try {
        socket.receive(dp);
        String s = new String(dp.getData(), 0, dp.getLength());
        System.out.println(s);
        Thread.yield();
      }
      catch (IOException e) {
        System.err.println(e);
      } 
     
    }  
  
  }

}
