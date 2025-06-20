// Servidor que implementa la interfaz remota Queue
package broker;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collection;
import mqprot.Queue;
import mqprot.QueueType;
import mqprot.Client;

class QueueImpl extends UnicastRemoteObject implements Queue  {
    public static final long serialVersionUID=1234567890L;
    private Broker broker;
    private String name;
    private QueueType qtype;
    private java.util.ArrayList<Client> clients;
    public QueueImpl(Broker b, String qname, QueueType qtype) throws RemoteException {
        super();
        broker = b;
        name = qname;
        this.qtype = qtype;
        clients = new java.util.ArrayList<>();
    }
    public String getName() throws RemoteException {
        return name;
    }
    public QueueType getQueueType() throws RemoteException {
        return qtype;
    }
    public void bind(Client cl) throws RemoteException {
    }
    public void unbind(Client cl) throws RemoteException {
    }
    public Collection <Client> clientList() throws RemoteException {
        return null;
    }
    public void send(byte[] m) throws RemoteException {
    }
}
