import org.apache.commons.io.input.ReversedLinesFileReader;
import org.apache.commons.math3.stat.regression.SimpleRegression;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
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
    private Path path;
    private Path file;


    public Handler(Socket c, BlockingQueue<String> q){
        this.queue = q;
        try {
            buf = new BufferedReader(new InputStreamReader(c.getInputStream()),512);
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
                        //if(stringContainsItemFromList(c, itemList)) {

                        if(!(c.matches(".*\\d.*"))){
                            //System.out.println("Missing Value");
                            c = calcValue(c);
                        }else if(c.contains("TEMP")){
                            c = calcTempValue(c);
                        }
                        queue.put(c);
                        //System.out.println(c);
                        //}
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
        double[][] data = new double[30][2];
        String type1 = type.split(">")[0];
        path = getPath();
        file = path.resolve("data.bin");

        if(getPath(path, file)) {
            File file2 = file.toFile();
            ReversedLinesFileReader revLineRead = new ReversedLinesFileReader(file2, Charset.forName("UTF-8"));

            //System.out.println("Reading file.......");
            int j = 1;
            for (int i = 0; i < 180; i++) {
                String line = revLineRead.readLine();
                //System.out.println(line);
                if (line != null){
                    if (line.contains(type1)) {
                        line = line.replaceAll("[^\\d.:-]", "");
                        data[j][0] = j;
                        data[j][1] = Double.parseDouble(line);
                        j++;
                    }
                }else {
                    String[] split = type.split("(?<=>)");
                    String missingVal = (split[0] + "0" + split[1]);
                    return missingVal;
                }
            }
            SimpleRegression regression = new SimpleRegression();
            regression.addData(data);

            //System.out.println(regression.predict(31));

            DecimalFormat df;
            if(type.contains("PRCP")) {
                df = new DecimalFormat("#0.00");
            }else{
                df = new DecimalFormat("#0.0");
            }
            Double missingValss = regression.predict(31);
            String missingVals = df.format(missingValss);

            String[] split = type.split("(?<=>)");
            String missingVal = (split[0] + missingVals + split[1]);
            //System.out.println(missingVal);

            return missingVal;
        }
        String[] split = type.split("(?<=>)");
        String missingVal = (split[0] + "0" + split[1]);
        return missingVal;
    }

    private String calcTempValue(String type) throws IOException{
        System.out.println("Entered calcTemp");
        Double measureTemp = Double.parseDouble(type.replaceAll("[^\\d.:-]", ""));
        String type1 = type.split(">")[0];
        path = getPath();
        file = path.resolve("data.bin");

        double[] data = new double[30];

        if(getPath(path, file)){
            System.out.println("Entered rev reader");
            File file2 = file.toFile();
            ReversedLinesFileReader revLineRead = new ReversedLinesFileReader(file2, Charset.forName("UTF-8"));

            int j = 1;
            for (int i = 0; i < 180; i++) {
                String line = revLineRead.readLine();
                //System.out.println(line);
                if (line != null){
                    if (line.contains(type1)) {
                        line = line.replaceAll("[^\\d.:-]", "");
                        data[j] = Double.parseDouble(line);
                        j++;
                    }
                }else {
                    return type;
                }
            }

            double sum = 0;
            for(double d : data){
                sum += d;
            }
            double avgTemp = sum/30;

            DecimalFormat df;
            df = new DecimalFormat("##.00");

            Double diff = (avgTemp - measureTemp)/((avgTemp + measureTemp)/2)*100;
            if(diff > 1.20 && diff < 1.20){

                System.out.println(avgTemp);
                String avgTempStr = df.format(String.valueOf(avgTemp));
                String[] split = type.split("(?<=>)");
                String missingval = (split[0] + avgTempStr + split[1]);
                return missingval;
            }
        }
        return type;
    }

    private boolean getPath(Path dir, Path file) {
        return (Files.exists(dir) && Files.exists(file));
    }

    private Path getPath(){
        String station = getStation();
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date date = new Date();
        String pathString = System.getProperty("user.dir") + "\\station_data\\" + station + "\\" + dateFormat.format(date);
        Path path = Paths.get(pathString);
        return path;
    }

    public static String AsciiToBinary(String asciiString){

        byte[] bytes = asciiString.getBytes();
        StringBuilder binary = new StringBuilder();
        for (byte b : bytes)
        {
            int val = b;
            for (int i = 0; i < 8; i++)
            {
                binary.append((val & 128) == 0 ? 0 : 1);
                val <<= 1;
            }
            // binary.append(' ');
        }
        return binary.toString();
    }
}