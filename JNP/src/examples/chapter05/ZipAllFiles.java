package examples.chapter05;
import java.io.*;
import java.util.*;


public class ZipAllFiles {
  
  public final static int THREAD_COUNT = 4;

  public static void main(String[] args) {

    Vector files = new Vector();
    ZipThread[] threads = new ZipThread[THREAD_COUNT];
    
    for (int i = 0; i < threads.length; i++) {
      threads[i] = new ZipThread(files); 
      threads[i].start();
    }

  }

}