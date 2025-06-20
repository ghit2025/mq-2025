// Servidor que implementa la interfaz remota MQSrv
package broker;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import mqprot.MQSrv;
import mqprot.Queue;
import mqprot.QueueType;
import mqprot.Client;
import java.util.Collection;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

class Broker extends UnicastRemoteObject implements MQSrv  {
    public static final long serialVersionUID=1234567890L;

    private ArrayList<Client> clients;
    private HashMap<String, Queue> queues;
    private HashMap<Client, Integer> pending;

    public Broker() throws RemoteException {
        super();
        clients = new ArrayList<>();
        queues = new HashMap<>();
        pending = new HashMap<>();
    }
    public int getVersion() throws RemoteException {
        return MQSrv.version;
    }
    public synchronized void addClient(Client cl) throws RemoteException {
        clients.add(cl);
        pending.put(cl, 0);
    }
    public synchronized void removeClient(Client cl) throws RemoteException {
        clients.remove(cl);
        pending.remove(cl);
    }
    public Collection <Client> clientList() throws RemoteException {
        return new ArrayList<>(clients);
    }
    public synchronized void broadcast(byte[] m) throws RemoteException {
        for (Client c : clients) {
            try {
                c.deliver(null, m);
                incPending(c);
            } catch (RemoteException e) {
                // Ignore delivery errors for this basic phase
            }
        }
    }
    public synchronized Queue createQueue(String name, QueueType qc) throws RemoteException {
        Queue q = queues.get(name);
        if (q != null) {
            // if existing queue has a different type, error
            if (!q.getQueueType().equals(qc))
                return null;
            return q;
        }
        q = new QueueImpl(this, name, qc);
        queues.put(name, q);
        return q;
    }
    public Collection <Queue> queueList() throws RemoteException {
        return new ArrayList<>(queues.values());
    }
    public Map <Client, Integer> clientsQueueLength() throws RemoteException {
        return pending;
    }
    public synchronized void ack(Client cl) throws RemoteException {
        Integer v = pending.get(cl);
        if (v != null && v > 0)
            pending.put(cl, v - 1);
    }

    synchronized void incPending(Client cl) {
        pending.put(cl, pending.getOrDefault(cl, 0) + 1);
    }
    static public void main (String args[])  {
        if (args.length!=1) {
            System.err.println("Usage: Broker registryPortNumber");
            return;
        }
        try {
            Broker brk = new Broker();
            Registry registry = LocateRegistry.getRegistry(Integer.parseInt(args[0]));
            registry.rebind("MQ", brk);
        }
        catch (Exception e) {
            System.err.println("Broker exception: " + e.toString());
            System.exit(1);
        }
    }

}
