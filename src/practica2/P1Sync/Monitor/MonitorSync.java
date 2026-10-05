package practica2.P1Sync.Monitor;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class MonitorSync {
    private final int N;
    ReentrantLock l = new ReentrantLock();
    Condition c = l.newCondition();
    protected int torn = 0;

    public MonitorSync(int N) {
        this.N = N;
    }

    public void waitForTurn(int id) {
        l.lock();
        try{
            while(torn != id){
                c.awaitUninterruptibly();
            }
        } finally{
            l.unlock();
        }
       
    }

    public void transferTurn() {
        try{
            l.lock();
            torn = (torn + 1) % N;      
            c.signalAll();
        }finally{
            l.unlock();
        }
    }
}
