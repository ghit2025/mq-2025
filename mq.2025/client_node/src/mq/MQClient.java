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

    private String name;
    private java.util.LinkedList<Message> messages;

    // constructor
    public MQClient(String name) throws RemoteException {
        super();
        this.name = name;
        this.messages = new java.util.LinkedList<>();
    }
    public String getName() throws RemoteException {
        return name;
    }
    public synchronized void deliver(String queue, byte[] m) throws RemoteException {
        messages.add(new Message(queue, m));
    }
    public synchronized Message poll() {
        if (messages.isEmpty())
            return null;
        return messages.removeFirst();
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
