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

public class Consumer implements Runnable{

    private BlockingQueue<String> queue;

    public Consumer(BlockingQueue<String> q){
        this.queue=q;
    }

    @Override
    public void run() {
        try {
            String msg;
            String station = "";
            if(queue.size() == 14){
                while ((msg = queue.take())!= null) {
                    if (msg.contains("STN")) {
                        station = msg.substring(7, msg.length() - 6);
                        synchronized (queue) {
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
                                    Files.createFile(file);
                                }
                                System.out.println(queue);
                                Files.write(file, queue, Charset.forName("UTF-8"), StandardOpenOption.APPEND);
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                        }
                    }
                }

            }
        }catch(InterruptedException ex){
            ex.printStackTrace();
        }
    }
}