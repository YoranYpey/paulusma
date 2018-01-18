import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Service {

    public static void main(String[] args) {
        //Creating BlockingQueue of size 10
        BlockingQueue<String> queue = new ArrayBlockingQueue<>(14);
        Listener listener = new Listener(queue);
        //starting producer to produce messages in queue
        //starting consumer to consume messages from queue
        new Thread(listener).start();
        System.out.println("Producer and Consumer has been started");
    }
}