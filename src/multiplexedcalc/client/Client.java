package multiplexedcalc.client;

import multiplexedcalc.Constants;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) throws IOException, InterruptedException {
        //Intanto mi voglio collegare al server
        InetSocketAddress address = new InetSocketAddress(Constants.IP_ADDRESS, Constants.PORT);
        //Poi creo un socket per collegarmi col server
        SocketChannel client = SocketChannel.open(address);
        System.out.println("Connected");
        System.out.print("MultiplexedCalc>");
        Scanner sc = new Scanner(System.in);
        String input = sc.nextLine();
        //Salvo un buffer per le risposte
        ByteBuffer readBuffer = ByteBuffer.allocate(2048);
        while(!input.equals("exit")){
            //Qui processo i comandi del client
            //Converto l'input in array di byte
            byte[] toSend = input.getBytes();
            //Converto l'array di byte in ByteBuffer
            ByteBuffer buffer = ByteBuffer.wrap(toSend);
            //Scrivo sul buffer
            client.write(buffer);
            //Pulisco il buffer
            buffer.clear();
            //Stampo la risposta dal server
            readBuffer.clear();
            client.read(readBuffer);
            readBuffer.flip();
            System.out.println(new String(readBuffer.array()));
            Thread.sleep(10);
            //Acquisisco nuovo input
            System.out.print("MultiplexedCalc>");
            input = sc.nextLine();
        }
        byte[] toSend = input.getBytes();
        //Converto l'array di byte in ByteBuffer
        ByteBuffer buffer = ByteBuffer.wrap(toSend);
        //Scrivo sul buffer
        client.write(buffer);
        Thread.sleep(10);
        client.close();
    }
}
