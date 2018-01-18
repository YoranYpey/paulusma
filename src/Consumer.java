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
        try{
            //consuming messages until exit message is received
            DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            Date date = new Date();
            String station = queue.peek().substring(7, queue.peek().length() - 6);
            String path = System.getProperty("user.dir") + "\\Test\\" + station + "\\" + dateFormat.format(date);

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
                Files.write(file, queue, Charset.forName("UTF-8"), StandardOpenOption.APPEND);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }catch(Exception e){}
    }
}