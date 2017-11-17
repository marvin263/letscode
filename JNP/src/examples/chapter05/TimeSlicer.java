package examples.chapter05;
public class TimeSlicer extends Thread {

  private long timeslice;

  public TimeSlicer(long milliseconds, int priority) {

    this.timeslice = milliseconds;
    this.setPriority(priority);
    // If this is the last thread left, it should not
    // stop the VM from exiting
    this.setDaemon(true);

  }

  // Use maximum priority
  public TimeSlicer(long milliseconds) {
    this(milliseconds, 10);
  }

  // Use maximum priority and 100ms timeslices
  public TimeSlicer() {
    this(100, 10);
  }

  public void run() {
  
    while (true) {
      try {
        Thread.sleep(timeslice);
      }
      catch (InterruptedException ex) {
      }
    }
  
  }

}
