package practica2.P0CZ;

public class TestSum {

    public static void main(String[] args) throws InterruptedException {
        CounterThread c1 = new CounterThread();
        CounterThread c2 = new CounterThread();
        c1.start();
        c2.start();
        c1.join();
        c2.join();
        System.out.println("El valor de la suma és "+ CounterThread.x);
    }
}
