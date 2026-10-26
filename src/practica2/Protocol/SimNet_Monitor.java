package practica2.Protocol;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import practica1.CircularQ.CircularQueue;
import util.Const;
import util.TCPSegment;
import util.SimNet;

public class SimNet_Monitor implements SimNet {

  protected CircularQueue<TCPSegment> queue;
  protected ReentrantLock l = new ReentrantLock();
  protected Condition notFull = l.newCondition();
  protected Condition notEmpty = l.newCondition();
  

  public SimNet_Monitor() {
    queue  = new CircularQueue<>(Const.SIMNET_QUEUE_SIZE);
    //Completar
  }

  @Override
  public void send(TCPSegment seg) {
    l.lock();
    try {
      while (queue.full()) {
        notFull.await();
      }
      queue.put(seg);
      notEmpty.signal();
    } finally {
      l.unlock();
    }
  }

  @Override
  public TCPSegment receive() {
    l.lock();
    try {
      while (queue.empty()) {
        notEmpty.await();
      }
      TCPSegment seg = queue.get();
      notFull.signal();
      return seg;
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      return null;
    } finally {
      l.unlock();
    }
  }

  @Override
  public int getMTU() {
    return Const.MTU_ETHERNET;
  }

}
