import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public class Parser{
	
	
	public Parser(){
		System.out.println("Evander is gay");
		List<String> lines = Arrays.asList("The first line", "The second line");
		
		/*try{
			Path file = Paths.get("new-textfile.txt");
			Files.write(file, lines, Charset.forName("UTF-8"));
		}catch(IOException ex){
			System.out.println("IO exception occured");
		}*/
	}
	
	public synchronized void parseQueue(ArrayList<String> messages) throws IOException{
	    ArrayList<String> list = new ArrayList<String>();
	    list.add(messages.get(0));
        list.add(messages.get(1));
        list.add(messages.get(2));
        list.add(messages.get(3));
        list.add(messages.get(5));
        list.add(messages.get(6));
        list.add(" ");

        File file = new File("D:/project2/",list.get(0).substring(7, list.get(0).length() - 6 ) + ".txt");
        if(!file.exists()){
            file.createNewFile();
        }

        Path textfile = Paths.get("D:/project2/" +list.get(0).substring(7, list.get(0).length() - 6 ) + ".txt");
        try {
            Files.write(textfile, list, Charset.forName("UTF-8"), StandardOpenOption.APPEND);
        }catch(IOException ex){}
	}
	
	public synchronized void handleArrayList(ArrayList<String> messages) {
		
		ArrayList<String>MessageList = new ArrayList<String>();
		for (String message : messages) {
		String newMessage = message.replaceAll("[^\\d.:-]", "");
		MessageList.add(newMessage);
		}
		String stn 	= 	MessageList.get(0);
		System.out.println("De STN is: "+stn);
		
		String date = 	MessageList.get(1);
		System.out.println("De date is: "+date);
		
		String time	=	MessageList.get(2);
		System.out.println("De time is: "+time);
		
		String temp = 	MessageList.get(3);
		System.out.println("De temp is: "+temp);
		
		String dewp = 	MessageList.get(4);
		System.out.println("De dewp is: "+dewp);
		
		String stp  = 	MessageList.get(5);
		System.out.println("De stp is: "+stp);
		
		String slp  = 	MessageList.get(6);
		System.out.println("De slp is: "+slp);
		
		String visib=	MessageList.get(7);
		System.out.println("De visib is: "+visib);
		
		String wdsp =	MessageList.get(8);
		System.out.println("De wdsp is: "+wdsp);
		
		String prcp	=	MessageList.get(9);
		System.out.println("De prcp is: "+prcp);
		
		String sndp = 	MessageList.get(10);
		System.out.println("De sndp is: "+sndp);
		
		String frshtt = MessageList.get(11);
		System.out.println("De frshtt is: "+frshtt);
		
		String cldc   = MessageList.get(12);
		System.out.println("De cldc is: "+cldc);
		
		String wnddir = MessageList.get(13);
		System.out.println("De wnddir is: "+wnddir);

		System.out.println("End of XML-Message from client.");
	}

}


