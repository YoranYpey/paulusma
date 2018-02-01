import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.BlockingQueue;

public class Parser implements Runnable{

    private BlockingQueue<String> queue;

    public Parser(BlockingQueue<String> q){
        this.queue = q;
    }

    public void run(){
        while(true) {
            if (queue.remainingCapacity() == 0) {
                writeToFile();
            }
        }
    }

    private void writeToFile(){
        String msg = queue.peek();
        String station = "";
        if (msg.contains("STN")) {
            station = msg.substring(7, msg.length() - 6);
            int year = Calendar.getInstance().get(Calendar.YEAR);
            int month = Calendar.getInstance().get(Calendar.MONTH)+1;
            int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
            String path = System.getProperty("user.dir") + "/station_data/" + station + "/" + year + "/" + month + "/" + day;

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
                    queue.clear();

                } else {
                    Files.write(file, queue, Charset.forName("UTF-8"), StandardOpenOption.APPEND);
                    queue.clear();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                System.out.println("Can't write");
            }
        }
    }

}