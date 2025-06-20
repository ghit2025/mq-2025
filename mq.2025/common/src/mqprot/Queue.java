// Interfaz remota de la cola
// NO MODIFICAR

package mqprot;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Collection;
import mqprot.Client;

public interface Queue extends Remote {
    String getName() throws RemoteException;
    QueueType getQueueType() throws RemoteException;
    void bind(Client cl) throws RemoteException;
    void unbind(Client cl) throws RemoteException;
    Collection <Client> clientList() throws RemoteException;
    void send(byte[] m) throws RemoteException;
}
