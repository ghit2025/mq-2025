// Interfaz remota de MQ
// NO MODIFICAR

package mqprot;
import java.rmi.Remote;
import java.rmi.RemoteException;
import mqprot.QueueType;
import mqprot.Client;
import java.util.Collection;
import java.util.Map;

public interface MQSrv extends Remote {
    public static final int version=1;
    int getVersion() throws RemoteException;
    void addClient(Client cl) throws RemoteException;
    void removeClient(Client cl) throws RemoteException;
    Collection <Client> clientList() throws RemoteException;
    void broadcast(byte[] m) throws RemoteException;
    Queue createQueue(String name, QueueType qc) throws RemoteException;
    Collection <Queue> queueList() throws RemoteException;
    Map<Client, Integer> clientsQueueLength() throws RemoteException;
    void ack(Client cl) throws RemoteException;
}
