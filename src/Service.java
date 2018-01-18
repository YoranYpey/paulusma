import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Service {

    public static void main(String[] args) {
        //Creating BlockingQueue of size 10
        BlockingQueue<String> queue = new ArrayBlockingQueue<>(14);
        Listener listener = new Listener();
        Consumer consumer = new Consumer(queue);
        Producer producer = new Producer(queue, listener.getClient(), listener);
        //starting producer to produce messages in queue
        new Thread(producer).start();
        //starting consumer to consume messages from queue
        new Thread(consumer).start();
        System.out.println("Producer and Consumer has been started");
    }
}