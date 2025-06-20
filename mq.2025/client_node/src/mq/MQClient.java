// Clase de cliente que proporciona los métodos local del API
package mq;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.rmi.NotBoundException;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import mqprot.MQSrv;
import mqprot.Client;
import mqprot.Queue;

public class MQClient extends UnicastRemoteObject implements Client  {
    public static final long serialVersionUID=1234567890L;

    // constructor
    public MQClient(String name) throws RemoteException {
    }
    public String getName() throws RemoteException {
        return null;
    }
    public synchronized void deliver(String queue, byte[] m) throws RemoteException {
    }
    public synchronized Message poll() {
        return null;
    }
    public MQSrv MQconnect(String host, String port) throws RemoteException, NotBoundException {
        Registry registry = LocateRegistry.getRegistry(host, Integer.parseInt(port));
        return (MQSrv) registry.lookup("MQ");
    }
    // función que serializa un objeto en un array de bytes
    public byte [] serializeMessage(Object o) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(o);
        return bos.toByteArray();
    }
}
