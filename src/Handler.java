<<<<<<< HEAD
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;

public class Handler implements Runnable {
	
	public Socket client;
	public int client_no;
	public Listener listener;
	public Parser parser;
	
	public Handler(Socket client,int client_no, Listener listener) {
		this.client = client;
		this.client_no = client_no;
		this.listener = listener;
		parser = new Parser();
		System.out.println("Ready to receive data from client number: "+client_no);
	}


	public void run() {
		try {
			String clientMessage = null;
			BufferedReader fromClient = new BufferedReader(new InputStreamReader(client.getInputStream(),"UTF-8"));
			int counter = 0;
			while ((clientMessage = fromClient.readLine())!= null) {
				if (clientMessage.equals("	<MEASUREMENT>")) {
					ArrayList<String> MessageList = new ArrayList<String>();
					System.out.println("Received a new XML-Entry from: "+client_no);
					while(counter<14) {
					clientMessage = fromClient.readLine();
					MessageList.add(clientMessage);
					counter++;
					}
				if(counter == 14) {
					//parser.handleArrayList(MessageList);
					parser.parseQueue(MessageList);
					counter =0;
					}
				}
			}
		}catch(IOException e) {
			e.printStackTrace();
		}
		
}	
}
=======
import java.net.Socket;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Handler implements Runnable{

    private BlockingQueue<String> queue;
    private BufferedReader buf;
    private String[] itemList = {"STN", "DATE", "TIME", "TEMP", "STP", "SLP"};

    public Handler(Socket c, BlockingQueue<String> q){
        this.queue = q;
        try {
            buf = new BufferedReader(new InputStreamReader(c.getInputStream()));
        }catch(IOException ex){ex.printStackTrace();}

    }

    public void run(){
            try {
                while (true) {
                    if (buf.readLine().contains("<measure".toUpperCase())) {
                        writeQueue();
                        //Thread thread = new Thread(new Parser(queue));
                        //thread.start();
                    } else {
                        buf.readLine();
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
    }

    private void writeQueue() {
        synchronized (queue) {
            try {
                for (int i = 0; i < 14; i++) {
                    try {
                        String c = buf.readLine();
                        if(stringContainsItemFromList(c, itemList)) {
                            queue.put(c);
                            System.out.println(c);
                        }
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private static boolean stringContainsItemFromList(String inputStr, String[] items) {
        return Arrays.stream(items).parallel().anyMatch(inputStr::contains);
    }

}
>>>>>>> 01c9b7e80a545147d0004d52a8f794327e7b3e14
