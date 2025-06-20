// Ejemplo de programa que usa MQ.
// Crea colas y envía mensajes de distintos tipos

package apps;
import java.rmi.RemoteException;
import java.rmi.NotBoundException;
import java.lang.ClassCastException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import mqprot.QueueType;
import mqprot.MQSrv;
import mqprot.Queue;
import mqprot.Client;
import mq.MQClient;
import mq.Message;

class Sender {
    static public void main(String args[]) {
        if (args.length!=3) {
            System.err.println("Usage: Sender name registryHost registryPort");
            return;
        }
        try {
            MQClient cl = new MQClient(args[0]);
            MQSrv srv = cl.MQconnect(args[1], args[2]);

	    srv.broadcast(cl.serializeMessage("hola a todos"));

            Queue q1 = srv.createQueue("Puntos", QueueType.PUBSUB);
            Queue q2 = srv.createQueue("Palabras", QueueType.PRODCONS);

           // envío de un objeto de una clase estándar
            q1.send(cl.serializeMessage("hola"));

            // envío de una colección que contiene objetos de una clase estándar
            List<String> lpa = Arrays.asList("hasta", "luego");
            q1.send(cl.serializeMessage(lpa));

            // envío de un objeto de una clase definida por el usuario
            Point p = new Point(111, 222);
            q2.send(cl.serializeMessage(p));

            // envío de colección con objetos de una clase definida por el usuario
            List<Point> lpo=Arrays.asList(new Point(333, 444), new Point());
            q2.send(cl.serializeMessage(lpo));

	    System.exit(0);

        } catch (NotBoundException e) {
            System.err.println("error localizando registry "+ e.toString());
	    System.exit(0);
        } catch (RemoteException e) {
            System.err.println("error de comunicación "+ e.toString());
	    System.exit(0);
        } catch (IOException|ClassCastException e) {
            System.err.println("error en serialización "+ e.toString());
	    System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
	    System.exit(0);
        }

    }
}
