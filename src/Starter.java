import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Starter {
    public static void main(String[] args){
        BlockingQueue<String> queue = new ArrayBlockingQueue<String>(6);
        Thread t1 = new Thread(new Server(queue));
        Thread t2 = new Thread(new Parser(queue));
        t1.start();
        t2.start();
    }
}
