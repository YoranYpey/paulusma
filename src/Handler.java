import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import java.util.concurrent.BlockingQueue;

public class Handler implements Runnable{

    private Socket client;
    private BlockingQueue<String> queue;
    private BufferedReader buf;
    public boolean running = true;

    public Handler(Socket c, BlockingQueue<String> q){
        this.queue = q;
        this.client = c;

        try {
            buf = new BufferedReader(new InputStreamReader(c.getInputStream()));
        }catch(IOException ex){ex.printStackTrace();}

    }

    public synchronized void run(){
        try {
            while(running) {
                if(buf.readLine().contains("<measure".toUpperCase())){
                    for(int i = 0; i < 14; i++){
                        try {
                            queue.put(buf.readLine());
                        }catch(InterruptedException ex){ ex.printStackTrace();}
                    }
                    try {
                        Thread.sleep(1000);
                    }catch(InterruptedException ex){}
                    running = false;
                    /*for(int i = 0; i < 14; i++){
                        try {
                            System.out.println(queue.take());
                        }catch(InterruptedException ex){ ex.printStackTrace();}
                    }*/
                    //System.out.println("Next station");
                }
            }
        }catch(IOException ex){ex.printStackTrace();}
    }

    public String readSocket(BufferedReader bufRead) {
        String s = "";
        try {
            s = bufRead.readLine();
        }catch(IOException ex){ex.printStackTrace();}
        return s;
    }

}