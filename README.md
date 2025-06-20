
MQ: Cola de mensajes con Java RMI
Se trata de un proyecto práctico de desarrollo en grupos de 2 personas, aunque también se puede hacer de manera individual, cuyo plazo de entrega se extiende hasta el final del 17 de junio en la convocatoria ordinaria y hasta el final del 4 de julio en la extraordinaria.

La práctica se puede desarrollar en cualquier sistema que tenga instalado el entorno de desarrollo y ejecución de Java. El material de apoyo de la práctica contiene una serie de scripts que facilitan su compilación y ejecución en un equipo Linux, no requiriéndose incluso la utilización de un IDE.

Para realizar la práctica, puede usarse una máquina personal o cualquiera del conjunto de 4 máquinas asociadas al nombre triqui.fi.upm.es. gestionadas por el centro de cálculo de la escuela. En cualquier caso, como se explicará más adelante, hay que entregar la práctica en triqui.fi.upm.es.
Objetivo de la práctica
Se plantea implementar un sistema de colas de mensajes, similar a los estudiados en la parte teórica de la asignatura, con las siguientes características:

    Basado en un proceso que realiza el rol de broker.
    Con un esquema push, tal que el broker envía los mensajes a los nodos clientes, en vez de ser estos los que los recogen, no siendo necesario, por tanto, almacenar los mensajes en el broker.
    Los mensajes recibidos en el cliente se almacenan localmente, de manera que la aplicación va recogiéndolos a discreción usando el método local poll.
    El modo de operación de una cola es configurable:
        editor-subscriptor: cada mensaje se transmite a todos los clientes asociados (subscritos) a la cola.
        productor-consumidor: cada mensaje se transmite a uno de los clientes asociados a la cola, concretamente, al menos cargado, es decir, al que tenga menos mensajes pendientes de procesar. 

Organización del código de la práctica
Revisemos los distintos directorios:

    ejemplo_JavaRMI: Se trata de ejemplos básicos de Java RMI que usan la misma estructura de directorios que el software de la práctica: un directorio para el servidor (en la práctica, el broker), otro para el cliente y un tercero para almacenar las definiciones comunes a ambos componentes correspondientes a la interfaz del servicio. Asimismo, incluye una colección de scripts para la compilación y ejecución del ejemplo, que son iguales que los que se usan en la práctica. Concretamente, se presentan tres ejemplos:
        1_eco: un cliente-servidor básico.
        2_chat: invocación de métodos del cliente desde el servidor (callback). En la práctica, se usa ese modo de operación para la entrega de los mensajes a los clientes.
        3_banco: uso del patrón factoría. En la práctica, se usa este patrón para la creación de colas. 
    common (paquete mqprot): donde está definida la interfaz de servicio, que ya está completamente programada y no se debe modificar. Incluye cuatro ficheros:
        MQSrv.java: define la interfaz remota del servicio.
        Queue.java: define la interfaz remota de la cola.
        Client.java: define la interfaz remota del cliente.
        QueueType.java: clase que define los tipos de colas disponibles. 
    broker_node (paquete broker): Contiene la funcionalidad del broker:
        Broker.java: Implementación de la interfaz remota de servicio. Inicialmente, incluye la funcionalidad de darse de alta en el registry y la colección de métodos remotos vacíos. Debe completar este fichero.
        QueueImpl.java: Implementación de la interfaz remota de la cola. Inicialmente, incluye la colección de métodos remotos vacíos. Debe completar este fichero. 
    client_node (paquete mq): Contiene la funcionalidad de la biblioteca de cliente que define el API para los clientes:
        MQClient.java: Implementación de la biblioteca del cliente que, inicialmente, incluye una colección de métodos vacíos, así como una función auxiliar para realizar la serialización. Debe completar este fichero.
        Message.java: clase que define el contenedor de un mensaje que se usa en la interfaz de las aplicaciones incluyendo el nombre de la cola y el contenido del mensaje, que será deserializado al ser accedido. No debe modificarse este fichero. 
    client_node (paquete apps): Contiene programas que usan la funcionalidad del sistema desarrollado:
        Test.java: programa interactivo que permite probar la práctica.
        Sender.java: ejemplo que muestra el uso del API para enviar distintos tipos de mensajes.
        Receiver.java: clase complementaria de la anterior, que recibe los mensajes emitidos por esta.
        Point.java: clase de usuario para realizar pruebas. 

Recapitulando, en el desarrollo de la práctica solo se modificarán tres clases:

    Broker.java
    QueueImpl.java
    MQClient.java 

Téngase en cuenta que la práctica está diseñada para no permitir la definición de nuevas clases, estando todas ya presentes, aunque prácticamente vacías en la mayoría de los casos, en el material de apoyo.

Es importante resaltar que en el material de apoyo existen varios enlaces simbólicos que se requieren para poder compartir ficheros entre los distintos directorios. Si copia el material de apoyo directamente, puede perderlos y no funcionará correctamente. En ese caso, puede volver a crearlos o descargarse nuevamente el material de apoyo en otro directorio y copiar los ficheros que haya modificado.
API ofrecida a las aplicaciones
En esta sección, se describen las operaciones que se les proporcionan a las aplicaciones. Vamos a distinguir tres partes: métodos locales, métodos remotos correspondientes al servicio general y métodos remotos asociados a una cola.

A continuación, se describen los métodos locales definidos en MQClient.java que forman parte del API proporcionado a las aplicaciones:

    El constructor de la clase que implementa la biblioteca de cliente. La aplicación comienza creando una instancia de esa clase, en la que especifica el nombre que se usará para identificar al cliente de cara a la depuración del sistema.

    public MQClient(String name) throws RemoteException;

    Se conecta con el broker, que da servicio en la máquina y puertos especificados, devolviendo una referencia remota al servicio general (MQSrv) que permite invocar sus métodos remotos.

    public MQSrv MQconnect(String host, String port) throws RemoteException, NotBoundException;

    Obtiene el primer mensaje en orden FIFO almacenado localmente.

    public Message poll();

    Función para serializar mensajes en un array de bytes.

    public byte [] serializeMessage(Object o) throws IOException;

