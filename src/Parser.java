import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.BlockingQueue;

public class Parser implements Runnable{

    public BlockingQueue<String> queue;
    public boolean running = true;

    public Parser(BlockingQueue<String> q){
        this.queue = q;
    }

    public synchronized void run(){
        while(running) {
            if (queue.remainingCapacity() == 0) {
                String msg = queue.peek();
                String station = "";
                if (msg.contains("STN")) {
                    station = msg.substring(7, msg.length() - 6);
                    DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                    Date date = new Date();
                    String path = System.getProperty("user.dir") + "\\station_data\\" + station + "\\" + dateFormat.format(date);

                    //Set directory and file
                    Path dir = Paths.get(path);
                    Path file = dir.resolve("data.txt");

                    try {
                        //Check if the directory exists, if not create it
                        if (!Files.exists(dir)) {
                            Files.createDirectories(dir);
                        }
                        //Check if the file exists, if not create it
                        if (!Files.exists(file)) {
                            Files.write(file, queue, Charset.forName("UTF-8"), StandardOpenOption.CREATE);
                        }
                        Files.write(file, queue, Charset.forName("UTF-8"), StandardOpenOption.APPEND);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
                try {
                    Thread.sleep(1000);
                }catch(InterruptedException ex){}
                System.out.println("Next station");
                running = false;
            }else{
                running = false;
            }
        }
    }
}