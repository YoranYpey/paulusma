import java.io.IOException;
import java.net.*;
import java.util.concurrent.BlockingQueue;

/*  j  av a  2s . c  o  m*/
public class Server implements Runnable{

    private ServerSocket serverSocket;
    private static final int PORT = 7789;
    private BlockingQueue<String> queue;
    private Socket clientSocket;


    public Server(BlockingQueue<String> q){
        this.queue = q;
    }

    public void run() {
        try {
            serverSocket = new ServerSocket(PORT);
        }catch(IOException ex){}
        while(true) {
            try {
                clientSocket = serverSocket.accept();
                Thread t = new Thread(new Handler(clientSocket, queue));
                t.start();
                Thread t2 = new Thread(new Parser(queue));
                t2.start();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}