A continuación, se describen los métodos remotos del servicio general definidos en MQSrv.java que forman parte del API proporcionado a las aplicaciones:

    Obtiene el número de versión del servicio (para depuración).

    int getVersion() throws RemoteException;

    Añade un nuevo cliente al sistema (nótese que la operación broadcast envían el mensaje a todos los clientes). El parámetro especificado es un objeto de tipo remoto que identifica al cliente y permite invocar desde el broker el método del cliente que permite entregarle un mensaje (deliver).

    void addClient(Client cl) throws RemoteException;

    Elimina un cliente.

    void removeClient(Client cl) throws RemoteException;

    Obtiene la lista de clientes en el sistema (para depuración).

    Collection <Client> clientList() throws RemoteException;

    Envía el mensaje especificado a todos los clientes.

    void broadcast(byte[] m) throws RemoteException;

    Crea una cola con el nombre y el tipo especificados devolviendo null en caso de error.

    Queue createQueue(String name, QueueType qc) throws RemoteException;

    Obtiene la lista de colas (para depuración).

    Collection <Queue> queueList() throws RemoteException;

    Obtiene la información que posee el broker sobre el número de mensajes pendientes de procesar que tiene cada cliente (para depuración).

    Map<Client, Integer> clientsQueueLength() throws RemoteException;

    El cliente le indica al broker que ha completado el procesamiento de un mensaje, lo que significa que tiene un mensaje menos pendiente de procesar.

    void ack(Client cl) throws RemoteException;

A continuación, se describen los métodos remotos asociados a una cola definidos en Queue.java que forman parte del API proporcionado a las aplicaciones:

    Obtiene el nombre de la cola (para depuración).

    String getName() throws RemoteException;

    Obtiene el tipo de la cola (para depuración).

        QueueType getQueueType() throws RemoteException;

    Asocia el cliente con la cola. A partir de ese momento, podrá recibir los mensajes dirigidos a esa cola.

        void bind(Client cl) throws RemoteException;

    Elimina la asociación del cliente con la cola.

        void unbind(Client cl) throws RemoteException;

    Obtiene la lista de clientes de una cola (para depuración).

        Collection  clientList() throws RemoteException;

    Envía un mensaje a la cola.

        void send(byte[] m) throws RemoteException;

Para ilustrar el uso de esta API incluimos los ficheros Sender.java y Receiver.java, disponibles en el material de apoyo de la práctica.

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

Ejecución de pruebas del sistema
Aunque todavía no hemos empezado con la funcionalidad, parece conveniente explicar desde el principio cómo se realizan las pruebas en esta práctica. Nótese que, en caso de disponer de varias máquinas, como ocurre si usa triqui, puede arrancar cada componente en un equipo distinto.

El primer paso es descargar el material de apoyo de la práctica, lo que se puede hacer tanto del Moodle de la asignatura como directamente desde esta URL:

wget https://laurel.datsi.fi.upm.es/_media/docencia/asignaturas/sd/mq-2025.tgz

El material de apoyo está empaquetado en un fichero TGZ:

tar xvfz mq-2025.tgz

Para compilar la práctica, existe un script denominado ./compile.sh en cada uno de los 3 directorios de la práctica. También se dispone del script ./compile_all.sh en el directorio raíz de la práctica que va invocando los scripts de compilación de cada directorio.

En cuanto a la ejecución, vamos a suponer que usamos tres máquinas denominadas maq1, maq2 y maq3. Si trabaja en un solo equipo, sustitúyalo por localhost, mientras que, si usa triqui, cámbielo por los nombres de los distintos nodos de ese equipo.

En primer lugar, arrancamos en el mismo equipo el registry y el broker:

maq1$ cd broker_node
maq1$ ./start_rmiregistry.sh 54321 &
maq1$ ./execute_broker.sh 54321

A continuación, arrancamos clientes en el mismo o en otros nodos. En primer lugar, el programa Receiver que espera un plazo de tiempo para recibir mensajes.

maq2$ cd client_node/
maq2$ ./execute.sh Receiver cliente1 maq1 54321

Acto seguido, ejecutamos el programa Sender que envía mensajes de distintos tipos, que serán recibidos por el programa anterior.

maq3$ cd client_node/
maq3$ ./execute.sh Sender cliente2 maq1 54321

