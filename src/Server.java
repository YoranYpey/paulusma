import java.io.IOException;
import java.net.*;
import java.util.concurrent.BlockingQueue;

public class Server implements Runnable{

    private ServerSocket serverSocket;
    private static final int PORT = 7789;
    private BlockingQueue<String> queue;


    public Server(BlockingQueue<String> q){
        this.queue = q;
    }

    public void run() {
        try {
            serverSocket = new ServerSocket(PORT);
        }catch(IOException ex){ex.printStackTrace();}
        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();
                Thread t = new Thread(new Handler(clientSocket, queue));
                t.start();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}