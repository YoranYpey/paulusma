import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Listener {
	//serverSockets
		private ServerSocket serverSocket = null;
		private Socket client = null;
		public int client_no = 0;
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
		Thread t1 = new Thread(new Handler(client,client_no,this));
		 t1.start();
	 }
}

public static void main(String[] args){
	Listener listener = new Listener();
	listener.initializeServer();
	listener.startToListen();
}


}//end of connection
