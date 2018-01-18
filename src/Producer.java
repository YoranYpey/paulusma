import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

public class Producer implements Runnable {

    public Socket client;
    public int client_no;
    public BlockingQueue<String> queue;
    public int i = 0;

    public Producer(Socket client, int client_no, BlockingQueue<String> queue) {
        this.client = client;
        this.client_no = client_no;
        this.queue = queue;
        //System.out.println("Ready to receive data from client number: "+client_no);
    }


    public void run() {
        try {
            String clientMessage = null;
            BufferedReader fromClient = new BufferedReader(new InputStreamReader(client.getInputStream(),"UTF-8"));
            while ((clientMessage = fromClient.readLine())!= null) {
                if (clientMessage.equals("	<MEASUREMENT>")) {
                        //System.out.println("Received a new XML-Entry from: "+client_no);
                        for(int i = 0; i < 14; i++) {
                            clientMessage = fromClient.readLine();
                            try {
                                if(!clientMessage.contains("STN")){
                                    queue.put(clientMessage + " " + Thread.currentThread().getName());
                                }else{
                                    queue.put(clientMessage);
                                }
                                //System.out.println("Messages produced: " + clientMessage);
                            } catch (InterruptedException ex) {
                                ex.printStackTrace();
                            }
                        }
                    }
            }
        }catch(IOException e) {
            e.printStackTrace();
        }

    }
}
