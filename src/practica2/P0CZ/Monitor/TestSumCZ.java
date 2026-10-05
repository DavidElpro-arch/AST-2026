package practica2.P0CZ.Monitor;

public class TestSumCZ {

    public static void main(String[] args) throws InterruptedException {
        MonitorCZ m = new MonitorCZ();
        CounterThreadCZ c1 = new CounterThreadCZ(m);
        CounterThreadCZ c2 = new CounterThreadCZ(m);
        
        
        c1.start();
        c2.start();
        
        c1.join();
        c2.join();
        
        System.out.println(m.getX());
    }
}
