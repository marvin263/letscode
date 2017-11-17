package examples.chapter18;
import java.rmi.*;

public interface SQL extends Remote {

  public String[] SQLQuery(String query) throws RemoteException;

}
