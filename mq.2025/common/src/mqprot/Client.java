// Interfaz remota del Callback del cliente
// NO MODIFICAR

package mqprot;
import java.rmi.Remote;
import java.rmi.RemoteException;
import mqprot.Queue;

public interface Client extends Remote {
    String getName() throws RemoteException;
    void deliver(String queue, byte[] m) throws RemoteException;
}
