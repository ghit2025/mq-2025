// Ejemplo de programa que usa MQ.
// Crea colas y recibe mensajes de distintos tipos

package apps;
import java.rmi.RemoteException;
import java.rmi.NotBoundException;
import java.lang.ClassCastException;
import java.io.IOException;
import mqprot.QueueType;
import mqprot.MQSrv;
import mqprot.Queue;
import mqprot.Client;
import mq.MQClient;
import mq.Message;
import java.util.concurrent.TimeUnit;

class Receiver {
    static public void main(String args[]) {
        if (args.length!=3) {
            System.err.println("Usage: Receiver name registryHost registryPort");
            return;
        }
        try {
            MQClient cl = new MQClient(args[0]);
            MQSrv srv = cl.MQconnect(args[1], args[2]);
            System.out.println("versión " + srv.getVersion());
	    srv.addClient(cl);
            Queue q1 = srv.createQueue("Puntos", QueueType.PUBSUB);
            Queue q2 = srv.createQueue("Palabras", QueueType.PRODCONS);
	    q1.bind(cl);
	    q2.bind(cl);
	    // espera un tiempo para recoger los mensajes
            TimeUnit.SECONDS.sleep(10);
	    Message m;
            while ((m = cl.poll())!=null) {
                Object o;
		System.err.println("\tcola \"" + m.getQueue() + "\" mensaje \"" + (o = m.getMessage()) + "\" clase \"" + o.getClass() + "\"");
		srv.ack(cl);
	    }
	    q1.unbind(cl);
	    q2.unbind(cl);
	    srv.removeClient(cl);
	    System.exit(0);

        } catch (NotBoundException e) {
            System.err.println("error localizando registry "+ e.toString());
	    System.exit(0);
        } catch (RemoteException e) {
            System.err.println("error de comunicación "+ e.toString());
	    System.exit(0);
        } catch (IOException|ClassNotFoundException|ClassCastException e) {
            System.err.println("error en serialización "+ e.toString());
	    System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
	    System.exit(0);
        }
    }
}
