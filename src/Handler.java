import org.apache.commons.io.input.ReversedLinesFileReader;
import org.apache.commons.math3.stat.regression.SimpleRegression;

import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.zip.GZIPInputStream;

public class Handler implements Runnable{

    private BlockingQueue<String> queue;
    private BufferedReader buf;
    private Path path;
    private Path file;


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
                    buf.readLine();
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
                        String c = buf.readLine().trim();
                        c = c.substring(0, c.indexOf("/")-1);
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

    private String getStation() {
        String msg = queue.peek();
        String station = "";
        if (msg.contains("STN")) {
            station = msg.substring(5, msg.length());
        }
        return station;
    }

    private String calcValue(String type) throws IOException{
        double[][] data = new double[30][2];
        String type1 = type.split(">")[0];
        path = getPath();
        file = path.resolve("data.txt");

        if(checkPath(path, file)) {
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
                    return (split[0] + "0" + split[1]);
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
            String missingVal = (split[0] + missingVals);
            //System.out.println(missingVal);

            return missingVal;
        }
        String[] split = type.split("(?<=>)");
        String missingVal = (split[0] + "0");
        return missingVal;
    }

    private String calcTempValue(String type) throws IOException{
        Double measureTemp = Double.parseDouble(type.replaceAll("[^\\d.:-]", ""));
        String type1 = type.split(">")[0];
        path = getPath();
        file = path.resolve("data.txt");

        double[] data = new double[30];

        if(checkPath(path, file)){
            File file2 = file.toFile();
            ReversedLinesFileReader revLineRead = new ReversedLinesFileReader(file2, Charset.forName("UTF-8"));

            int j = 0;
            for (int i = 0; i < 420; i++) {
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

            if(j==30) {
                double sum = 0;
                for (double d : data) {
                    sum += d;
                }
                DecimalFormat df;
                df = new DecimalFormat("##.00");
                double avgTemp = sum / 30;
                String avgTempStr = Long.toString((Math.round(avgTemp * 100) / 100));

                if (measureTemp > avgTemp * 1.20 || measureTemp < avgTemp * .80) {
                    System.out.println("Changed temp value from: " + measureTemp + " to: " + avgTemp);
                    String[] split = type.split("(?<=>)(.*)");
                    String missingval = (split[0] + avgTempStr);
                    System.out.println(missingval);
                    return missingval;
                }
            }else{
                System.out.println("Array not filled full");
            }
        }
        return type;
    }

    private boolean checkPath(Path dir, Path file) {
        return (Files.exists(dir) && Files.exists(file));
    }

    private Path getPath(){
        String station = getStation();
        int year = Calendar.getInstance().get(Calendar.YEAR);
        int month = Calendar.getInstance().get(Calendar.MONTH);
        int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        String pathString = System.getProperty("user.dir") + "/station_data/" + station + "/" + year + "/" + month + "/" + day;
        Path path = Paths.get(pathString);
        return path;
    }
}