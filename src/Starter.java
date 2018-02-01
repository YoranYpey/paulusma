import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Starter {
    public static void main(String[] args){
        System.out.println("started");
        BlockingQueue<String> queue = new ArrayBlockingQueue<String>(14);
        Thread t1 = new Thread(new Server(queue));
        Thread t2 = new Thread(new Parser(queue));
        t1.start();
        t2.start();
    }
}
