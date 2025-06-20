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
    public synchronized void bind(Client cl) throws RemoteException {
        clients.add(cl);
    }
    public synchronized void unbind(Client cl) throws RemoteException {
        clients.remove(cl);
    }
    public synchronized Collection <Client> clientList() throws RemoteException {
        return new java.util.ArrayList<>(clients);
    }
    public synchronized void send(byte[] m) throws RemoteException {
        if (qtype == QueueType.PUBSUB) {
            for (Client c : clients) {
                try {
                    c.deliver(name, m);
                } catch (RemoteException e) {
                    // Ignore delivery errors at this stage
                }
            }
        }
    }
}
