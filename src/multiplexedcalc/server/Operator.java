package multiplexedcalc.server;

import multiplexedcalc.Constants;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class Operator implements Runnable{
    private SelectionKey key;
    private SocketChannel client;
    private ByteBuffer buffer;
    private String result;
    public Operator(SelectionKey key, SocketChannel client, ByteBuffer buffer, String result){
        this.key = key;
        this.buffer = buffer;
        this.client = client;
        this.result = result;
    }

    private static float add(float a, float b){
        return a+b;
    }
    private static float sub(float a, float b){
        return a-b;
    }
    private static float mul(float a, float b){
        return a*b;
    }
    private static float div(float a, float b){
        return a/b;
    }


    @Override
    public void run() {
        float res = 0;
        String[] splitted = result.split(" ",3);
        if(splitted[0].equals("help")){
            error(Constants.HELP);
            return;
        }
        if(splitted.length == 3){
            //Controllo che siano inseriti solo numeri
            float a;
            float b;
            try{
                a = Float.parseFloat(splitted[1]);
                b = Float.parseFloat(splitted[2]);
            }catch (NumberFormatException | NullPointerException e){
                String reply = "Error: You must insert numbers in order to calculate any result";
                error(reply);
                return;
            }
            //Scandisco il comando
            switch(splitted[0]){
                case Constants.ADD:
                    res = add(a,b);
                    break;
                case Constants.SUB:
                    res = sub(a,b);
                    break;
                case Constants.MUL:
                    res = mul(a,b);
                    break;
                case Constants.DIV:
                    if(b == 0){
                        res = div(a,b);
                    }else{
                        String reply = "Error: Division by 0 not allowed.";
                        error(reply);
                        return;
                    }
                    break;
                default:
                    String reply = "Error: Illegal command.";
                    error(reply);
                    break;
            }
        }else{
            //Error handling
            String reply = "Wrong usage: [operation] [op1] [op2]";
            error(reply);
        }
        String reply = String.valueOf(res);
        buffer.compact();
        buffer.put(reply.getBytes());
        buffer.clear();
        try {
            client.write(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void error(String reply) {
        buffer.clear();
        buffer.put(reply.getBytes());
        buffer.clear();
        try {
            client.write(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
