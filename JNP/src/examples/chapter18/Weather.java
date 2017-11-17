package examples.chapter18;
import java.rmi.*;
import java.util.Date;

public interface Weather extends Remote {

  public double getTemperature() throws RemoteException;
  public double getHumidity() throws RemoteException;
  public double getPressure() throws RemoteException;
  public double getWindSpeed() throws RemoteException;
  public double getWindDirection() throws RemoteException;
  public double getLatitude() throws RemoteException;
  public double getLongitude() throws RemoteException;
  public Date   getTime() throws RemoteException;

}
