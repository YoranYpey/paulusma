import java.net.Socket;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Handler implements Runnable{

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
                while (true) {
                    if (buf.readLine().contains("<measure".toUpperCase())) {
                        writeQueue();
                        //Thread thread = new Thread(new Parser(queue));
                        //thread.start();
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
                            queue.put(c);
                            System.out.println(c);
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

}