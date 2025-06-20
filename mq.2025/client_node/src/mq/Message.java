// Clase que define el contenedor de un mensaje que se usa en la
// interfaz de las aplicaciones incluyendo el nombre de la cola
// y el contenido del mensaje, que será deserializado al ser accedido.
package mq;
import java.io.IOException;
import java.lang.ClassCastException;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;

public class Message {
    String queue;
    byte [] message;
    public Message(String q, byte [] m) {
        queue = q;
        message = m;
    }
    public String getQueue() {
        return queue;
    }
    public Object getMessage() throws ClassNotFoundException, IOException { // deserializa el mensaje
        ByteArrayInputStream bis = new ByteArrayInputStream(message);
        ObjectInputStream ois = new ObjectInputStream(bis);
        return ois.readObject();
    }
}
