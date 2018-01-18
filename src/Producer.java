import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

public class Producer implements Runnable {

    private BlockingQueue<String> queue;
    private Listener listener;
    private Socket client;
    private int counter = 0;

    public Producer(BlockingQueue<String> q, Socket client, Listener listener){
        this.queue=q;
        this.client = client;
        this.listener = listener;
    }

    @Override
    public void run() {
        try{
            String clientMessage;
            BufferedReader fromClient = new BufferedReader(new InputStreamReader(client.getInputStream(), "UTF-8"));
            while((clientMessage = fromClient.readLine()) != null){
                if(clientMessage.equals("	<MEASUREMENT>")){
                    clientMessage = fromClient.readLine();
                    while(counter < 14){
                        try{
                            queue.put(clientMessage);
                        }catch(InterruptedException ex){
                            ex.printStackTrace();
                        }
                        counter++;
                    }
                    counter=0;
                }else{
                    System.out.println("Message not found");
                }
            }
        }catch(IOException ex){ex.printStackTrace();}

    }

}