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