En la ventana del primer cliente aparecerá (el valor nulo en el nombre de la cola indica que se trata de un mensaje de tipo broadcast:

maq2$ ./execute.sh Receiver cliente1 maq1 54321
versión 1
	cola "null" mensaje "hola a todos" clase "class java.lang.String"
	cola "Puntos" mensaje "hola" clase "class java.lang.String"
	cola "Puntos" mensaje "[hasta, luego]" clase "class java.util.Arrays$ArrayList"
	cola "Palabras" mensaje "(111,222)" clase "class apps.Point"
	cola "Palabras" mensaje "[(333,444), (2001194062,-1921432959)]" clase "class java.util.Arrays$ArrayList"

Fases en el desarrollo de la práctica
Se plantean 9 fases:

    (0,5 puntos) Primer contacto con el broker.
    (1 punto) Gestión de clientes del sistema.
    (2 puntos) broadcast.
    (0,5 puntos) Creación básica de colas.
    (1 punto) Creación general de colas.
    (1 punto) Gestión de clientes de una cola.
    (1 punto) Envío a cola editor/subscriptor.
    (1,5 puntos) Gestión en el broker de la carga de los clientes.
    (1,5 puntos) Envío a cola productor/consumidor. 

Fase 1: Primer contacto con el broker (0,5 puntos)
En esta fase, se plantea únicamente que el cliente pueda contactar con el broker. Para ello, en el método MQconnect de MQClient.java, se debe localizar al registry y obtener una referencia remota del servicio denominado MQ usando la operación lookup, devolviendo esa referencia como valor de retorno del método. Puede basarse en el cliente del servicio de eco suministrado como ejemplo. Observe que en el main del broker ya está incluida la operación de dar de alta el servicio MQ en el registry mediante la operación rebind. Para probar el funcionamiento de esta fase y verificar que se puede contactar con el broker, en el método getVersion del fichero Broker.java vamos a devolver el número de versión del servicio (la constante version definida en la interfaz MQSrv incluida en el fichero common/src/mqprot/MQSrv.java).
Pruebas
A continuación, se muestra una prueba para comprobar la funcionalidad de este caso. Una vez arrancado el registry y el broker, tal como se explicó previamente, ejecutamos el programa de prueba (en la salida que se muestra de la ejecución del programa aparece en negrilla lo que se teclea, mientras que las respuestas se muestran subrayadas). En las pruebas que se indicarán para las distintas fases, para simplificar, se asumirá que se ejecutan en el mismo equipo, pero puede hacerlo en distintas máquinas cambiando localhost por el nombre de la máquina donde ejecuta el registry y el broker.

$ ./execute.sh Test CL1 localhost 54321
Introduzca operacion (Ctrl-D para terminar)
	operaciones: getVersion|addClient|removeClient|clientList|broadcastString|broadcastClass|poll|createQueue|queueList|bind|unbind|queueClientList|sendString|sendClass|ack|clientsQueueLength
getVersion
getVersion ha devuelto: 1

Fase 2: Gestión de clientes del sistema (1 punto)
El broker gestiona una lista de clientes en el sistema, que serán los receptores de las operaciones de tipo broadcast. Para ello, en el código del broker habrá que definir e iniciar una lista, de manera que se vayan añadiendo a ella los nuevos clientes (addClient) y eliminando los que se dan de baja (removeClient), retornando esa lista en clientList. Nótese que, al implementar MQClient la interfaz remota Client, el parámetro que envía el cliente al broker en addClient, le permite a este invocar de forma remota funciones de la interfaz remota de cliente como getName y deliver.

Asimismo, en el cliente hay que añadir la gestión del nombre del cliente, que se recibe como primer argumento y se usa para depuración. Para ello, hay que completar el método remoto del cliente getName, que permite al broker obtener el nombre de un cliente.
Necesidad de sincronización
Los métodos remotos del servicio general y del servicio asociado a una cola pueden ser invocados concurrentemente por varios clientes. Por tanto, para evitar problemas de sincronización, están definidos con la cláusula synchronized, que asegura que se ejecutan en exclusión mutua.

En el caso del cliente, también existe concurrencia entre la ejecución de los métodos locales y los invocados de forma remota por el broker, usando la misma técnica para eliminar los problemas de sincronización.
Pruebas
Para esta prueba se usarán dos clientes identificados por distinto color y por el uso de cursiva en uno de ellos.

$ ./execute.sh Test CL1 localhost 54321


$ ./execute.sh Test CL2 localhost 54321

A continuación, se muestra la salida mezclada de ambos diferenciándose por el color y el uso de cursiva:

Introduzca operacion (Ctrl-D para terminar)
	operaciones: getVersion|addClient|removeClient|clientList|broadcastString|broadcastClass|poll|createQueue|queueList|bind|unbind|queueClientList|sendString|sendClass|ack|clientsQueueLength
addClient
addClient completado



Introduzca operacion (Ctrl-D para terminar)
	operaciones: getVersion|addClient|removeClient|clientList|broadcastString|broadcastClass|poll|createQueue|queueList|bind|unbind|queueClientList|sendString|sendClass|ack|clientsQueueLength
addClient
addClient completado
Introduzca operacion (Ctrl-D para terminar)
	operaciones: getVersion|addClient|removeClient|clientList|broadcastString|broadcastClass|poll|createQueue|queueList|bind|unbind|queueClientList|sendString|sendClass|ack|clientsQueueLength
clientList
Client List
  Client CL1  Client CL2
Introduzca operacion (Ctrl-D para terminar)
	operaciones: getVersion|addClient|removeClient|clientList|broadcastString|broadcastClass|poll|createQueue|queueList|bind|unbind|queueClientList|sendString|sendClass|ack|clientsQueueLength
removeClient
removeClient completado

Introduzca operacion (Ctrl-D para terminar)
	operaciones: getVersion|addClient|removeClient|clientList|broadcastString|broadcastClass|poll|createQueue|queueList|bind|unbind|queueClientList|sendString|sendClass|ack|clientsQueueLength
clientList
Client List
  Client CL1
Introduzca operacion (Ctrl-D para terminar)
	operaciones: getVersion|addClient|removeClient|clientList|broadcastString|broadcastClass|poll|createQueue|queueList|bind|unbind|queueClientList|sendString|sendClass|ack|clientsQueueLength
removeClient
removeClient completado
Introduzca operacion (Ctrl-D para terminar)
	operaciones: getVersion|addClient|removeClient|clientList|broadcastString|broadcastClass|poll|createQueue|queueList|bind|unbind|queueClientList|sendString|sendClass|ack|clientsQueueLength
clientList
Client List

Fase 3: broadcast (2 puntos)
Esta operación permite enviar un mensaje a todos los clientes activos en el sistema, con independencia de si están asociados a colas o no.

En el método del broker correspondiente a esta operación hay que invocar el método remoto deliver de todos los clientes, incluido el propio emisor. Al tratarse de un envío que no está asociado a ninguna cola, se especificará un valor nulo como parámetro de ese método.

En la parte cliente, hay que gestionar una lista de mensajes tal que el método deliver, invocado desde el servidor, añade un mensaje a la lista, mientras que poll, llamada localmente, extrae el primero de la lista en orden FIFO.
Mensaje como array de bytes vs. como Object
Aunque no afecta al desarrollo de la práctica y no es precisa su lectura, en esta sección se realizan algunas consideraciones sobre el diseño del esquema de serialización usado en la misma. En principio, puede parecer más razonable gestionar en el broker un mensaje como un objeto de tipo Object en vez de como un array de bytes. Java lo permite ya que realiza automáticamente la serialización de cualquier objeto, siempre que disponga del código de la clase. En el caso del broker se necesitaría tener en su JVM el código de las clases de todos los objetos que se puedan enviar en un momento dado. Este requisito no se puede cumplir de forma estática (no es factible almacenar en el broker el código de las clases de todos los objetos que se pueden enviar porque no se conocen a priori), pero sí de forma dinámica, ya que Java dispone de la funcionalidad de la descarga dinámica de código de clases desde una fuente externa mediante la propiedad codebase. Sin embargo, esta opción tiene repercusiones desde el punto de vista de la seguridad (ejecutar código descargado de una fuente externa puede ser peligroso), por lo que se ha optado por serializar explícitamente los mensajes y tratarlos en el broker como arrays de bytes, como se puede apreciar en las aplicaciones proporcionadas como ejemplo.
Pruebas
Para esta prueba se usarán dos clientes identificados por distinto color y por el uso de cursiva en uno de ellos.

$ ./execute.sh Test CL1 localhost 54321


$ ./execute.sh Test CL2 localhost 54321

A continuación, se muestra la salida mezclada de ambos diferenciándose por el color y el uso de cursiva:

Introduzca operacion (Ctrl-D para terminar)
	operaciones: getVersion|addClient|removeClient|clientList|broadcastString|broadcastClass|poll|createQueue|queueList|bind|unbind|queueClientList|sendString|sendClass|ack|clientsQueueLength
addClient
addClient completado


Introduzca operacion (Ctrl-D para terminar)
	operaciones: getVersion|addClient|removeClient|clientList|broadcastString|broadcastClass|poll|createQueue|queueList|bind|unbind|queueClientList|sendString|sendClass|ack|clientsQueueLength
addClient
addClient completado
Introduzca operacion (Ctrl-D para terminar)
	operaciones: getVersion|addClient|removeClient|clientList|broadcastString|broadcastClass|poll|createQueue|queueList|bind|unbind|queueClientList|sendString|sendClass|ack|clientsQueueLength
broadcastString
Se ha hecho broadcast del string "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
Introduzca operacion (Ctrl-D para terminar)
	operaciones: getVersion|addClient|removeClient|clientList|broadcastString|broadcastClass|poll|createQueue|queueList|bind|unbind|queueClientList|sendString|sendClass|ack|clientsQueueLength
broadcastClass
Introduzca el nombre completo de la clase (paquete.clase) de la que quiere enviar un objeto:
apps.Point
Se ha hecho broadcast del objeto (349544327,999727082) de la clase apps.Point
Introduzca operacion (Ctrl-D para terminar)
	operaciones: getVersion|addClient|removeClient|clientList|broadcastString|broadcastClass|poll|createQueue|queueList|bind|unbind|queueClientList|sendString|sendClass|ack|clientsQueueLength
poll
Mensaje recibido (en caso de que haya)
	cola "null" mensaje "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" clase "class java.lang.String"
Introduzca operacion (Ctrl-D para terminar)
	operaciones: getVersion|addClient|removeClient|clientList|broadcastString|broadcastClass|poll|createQueue|queueList|bind|unbind|queueClientList|sendString|sendClass|ack|clientsQueueLength
poll
Mensaje recibido (en caso de que haya)
	cola "null" mensaje "(349544327,999727082)" clase "class apps.Point"

Introduzca operacion (Ctrl-D para terminar)
	operaciones: getVersion|addClient|removeClient|clientList|broadcastString|broadcastClass|poll|createQueue|queueList|bind|unbind|queueClientList|sendString|sendClass|ack|clientsQueueLength
poll
Mensaje recibido (en caso de que haya)
	cola "null" mensaje "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" clase "class java.lang.String"
Introduzca operacion (Ctrl-D para terminar)
	operaciones: getVersion|addClient|removeClient|clientList|broadcastString|broadcastClass|poll|createQueue|queueList|bind|unbind|queueClientList|sendString|sendClass|ack|clientsQueueLength
poll
Mensaje recibido (en caso de que haya)
	cola "null" mensaje "(349544327,999727082)" clase "class apps.Point"
Introduzca operacion (Ctrl-D para terminar)
	operaciones: getVersion|addClient|removeClient|clientList|broadcastString|broadcastClass|poll|createQueue|queueList|bind|unbind|queueClientList|sendString|sendClass|ack|clientsQueueLength
poll
Mensaje recibido (en caso de que haya)

Fase 4: Creación básica de colas (0,5 puntos)
En esta fase, que permite llegar a una calificación de 4 alcanzando el mínimo compensable, se lleva a cabo una primera parte de la operación de creación de una cola:

    En el método createQueue del broker, solo hay que instanciar un objeto de la clase correspondiente a la cola.
    En la clase QueueImpl hay que programar el constructor y los getters correspondientes. 

Pruebas
Para esta prueba se usará un único cliente:

$ ./execute.sh Test CL1 localhost 54321
Introduzca operacion (Ctrl-D para terminar)
	operaciones: getVersion|addClient|removeClient|clientList|broadcastString|broadcastClass|poll|createQueue|queueList|bind|unbind|queueClientList|sendString|sendClass|ack|clientsQueueLength
createQueue
Introduzca el nombre de la cola y el tipo (PUBSUB|PRODCONS)
MyQueue PUBSUB
Se ha creado la cola "MyQueue" de tipo PUBSUB
Introduzca operacion (Ctrl-D para terminar)
	operaciones: getVersion|addClient|removeClient|clientList|broadcastString|broadcastClass|poll|createQueue|queueList|bind|unbind|queueClientList|sendString|sendClass|ack|clientsQueueLength
createQueue
Introduzca el nombre de la cola y el tipo (PUBSUB|PRODCONS)
AnotherQueue PRODCONS
Se ha creado la cola "AnotherQueue" de tipo PRODCONS

Fase 5: Creación general de colas (1 punto)
En esta fase se completa la operación que crea una cola teniendo en cuenta los siguientes aspectos:

    La operación createQueue crea una cola con el nombre y el tipo especificados si esta no existía previamente.
    En caso de que ya existiera, esta operación devuelve una referencia remota a la cola ya existente (es una especie de open).
    Excepto en caso de que existiera previamente, pero con un tipo diferente al solicitado, que se consideraría un error y debería devolver un null. 

De cara a implementar esta funcionalidad, se recomienda el uso de un HashMap que relacione el nombre de la cola y su referencia. Nótese que en el método queueList simplemente se devolvería los valores almacenados en ese mapa. Tenga en cuenta que, aunque la clase HashMap es serializable, no lo son las colecciones devueltas por sus métodos keySet y values. Para solucionar este problema puede envolver estas colecciones en una lista.
Pruebas
Para esta prueba se usará un único cliente:

$ ./execute.sh Test CL1 localhost 54321
Introduzca operacion (Ctrl-D para terminar)
	operaciones: getVersion|addClient|removeClient|clientList|broadcastString|broadcastClass|poll|createQueue|queueList|bind|unbind|queueClientList|sendString|sendClass|ack|clientsQueueLength
createQueue
Introduzca el nombre de la cola y el tipo (PUBSUB|PRODCONS)
Q1 PUBSUB
Se ha creado la cola "Q1" de tipo PUBSUB
Introduzca operacion (Ctrl-D para terminar)
	operaciones: getVersion|addClient|removeClient|clientList|broadcastString|broadcastClass|poll|createQueue|queueList|bind|unbind|queueClientList|sendString|sendClass|ack|clientsQueueLength
createQueue
Introduzca el nombre de la cola y el tipo (PUBSUB|PRODCONS)
Q1 PUBSUB
Se ha creado la cola "Q1" de tipo PUBSUB
Introduzca operacion (Ctrl-D para terminar)
	operaciones: getVersion|addClient|removeClient|clientList|broadcastString|broadcastClass|poll|createQueue|queueList|bind|unbind|queueClientList|sendString|sendClass|ack|clientsQueueLength
createQueue
Introduzca el nombre de la cola y el tipo (PUBSUB|PRODCONS)
Q1 PRODCONS
Error al crear la cola
Introduzca operacion (Ctrl-D para terminar)
	operaciones: getVersion|addClient|removeClient|clientList|broadcastString|broadcastClass|poll|createQueue|queueList|bind|unbind|queueClientList|sendString|sendClass|ack|clientsQueueLength
createQueue
Introduzca el nombre de la cola y el tipo (PUBSUB|PRODCONS)
Q2 PRODCONS
Se ha creado la cola "Q2" de tipo PRODCONS
Introduzca operacion (Ctrl-D para terminar)
	operaciones: getVersion|addClient|removeClient|clientList|broadcastString|broadcastClass|poll|createQueue|queueList|bind|unbind|queueClientList|sendString|sendClass|ack|clientsQueueLength
queueList
Queue List
  Queue Q1 type PUBSUB  Queue Q2 type PRODCONS

Fase 6: Gestión de clientes de una cola (1 punto)
En esta fase hay que gestionar los clientes asociados a una cola. Se trata de una funcionalidad similar a la correspondiente a la segunda fase, pero aplicada a una cola en vez de a todo el sistema (los métodos bind y unbind hacen el mismo rol que addClient y removeClient).
Pruebas
Para esta prueba se usarán dos clientes identificados por distinto color y por el uso de cursiva en uno de ellos.

 
$ ./execute.sh Test CL1 localhost 54321


$ ./execute.sh Test CL2 localhost 54321

 
Introduzca operacion (Ctrl-D para terminar)
	operaciones: getVersion|addClient|removeClient|clientList|broadcastString|broadcastClass|poll|createQueue|queueList|bind|unbind|queueClientList|sendString|sendClass|ack|clientsQueueLength
createQueue
Introduzca el nombre de la cola y el tipo (PUBSUB|PRODCONS)
Q PUBSUB
Se ha creado la cola "Q" de tipo PUBSUB
Introduzca operacion (Ctrl-D para terminar)
	operaciones: getVersion|addClient|removeClient|clientList|broadcastString|broadcastClass|poll|createQueue|queueList|bind|unbind|queueClientList|sendString|sendClass|ack|clientsQueueLength
bind
Introduzca el nombre de la cola
Q
Se ha realizado la operación bind


Introduzca operacion (Ctrl-D para terminar)
	operaciones: getVersion|addClient|removeClient|clientList|broadcastString|broadcastClass|poll|createQueue|queueList|bind|unbind|queueClientList|sendString|sendClass|ack|clientsQueueLength
createQueue
Introduzca el nombre de la cola y el tipo (PUBSUB|PRODCONS)
Q PUBSUB
Se ha creado la cola "Q" de tipo PUBSUB
Introduzca operacion (Ctrl-D para terminar)
	operaciones: getVersion|addClient|removeClient|clientList|broadcastString|broadcastClass|poll|createQueue|queueList|bind|unbind|queueClientList|sendString|sendClass|ack|clientsQueueLength
queueClientList
Introduzca el nombre de la cola
Q
Q Client List
  Client CL1
Introduzca operacion (Ctrl-D para terminar)
	operaciones: getVersion|addClient|removeClient|clientList|broadcastString|broadcastClass|poll|createQueue|queueList|bind|unbind|queueClientList|sendString|sendClass|ack|clientsQueueLength
bind
Introduzca el nombre de la cola
Q
Se ha realizado la operación bind
Introduzca operacion (Ctrl-D para terminar)
	operaciones: getVersion|addClient|removeClient|clientList|broadcastString|broadcastClass|poll|createQueue|queueList|bind|unbind|queueClientList|sendString|sendClass|ack|clientsQueueLength
queueClientList
Introduzca el nombre de la cola
Q
Q Client List
  Client CL1  Client CL2
Introduzca operacion (Ctrl-D para terminar)
	operaciones: getVersion|addClient|removeClient|clientList|broadcastString|broadcastClass|poll|createQueue|queueList|bind|unbind|queueClientList|sendString|sendClass|ack|clientsQueueLength
unbind
Introduzca el nombre de la cola
Q
Se ha realizado la operación unbind

 
Introduzca operacion (Ctrl-D para terminar)
	operaciones: getVersion|addClient|removeClient|clientList|broadcastString|broadcastClass|poll|createQueue|queueList|bind|unbind|queueClientList|sendString|sendClass|ack|clientsQueueLength
queueClientList
Introduzca el nombre de la cola
Q
Q Client List
  Client CL1

Fase 7: Envío a cola editor/subscriptor (1 punto)
En esta fase, se implementa el método send, pero suponiendo que la cola es de tipo editor/subscriptor. Para este tipo de cola, los mensajes enviados a la cola hay que retransmitirlos a todos los clientes asociados/subscritos a la misma. Se trata de una funcionalidad similar al broadcast (fase 3), pero aplicada a una cola en vez de a todo el sistema.
Pruebas
Para esta prueba se usarán dos clientes identificados por distinto color y por el uso de cursiva en uno de ellos.

 
$ ./execute.sh Test CL1 localhost 54321


$ ./execute.sh Test CL2 localhost 54321

 
Introduzca operacion (Ctrl-D para terminar)
	operaciones: getVersion|addClient|removeClient|clientList|broadcastString|broadcastClass|poll|createQueue|queueList|bind|unbind|queueClientList|sendString|sendClass|ack|clientsQueueLength
addClient
addClient completado
Introduzca operacion (Ctrl-D para terminar)
	operaciones: getVersion|addClient|removeClient|clientList|broadcastString|broadcastClass|poll|createQueue|queueList|bind|unbind|queueClientList|sendString|sendClass|ack|clientsQueueLength
createQueue
Introduzca el nombre de la cola y el tipo (PUBSUB|PRODCONS)
Q PUBSUB
Se ha creado la cola "Q" de tipo PUBSUB
Introduzca operacion (Ctrl-D para terminar)
	operaciones: getVersion|addClient|removeClient|clientList|broadcastString|broadcastClass|poll|createQueue|queueList|bind|unbind|queueClientList|sendString|sendClass|ack|clientsQueueLength
bind
Introduzca el nombre de la cola
Q
Se ha realizado la operación bind


Introduzca operacion (Ctrl-D para terminar)
	operaciones: getVersion|addClient|removeClient|clientList|broadcastString|broadcastClass|poll|createQueue|queueList|bind|unbind|queueClientList|sendString|sendClass|ack|clientsQueueLength
addClient
addClient completado
Introduzca operacion (Ctrl-D para terminar)
	operaciones: getVersion|addClient|removeClient|clientList|broadcastString|broadcastClass|poll|createQueue|queueList|bind|unbind|queueClientList|sendString|sendClass|ack|clientsQueueLength
createQueue
Introduzca el nombre de la cola y el tipo (PUBSUB|PRODCONS)
Q PUBSUB
Se ha creado la cola "Q" de tipo PUBSUB
Introduzca operacion (Ctrl-D para terminar)
	operaciones: getVersion|addClient|removeClient|clientList|broadcastString|broadcastClass|poll|createQueue|queueList|bind|unbind|queueClientList|sendString|sendClass|ack|clientsQueueLength
bind
Introduzca el nombre de la cola
Q
Se ha realizado la operación bind
Introduzca operacion (Ctrl-D para terminar)
	operaciones: getVersion|addClient|removeClient|clientList|broadcastString|broadcastClass|poll|createQueue|queueList|bind|unbind|queueClientList|sendString|sendClass|ack|clientsQueueLength
sendString
Introduzca el nombre de la cola
Q
Se ha hecho send del string "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
Introduzca operacion (Ctrl-D para terminar)
	operaciones: getVersion|addClient|removeClient|clientList|broadcastString|broadcastClass|poll|createQueue|queueList|bind|unbind|queueClientList|sendString|sendClass|ack|clientsQueueLength
sendClass
Introduzca el nombre de la cola y el nombre completo de la clase (paquete.clase) de la que quiere enviar un objeto:
Q apps.Point
Se ha hecho send del objeto (-844049814,-496696803) de la clase apps.Point
Introduzca operacion (Ctrl-D para terminar)
	operaciones: getVersion|addClient|removeClient|clientList|broadcastString|broadcastClass|poll|createQueue|queueList|bind|unbind|queueClientList|sendString|sendClass|ack|clientsQueueLength
poll
Mensaje recibido (en caso de que haya)
	cola "Q" mensaje "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" clase "class java.lang.String"
Introduzca operacion (Ctrl-D para terminar)
	operaciones: getVersion|addClient|removeClient|clientList|broadcastString|broadcastClass|poll|createQueue|queueList|bind|unbind|queueClientList|sendString|sendClass|ack|clientsQueueLength
poll
Mensaje recibido (en caso de que haya)
	cola "Q" mensaje "(-844049814,-496696803)" clase "class apps.Point"
Introduzca operacion (Ctrl-D para terminar)
	operaciones: getVersion|addClient|removeClient|clientList|broadcastString|broadcastClass|poll|createQueue|queueList|bind|unbind|queueClientList|sendString|sendClass|ack|clientsQueueLength
poll
Mensaje recibido (en caso de que haya)

 
Introduzca operacion (Ctrl-D para terminar)
	operaciones: getVersion|addClient|removeClient|clientList|broadcastString|broadcastClass|poll|createQueue|queueList|bind|unbind|queueClientList|sendString|sendClass|ack|clientsQueueLength
poll
Mensaje recibido (en caso de que haya)
	cola "Q" mensaje "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" clase "class java.lang.String"
Introduzca operacion (Ctrl-D para terminar)
	operaciones: getVersion|addClient|removeClient|clientList|broadcastString|broadcastClass|poll|createQueue|queueList|bind|unbind|queueClientList|sendString|sendClass|ack|clientsQueueLength
poll
Mensaje recibido (en caso de que haya)
	cola "Q" mensaje "(-844049814,-496696803)" clase "class apps.Point"
Introduzca operacion (Ctrl-D para terminar)
	operaciones: getVersion|addClient|removeClient|clientList|broadcastString|broadcastClass|poll|createQueue|queueList|bind|unbind|queueClientList|sendString|sendClass|ack|clientsQueueLength
poll
Mensaje recibido (en caso de que haya)

Fase 8: Gestión en el broker de la carga de los clientes (1,5 puntos)
Para implementar el envío a una cola productor/consumidor, el broker debe conocer cuántos mensajes tiene pendiente de procesar cada cliente. Esta fase se centra en gestionar esa contabilidad:

    Cada vez que el broker envía un mensaje a un cliente, ya sea con broadcast o con send, se incrementa el número de mensajes pendientes de procesar por ese cliente.
    Cada vez que el cliente invoque al método ack, indicando que ha terminado de procesar un mensaje, el broker decrementa el número de mensajes pendientes de procesar por ese cliente. 

Para implementar esta funcionalidad se recomienda usar un HashMap que relacione el cliente con el número de mensajes que tiene pendiente de procesar. Nótese que el método clientsQueueLength devuelve directamente ese mapa.
Pruebas
Para esta prueba se usarán dos clientes identificados por distinto color y por el uso de cursiva en uno de ellos.

$ ./execute.sh Test CL1 localhost 54321


$ ./execute.sh Test CL2 localhost 54321

 
Introduzca operacion (Ctrl-D para terminar)
	operaciones: getVersion|addClient|removeClient|clientList|broadcastString|broadcastClass|poll|createQueue|queueList|bind|unbind|queueClientList|sendString|sendClass|ack|clientsQueueLength
addClient
addClient completado
Introduzca operacion (Ctrl-D para terminar)
	operaciones: getVersion|addClient|removeClient|clientList|broadcastString|broadcastClass|poll|createQueue|queueList|bind|unbind|queueClientList|sendString|sendClass|ack|clientsQueueLength
createQueue
Introduzca el nombre de la cola y el tipo (PUBSUB|PRODCONS)
Q PUBSUB
Se ha creado la cola "Q" de tipo PUBSUB
Introduzca operacion (Ctrl-D para terminar)
	operaciones: getVersion|addClient|removeClient|clientList|broadcastString|broadcastClass|poll|createQueue|queueList|bind|unbind|queueClientList|sendString|sendClass|ack|clientsQueueLength
bind
Introduzca el nombre de la cola<
Q
Se ha realizado la operación bind


Introduzca operacion (Ctrl-D para terminar)
	operaciones: getVersion|addClient|removeClient|clientList|broadcastString|broadcastClass|poll|createQueue|queueList|bind|unbind|queueClientList|sendString|sendClass|ack|clientsQueueLength
createQueue
Introduzca el nombre de la cola y el tipo (PUBSUB|PRODCONS)
Q PUBSUB
Se ha creado la cola "Q" de tipo PUBSUB
Introduzca operacion (Ctrl-D para terminar)
	operaciones: getVersion|addClient|removeClient|clientList|broadcastString|broadcastClass|poll|createQueue|queueList|bind|unbind|queueClientList|sendString|sendClass|ack|clientsQueueLength
broadcastString
Se ha hecho broadcast del string "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
Introduzca operacion (Ctrl-D para terminar)
	operaciones: getVersion|addClient|removeClient|clientList|broadcastString|broadcastClass|poll|createQueue|queueList|bind|unbind|queueClientList|sendString|sendClass|ack|clientsQueueLength
sendString
Introduzca el nombre de la cola
Q
Se ha hecho send del string "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB"
Introduzca operacion (Ctrl-D para terminar)
	operaciones: getVersion|addClient|removeClient|clientList|broadcastString|broadcastClass|poll|createQueue|queueList|bind|unbind|queueClientList|sendString|sendClass|ack|clientsQueueLength
clientsQueueLength
Client CL1 length 2

 
Introduzca operacion (Ctrl-D para terminar)
	operaciones: getVersion|addClient|removeClient|clientList|broadcastString|broadcastClass|poll|createQueue|queueList|bind|unbind|queueClientList|sendString|sendClass|ack|clientsQueueLength
poll
Mensaje recibido (en caso de que haya)
	cola "null" mensaje "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" clase "class java.lang.String"
Introduzca operacion (Ctrl-D para terminar)
	operaciones: getVersion|addClient|removeClient|clientList|broadcastString|broadcastClass|poll|createQueue|queueList|bind|unbind|queueClientList|sendString|sendClass|ack|clientsQueueLength
ack
se ha realizado ack


Introduzca operacion (Ctrl-D para terminar)
	operaciones: getVersion|addClient|removeClient|clientList|broadcastString|broadcastClass|poll|createQueue|queueList|bind|unbind|queueClientList|sendString|sendClass|ack|clientsQueueLength
clientsQueueLength
Client CL1 length 1

Fase 9: Envío a cola productor/consumidor (1,5 puntos)
En esta fase, se debe completar el método send para tratar el caso de las colas de tipo productor/consumidor: habrá que enviar el mensaje a aquel cliente con menor carga, es decir, que tenga un menor número de mensajes pendientes de procesar.
Pruebas
Para esta prueba se usarán tres clientes identificados por distinto color y por el uso de cursiva en el segundos.

$ ./execute.sh Test CL1 localhost 54321


$ ./execute.sh Test CL2 localhost 54321

$ ./execute.sh Test CL3 localhost 54321

 
Introduzca operacion (Ctrl-D para terminar)
	operaciones: getVersion|addClient|removeClient|clientList|broadcastString|broadcastClass|poll|createQueue|queueList|bind|unbind|queueClientList|sendString|sendClass|ack|clientsQueueLength
addClient
addClient completado
Introduzca operacion (Ctrl-D para terminar)
	operaciones: getVersion|addClient|removeClient|clientList|broadcastString|broadcastClass|poll|createQueue|queueList|bind|unbind|queueClientList|sendString|sendClass|ack|clientsQueueLength
createQueue
Introduzca el nombre de la cola y el tipo (PUBSUB|PRODCONS)
Q1 PUBSUB
Se ha creado la cola "Q1" de tipo PUBSUB
Introduzca operacion (Ctrl-D para terminar)
	operaciones: getVersion|addClient|removeClient|clientList|broadcastString|broadcastClass|poll|createQueue|queueList|bind|unbind|queueClientList|sendString|sendClass|ack|clientsQueueLength
bind
Introduzca el nombre de la cola
Q1
Se ha realizado la operación bind
Introduzca operacion (Ctrl-D para terminar)
	operaciones: getVersion|addClient|removeClient|clientList|broadcastString|broadcastClass|poll|createQueue|queueList|bind|unbind|queueClientList|sendString|sendClass|ack|clientsQueueLength
createQueue
Introduzca el nombre de la cola y el tipo (PUBSUB|PRODCONS)
Q2 PRODCONS
Se ha creado la cola "Q2" de tipo PRODCONS
Introduzca operacion (Ctrl-D para terminar)
	operaciones: getVersion|addClient|removeClient|clientList|broadcastString|broadcastClass|poll|createQueue|queueList|bind|unbind|queueClientList|sendString|sendClass|ack|clientsQueueLength
bind
Introduzca el nombre de la cola
Q2
Se ha realizado la operación bind


Introduzca operacion (Ctrl-D para terminar)
	operaciones: getVersion|addClient|removeClient|clientList|broadcastString|broadcastClass|poll|createQueue|queueList|bind|unbind|queueClientList|sendString|sendClass|ack|clientsQueueLength
addClient
addClient completado
Introduzca operacion (Ctrl-D para terminar)
	operaciones: getVersion|addClient|removeClient|clientList|broadcastString|broadcastClass|poll|createQueue|queueList|bind|unbind|queueClientList|sendString|sendClass|ack|clientsQueueLength
createQueue
Introduzca el nombre de la cola y el tipo (PUBSUB|PRODCONS)
Q1 PUBSUB
Se ha creado la cola "Q1" de tipo PUBSUB
Introduzca operacion (Ctrl-D para terminar)
	operaciones: getVersion|addClient|removeClient|clientList|broadcastString|broadcastClass|poll|createQueue|queueList|bind|unbind|queueClientList|sendString|sendClass|ack|clientsQueueLength
bind
Introduzca el nombre de la cola
Q1
Se ha realizado la operación bind
Introduzca operacion (Ctrl-D para terminar)
	operaciones: getVersion|addClient|removeClient|clientList|broadcastString|broadcastClass|poll|createQueue|queueList|bind|unbind|queueClientList|sendString|sendClass|ack|clientsQueueLength
createQueue
Introduzca el nombre de la cola y el tipo (PUBSUB|PRODCONS)
Q2 PRODCONS
Se ha creado la cola "Q2" de tipo PRODCONS
Introduzca operacion (Ctrl-D para terminar)
	operaciones: getVersion|addClient|removeClient|clientList|broadcastString|broadcastClass|poll|createQueue|queueList|bind|unbind|queueClientList|sendString|sendClass|ack|clientsQueueLength
bind
Introduzca el nombre de la cola
Q2
Se ha realizado la operación bind

Introduzca operacion (Ctrl-D para terminar)
	operaciones: getVersion|addClient|removeClient|clientList|broadcastString|broadcastClass|poll|createQueue|queueList|bind|unbind|queueClientList|sendString|sendClass|ack|clientsQueueLength
broadcastString
Se ha hecho broadcast del string "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
Introduzca operacion (Ctrl-D para terminar)
	operaciones: getVersion|addClient|removeClient|clientList|broadcastString|broadcastClass|poll|createQueue|queueList|bind|unbind|queueClientList|sendString|sendClass|ack|clientsQueueLength
createQueue
Introduzca el nombre de la cola y el tipo (PUBSUB|PRODCONS)
Q1 PUBSUB
Se ha creado la cola "Q1" de tipo PUBSUB
Introduzca operacion (Ctrl-D para terminar)
	operaciones: getVersion|addClient|removeClient|clientList|broadcastString|broadcastClass|poll|createQueue|queueList|bind|unbind|queueClientList|sendString|sendClass|ack|clientsQueueLength
createQueue
Introduzca el nombre de la cola y el tipo (PUBSUB|PRODCONS)
Q2 PRODCONS
Se ha creado la cola "Q2" de tipo PRODCONS
Introduzca operacion (Ctrl-D para terminar)
	operaciones: getVersion|addClient|removeClient|clientList|broadcastString|broadcastClass|poll|createQueue|queueList|bind|unbind|queueClientList|sendString|sendClass|ack|clientsQueueLength
sendString
Introduzca el nombre de la cola
Q1
Se ha hecho send del string "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB"


Introduzca operacion (Ctrl-D para terminar)
	operaciones: getVersion|addClient|removeClient|clientList|broadcastString|broadcastClass|poll|createQueue|queueList|bind|unbind|queueClientList|sendString|sendClass|ack|clientsQueueLength
poll
Mensaje recibido (en caso de que haya)
	cola "null" mensaje "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" clase "class java.lang.String"
Introduzca operacion (Ctrl-D para terminar)
	operaciones: getVersion|addClient|removeClient|clientList|broadcastString|broadcastClass|poll|createQueue|queueList|bind|unbind|queueClientList|sendString|sendClass|ack|clientsQueueLength
ack
se ha realizado ack

Introduzca operacion (Ctrl-D para terminar)
	operaciones: getVersion|addClient|removeClient|clientList|broadcastString|broadcastClass|poll|createQueue|queueList|bind|unbind|queueClientList|sendString|sendClass|ack|clientsQueueLength
clientsQueueLength
Client CL1 length 2
Client CL2 length 1
Introduzca operacion (Ctrl-D para terminar)
	operaciones: getVersion|addClient|removeClient|clientList|broadcastString|broadcastClass|poll|createQueue|queueList|bind|unbind|queueClientList|sendString|sendClass|ack|clientsQueueLength
sendString
Introduzca el nombre de la cola
Q2
Se ha hecho send del string "CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC"

 
Introduzca operacion (Ctrl-D para terminar)
	operaciones: getVersion|addClient|removeClient|clientList|broadcastString|broadcastClass|poll|createQueue|queueList|bind|unbind|queueClientList|sendString|sendClass|ack|clientsQueueLength
poll
Mensaje recibido (en caso de que haya)
	cola "null" mensaje "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" clase "class java.lang.String"
Introduzca operacion (Ctrl-D para terminar)
	operaciones: getVersion|addClient|removeClient|clientList|broadcastString|broadcastClass|poll|createQueue|queueList|bind|unbind|queueClientList|sendString|sendClass|ack|clientsQueueLength
poll
Mensaje recibido (en caso de que haya)
	cola "Q1" mensaje "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB" clase "class java.lang.String"
Introduzca operacion (Ctrl-D para terminar)
	operaciones: getVersion|addClient|removeClient|clientList|broadcastString|broadcastClass|poll|createQueue|queueList|bind|unbind|queueClientList|sendString|sendClass|ack|clientsQueueLength
poll
Mensaje recibido (en caso de que haya)


Introduzca operacion (Ctrl-D para terminar)
	operaciones: getVersion|addClient|removeClient|clientList|broadcastString|broadcastClass|poll|createQueue|queueList|bind|unbind|queueClientList|sendString|sendClass|ack|clientsQueueLength
poll
Mensaje recibido (en caso de que haya)
	cola "Q1" mensaje "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB" clase "class java.lang.String"
Introduzca operacion (Ctrl-D para terminar)
	operaciones: getVersion|addClient|removeClient|clientList|broadcastString|broadcastClass|poll|createQueue|queueList|bind|unbind|queueClientList|sendString|sendClass|ack|clientsQueueLength
poll
Mensaje recibido (en caso de que haya)
	cola "Q2" mensaje "CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC" clase "class java.lang.String"
Introduzca operacion (Ctrl-D para terminar)
	operaciones: getVersion|addClient|removeClient|clientList|broadcastString|broadcastClass|poll|createQueue|queueList|bind|unbind|queueClientList|sendString|sendClass|ack|clientsQueueLength
poll
Mensaje recibido (en caso de que haya)

Entrega de la práctica
Se realizará en la máquina triqui, usando el mandato:

entrega.sd mq.2025

Este mandato recogerá los siguientes ficheros:

    autores.txt Fichero con los datos de los autores con este formato:

    DNI APELLIDOS NOMBRE MATRÍCULA

    client_node/src/mq/MQClient.java: Código de la biblioteca de cliente.
    broker_node/src/broker/Broker.java: Código del broker.
    broker_node/src/broker/QueueImpl.java: Código de las operaciones sobre las colas. 

