import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Starter {
    public static void main(String[] args){
        BlockingQueue<String> queue = new ArrayBlockingQueue<String>(14);
        Thread t = new Thread(new Server(queue));
        t.start();
    }
}
