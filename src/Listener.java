import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;

public class Listener implements Runnable{
    //serverSockets
    private ServerSocket serverSocket = null;
    private Socket client = null;
    public int client_no = 0;
    public BlockingQueue<String> queue;
    public int successfulltranfer = 0;
    public int dataloss = 0;


    public void initializeServer() {
        int portNumber = 7789;
        try {
            serverSocket = new ServerSocket(portNumber);
            System.out.println("Initialized the server on port "+portNumber);
        }
        catch(IOException e) {
            System.out.println(e.getMessage());
            System.out.println("Could not listen on port: "+portNumber);
        }
    }

    public void startToListen() {

        while(true) {
            try {
                client = serverSocket.accept();
                client_no++;
            }
            catch(IOException e) {
                System.err.println("Could not accept the request");
            }
            //start a new thread that handles the connection with the client.
            Thread t1 = new Thread(new Producer(client,client_no,queue));
            Thread t2 = new Thread(new Consumer(queue));
            t1.start();
            t2.start();
        }
    }

    public Listener(BlockingQueue queue){
        this.queue = queue;
        initializeServer();
    }

    @Override
    public void run() {
        startToListen();
    }
}//end of connection
