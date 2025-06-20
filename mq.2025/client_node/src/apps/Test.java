// Cliente para probar el servicio.
package apps;
import java.util.Scanner;
import java.util.ArrayList;
import java.rmi.RemoteException;
import java.rmi.NotBoundException;
import java.io.IOException;
import java.lang.ClassNotFoundException;
import java.lang.IllegalArgumentException ;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.HashMap;
import mqprot.QueueType;
import mqprot.MQSrv;
import mqprot.Queue;
import mqprot.Client;
import mq.MQClient;
import mq.Message;

class Test {
    static char car='A';
    static boolean clntAdded = false;
    static private HashMap<String, Queue> qmap;
    static private void prompt() {
        System.err.println("Introduzca operacion (Ctrl-D para terminar)");
        System.err.println("\toperaciones: getVersion|addClient|removeClient|clientList|broadcastString|broadcastClass|poll|createQueue|queueList|bind|unbind|queueClientList|sendString|sendClass|ack|clientsQueueLength");
    }
    static private boolean doGetVersion(MQClient cln, MQSrv srv,  Scanner ent) throws RemoteException {
        System.err.println("getVersion ha devuelto: " + srv.getVersion());
        return true;
    }
    static private boolean doAddClient(MQClient cln, MQSrv srv,  Scanner ent) throws RemoteException {
        if (clntAdded)
            System.err.println("cliente ya está añadido");
	else {
            srv.addClient(cln);
            System.err.println("addClient completado");
            clntAdded=true;
	}
        return true;
    }
    static private boolean doRemoveClient(MQClient cln, MQSrv srv,  Scanner ent) throws RemoteException {
        if (!clntAdded)
            System.err.println("cliente no estaba añadido");
        else {
            srv.removeClient(cln);
            System.err.println("removeClient completado");
            clntAdded=false;
	}
        return true;
    }
    static private boolean doClientList(MQClient cln, MQSrv srv,  Scanner ent) throws RemoteException {
        System.out.println("Client List ");
        for (Client c: srv.clientList())
                System.out.print("  Client " + c.getName());
        System.out.println("");
        return true;
    }
    static private boolean doBroadcastString(MQClient cln, MQSrv srv,  Scanner ent) throws RemoteException, IOException {
        int tam = 32;
        byte [] buf = new byte[tam];
        for (int i=0; i<tam; i++) buf[i] = (byte) car;
        ++car;
        String mess = new String(buf);
	srv.broadcast(cln.serializeMessage(mess));
        System.err.println("Se ha hecho broadcast del string \"" + mess + "\"");
        return true;
    }
    static private boolean doBroadcastClass(MQClient cln, MQSrv srv,  Scanner ent) throws RemoteException, IOException {
        System.err.println("Introduzca el nombre completo de la clase (paquete.clase) de la que quiere enviar un objeto: ");
        if (!ent.hasNextLine()) return false;
        String lin = ent.nextLine();
        Scanner s = new Scanner(lin);
        if  (!s.hasNext()) return false;
        String clase = s.next();
        Object o;
        try {
            o = Class.forName(clase).getConstructor().newInstance();
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            System.err.println("error en clase especificada");
            return false;
        }
	srv.broadcast(cln.serializeMessage(o));
        System.err.println("Se ha hecho broadcast del objeto " + o + " de la clase " + clase);
        return true;
    }
    static private boolean doPoll(MQClient cln, MQSrv srv,  Scanner ent) throws RemoteException, ClassNotFoundException, IOException {
        System.err.println("Mensaje recibido (en caso de que haya)");
        Message m;
        if ((m = cln.poll())!=null) {
            Object o;
            System.err.println("\tcola \"" + m.getQueue() + "\" mensaje \"" + (o = m.getMessage()) + "\" clase \"" + o.getClass() + "\"");
        }
        return true;
    }
    static private boolean doCreateQueue(MQClient cln, MQSrv srv,  Scanner ent) throws RemoteException {
        System.err.println("Introduzca el nombre de la cola y el tipo (PUBSUB|PRODCONS)");
        if (!ent.hasNextLine()) return false;
        String lin = ent.nextLine();
        Scanner s = new Scanner(lin);
        if  (!s.hasNext()) return false;
        String qname = s.next();
        if  (!s.hasNext()) return false;
        String type = s.next(); 
        QueueType qt = null;
	try {
       	    qt = QueueType.valueOf(type);
	} catch (IllegalArgumentException e) {}
	if (qt == null) return false;
	Queue q = srv.createQueue(qname, qt);
	if (q == null)
            System.err.println("Error al crear la cola");
	else {
	    qmap.put(qname, q);
            System.err.println("Se ha creado la cola \"" + q.getName() + "\" de tipo " + q.getQueueType());
        }
        return true;
    }
    static private boolean doQueueList(MQClient cln, MQSrv srv,  Scanner ent) throws RemoteException {
        System.out.println("Queue List ");
        for (Queue q: srv.queueList())
                System.out.print("  Queue " + q.getName() + " type " + q.getQueueType());
        System.out.println("");
        return true;
    }
    static private boolean doBind(MQClient cln, MQSrv srv,  Scanner ent) throws RemoteException {
        System.err.println("Introduzca el nombre de la cola");
        if (!ent.hasNextLine()) return false;
        String lin = ent.nextLine();
        Scanner s = new Scanner(lin);
        if  (!s.hasNext()) return false;
        String qname = s.next();
        Queue q = qmap.get(qname);
	if (q == null) return false;
        q.bind(cln);
        System.err.println("Se ha realizado la operación bind");
        return true;
    }
    static private boolean doUnbind(MQClient cln, MQSrv srv,  Scanner ent) throws RemoteException {
        System.err.println("Introduzca el nombre de la cola");
        if (!ent.hasNextLine()) return false;
        String lin = ent.nextLine();
        Scanner s = new Scanner(lin);
        if  (!s.hasNext()) return false;
        String qname = s.next();
        Queue q = qmap.get(qname);
	if (q == null) return false;
        q.unbind(cln);
        System.err.println("Se ha realizado la operación unbind");
        return true;
    }
    static private boolean doQueueClientList(MQClient cln, MQSrv srv,  Scanner ent) throws RemoteException {
        System.err.println("Introduzca el nombre de la cola");
        if (!ent.hasNextLine()) return false;
        String lin = ent.nextLine();
        Scanner s = new Scanner(lin);
        if  (!s.hasNext()) return false;
        String qname = s.next();
	Queue q = qmap.get(qname);
	if (q == null) return false;
        System.out.println(qname + " Client List ");
        for (Client c: q.clientList())
                System.out.print("  Client " + c.getName());
        System.out.println("");
        return true;
    }
    static private boolean doSendString(MQClient cln, MQSrv srv,  Scanner ent) throws RemoteException, IOException {
        System.err.println("Introduzca el nombre de la cola");
        if (!ent.hasNextLine()) return false;
        String lin = ent.nextLine();
        Scanner s = new Scanner(lin);
        if  (!s.hasNext()) return false;
        String qname = s.next();
        Queue q = qmap.get(qname);
        if (q == null) return false;
        int tam = 32;
        byte [] buf = new byte[tam];
        for (int i=0; i<tam; i++) buf[i] = (byte) car;
        ++car;
        String mess = new String(buf);
	q.send(cln.serializeMessage(mess));
        System.err.println("Se ha hecho send del string \"" + mess + "\"");
        return true;
    }
    static private boolean doSendClass(MQClient cln, MQSrv srv,  Scanner ent) throws RemoteException, IOException {
        System.err.println("Introduzca el nombre de la cola y el nombre completo de la clase (paquete.clase) de la que quiere enviar un objeto: ");
        if (!ent.hasNextLine()) return false;
        String lin = ent.nextLine();
        Scanner s = new Scanner(lin);
        if  (!s.hasNext()) return false;
        String qname = s.next();
        Queue q = qmap.get(qname);
        if (q == null) return false;
        if  (!s.hasNext()) return false;
        String clase = s.next();
        Object o;
        try {
            o = Class.forName(clase).getConstructor().newInstance();
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            System.err.println("error en clase especificada");
            return false;
        }
	q.send(cln.serializeMessage(o));
        System.err.println("Se ha hecho send del objeto " + o + " de la clase " + clase);
        return true;
    }
    static private boolean doAck(MQClient cln, MQSrv srv,  Scanner ent) throws RemoteException {
        srv.ack(cln);
        System.err.println("se ha realizado ack");
        return true;
    }
    static private boolean doClientsQueueLength(MQClient cln, MQSrv srv,  Scanner ent) throws RemoteException {
        Map <Client, Integer> map = srv.clientsQueueLength();
        for (Map.Entry<Client, Integer> entry : map.entrySet())
            System.err.println("Client " + entry.getKey().getName() + " length " + entry.getValue());
        return true;
    }
    static public void main(String args[]) {
        if (args.length!=3) {
            System.err.println("Usage: Test name registryHost registryPort");
            return;
        }
	qmap = new HashMap<>();
        try {
            MQClient cl = new MQClient(args[0]);
            MQSrv srv = cl.MQconnect(args[1], args[2]);
            if (cl == null) System.exit(0);
            while (true) {
                boolean formatoOK = false;
                Scanner ent = new Scanner(System.in);
                prompt();
                if (!ent.hasNextLine()) System.exit(0);
                String lin = ent.nextLine();
                Scanner s = new Scanner(lin);
                if (s.hasNext()) {
                   String op = s.next();
                   switch (op) {
                       case "getVersion": formatoOK = doGetVersion(cl, srv, ent); break;
                       case "addClient": formatoOK = doAddClient(cl, srv, ent); break;
                       case "removeClient": formatoOK = doRemoveClient(cl, srv, ent); break;
                       case "clientList": formatoOK = doClientList(cl, srv, ent); break;
                       case "broadcastString": formatoOK = doBroadcastString(cl, srv, ent); break;
                       case "broadcastClass": formatoOK = doBroadcastClass(cl, srv, ent); break;
                       case "poll": formatoOK = doPoll(cl, srv, ent); break;
                       case "createQueue": formatoOK = doCreateQueue(cl, srv, ent); break;
                       case "queueList": formatoOK = doQueueList(cl, srv, ent); break;
                       case "bind": formatoOK = doBind(cl, srv, ent); break;
                       case "unbind": formatoOK = doUnbind(cl, srv, ent); break;
                       case "queueClientList": formatoOK = doQueueClientList(cl, srv, ent); break;
                       case "sendString": formatoOK = doSendString(cl, srv, ent); break;
                       case "sendClass": formatoOK = doSendClass(cl, srv, ent); break;
                       case "ack": formatoOK = doAck(cl, srv, ent); break;
                       case "clientsQueueLength": formatoOK = doClientsQueueLength(cl, srv, ent); break;
                    }
                }
                if (!formatoOK)
                     System.err.println("Error en formato de operacion");
            }
        } catch (NotBoundException e) {
            System.err.println("error localizando registry "+ e.toString());
            return;
        } catch (RemoteException e) {
            System.err.println("error de comunicación "+ e.toString());
            return;
        } catch (Exception e) {
            System.err.println("excepción en la ejecución del Test: " + e.toString());
	    e.printStackTrace();
        }
        finally {
            System.exit(0);
        }

    }
}
