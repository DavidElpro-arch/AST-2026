package practica2.P0CZ.Monitor;

import java.util.concurrent.locks.ReentrantLock;

public class MonitorCZ {

    private int x = 0;
    private final int I = 10000;
    private ReentrantLock l = new ReentrantLock();
    
    public void inc() {
       try{
        l.lock();
        x = x + 1;
       } finally{
        l.unlock();
       }
    }

    public int getX() {
        l.lock();
        int temp = x;
        l.unlock();
        return temp;
    }

}
