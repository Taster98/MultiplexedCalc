package multiplexedcalc.server;

import multiplexedcalc.Constants;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class Server {
    public static void main(String[] args) throws IOException {
        //Questo è il server. Il server rimane in attesa di connessioni da parte dei clients.
        //La prima cosa che faccio è istanziare un selector:
        Selector selector = Selector.open();
        //Ora devo istanziare un ServerSocketChannel per gestire le connessioni
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        //Creo un inetSocketAddress con indirizzo ip e porta
        InetSocketAddress address = new InetSocketAddress(Constants.IP_ADDRESS,Constants.PORT);
        //E ora faccio il binding tra il canale e l'indirizzo:
        serverSocketChannel.bind(address);
        //Lo setto in modalità non blocking
        serverSocketChannel.configureBlocking(false);
        //Ora registro il serverSocketCHannel per accettare richieste di connessione
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("Server started");
        //Ora mantengo il server attivo in loop
        while(true){
            selector.select();
            //Recepisco l'insieme di selection key e le scandisco per capire a che operazione corrispondono:
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while(iterator.hasNext()){
                //Prendo l'attuale chiave
                SelectionKey key = iterator.next();
                //Controllo che tipo di chiave è:
                if(key.isAcceptable()){
                    //Qui devo accettare la connessione al client
                    SocketChannel client = serverSocketChannel.accept();
                    //Lo setto a non blocking
                    client.configureBlocking(false);
                    //Lo imposto in modalità lettura
                    client.register(selector,SelectionKey.OP_READ);
                }else if(key.isReadable()){
                    handleOperation(key);
                }
                iterator.remove();
            }
        }
    }
    private static void handleOperation(SelectionKey key) throws IOException {
        //Se sono qui invece il client mi ha spedito qualcosa
        SocketChannel client = (SocketChannel) key.channel();
        //Mi creo un buffer
        ByteBuffer buffer = ByteBuffer.allocate(256);
        //Leggo dal client
        client.read(buffer);
        //Mi prendo il risultato dal buffer
        String result = new String(buffer.array()).trim();
        if(result.equals("exit")) {
            client.close();
            return;
        }
        //Delego un thread di gestire l'operazione da fare
        Thread t = new Thread(new Operator(key,client,buffer,result));
        t.start();
    }
}
