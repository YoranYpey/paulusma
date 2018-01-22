import org.apache.commons.io.input.ReversedLinesFileReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public class Handler implements Runnable{

    static int counter;

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
                buf.readLine();
                while (true) {
                    if (buf.readLine().contains("<measure".toUpperCase())) {
                        writeQueue();
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
                            if(!(c.matches(".*\\d.*"))){
                                System.out.println("Missing Value");
                                calcValue(c);
                            }
                            queue.put(c);
                            //System.out.println(c);
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

    private String getStation() {
        String msg = queue.peek();
        String station = "";
        if (msg.contains("STN")) {
            station = msg.substring(7, msg.length() - 6);
        }
        return station;
    }

    private String calcValue(String type) throws IOException{
        String type1 = type.split(">")[0];
        List<Double> values = new ArrayList<>();
        String station = getStation();
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date date = new Date();
        String path = System.getProperty("user.dir") + "\\station_data\\" + station + "\\" + dateFormat.format(date);

        //Set directory and file
        Path dir = Paths.get(path);
        Path file = dir.resolve("data.txt");
        File file2 = file.toFile();

        ReversedLinesFileReader revLineRead = new ReversedLinesFileReader(file2, Charset.forName("UTF-8"));

        System.out.println("Reading file.......");

        for(int i = 0; i < 180; i++) {
            String line = revLineRead.readLine();
            if (line.contains(type1)) {
                line = line.replaceAll("[^\\d.:-]", "");
                values.add(Double.parseDouble(line));
            }
        }



        double sum = 0;
        for(Double d : values){
            sum+= d;
        }

        DecimalFormat df = new DecimalFormat("#0.00");
        String missingVals = df.format(sum/30);
        String[] split = type.split("(?<=>)");
        String missingVal = (split[0] + missingVals + split[1]);
        System.out.println(missingVal);

        return missingVal;
    }

}