import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

public class Listener {
    //serverSockets
    public Socket client;
    public ServerSocket serverSocket;
    public int client_no = 0;
    public int successfulltranfer = 0;
    public int dataloss = 0;
    private final int PORT = 7789;


    public void initializeServer() {
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Initialized the server on port "+ PORT);
        }
        catch(IOException e) {
            System.out.println(e.getMessage());
            System.out.println("Could not listen on port: "+ PORT);
        }
    }

    public void startToListen() {
        try {
            client = serverSocket.accept();
            client_no++;
        }
        catch(IOException e) {
            System.err.println("Could not accept the request");
        }
    }

    public Listener(){
        initializeServer();
        startToListen();
    }

    public Socket getClient(){
        return client;
    }
}//end of connection
