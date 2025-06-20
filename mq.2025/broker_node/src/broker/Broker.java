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

class Broker extends UnicastRemoteObject implements MQSrv  {
    public static final long serialVersionUID=1234567890L;

    public Broker() throws RemoteException {
    }
    public int getVersion() throws RemoteException {
        return MQSrv.version;
    }
    public synchronized void addClient(Client cl) throws RemoteException {
    }
    public synchronized void removeClient(Client cl) throws RemoteException {
    }
    public Collection <Client> clientList() throws RemoteException {
        return null;
    }
    public synchronized void broadcast(byte[] m) throws RemoteException {
    }
    public synchronized Queue createQueue(String name, QueueType qc) throws RemoteException {
        return null;
    }
    public Collection <Queue> queueList() throws RemoteException {
        return null;
    }
    public Map <Client, Integer> clientsQueueLength() throws RemoteException {
        return null;
    }
    public synchronized void ack(Client cl) throws RemoteException {
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
