package practica4;

import practica1.CircularQ.CircularQueue;
import util.Const;
import util.TCPSegment;
import util.TSocket_base;

public class TSocket extends TSocket_base {

  //sender variable:
  protected int MSS;

  //receiver variables:
  protected CircularQueue<TCPSegment> rcvQueue;
  protected int rcvSegConsumedBytes;

  protected TSocket(Protocol p, int localPort, int remotePort) {
    super(p.getNetwork());
    this.localPort  = localPort;
    this.remotePort = remotePort;
    p.addActiveTSocket(this);
    MSS = network.getMTU() - Const.IP_HEADER - Const.TCP_HEADER;
    rcvQueue = new CircularQueue<>(Const.RCV_QUEUE_SIZE);
    rcvSegConsumedBytes = 0;
  }

  @Override
  public void sendData(byte[] data, int offset, int length) {
   int restant = length;
    int offrest = offset;
    
    while(restant > 0){
        
        int longitud = Math.min(MSS, restant);
        TCPSegment seg = segmentize(data, offrest, longitud);
        network.send(seg);
        restant = restant - longitud;
        offrest = offrest + longitud;
        
    
    }
  }

  protected TCPSegment segmentize(byte[] data, int offset, int length) {
    TCPSegment temp = new TCPSegment();
    temp.setSourcePort(localPort);      
    temp.setDestinationPort(remotePort); 
    temp.setData(data, offset, length);
    temp.setPsh(true);
    return temp;
  }

  @Override
  public int receiveData(byte[] buf, int offset, int length) {
      lock.lock();
    try {
        while (rcvQueue.empty()) {
            appCV.awaitUninterruptibly();
        }
        
        int totalBytesRead = 0;
        
        while (totalBytesRead < length && !rcvQueue.empty()) {
            int currentOffset = offset + totalBytesRead;
            int remainingLength = length - totalBytesRead;
            
            int bytesReadInThisIteration = consumeSegment(buf, currentOffset, remainingLength);
            totalBytesRead += bytesReadInThisIteration;
        }
        
        return totalBytesRead;
    } finally {
        lock.unlock();
    }
  }

  protected int consumeSegment(byte[] buf, int offset, int length) {
    TCPSegment seg = rcvQueue.peekFirst();
    int a_agafar = Math.min(length, seg.getDataLength() - rcvSegConsumedBytes);
    System.arraycopy(seg.getData(), rcvSegConsumedBytes, buf, offset, a_agafar);
    rcvSegConsumedBytes += a_agafar;
    if (rcvSegConsumedBytes == seg.getDataLength()) {
      rcvQueue.get();
      rcvSegConsumedBytes = 0;
    }
    return a_agafar;
  }

  protected void sendAck() {
   TCPSegment ackSeg = new TCPSegment();
    ackSeg.setSourcePort(localPort);
    ackSeg.setDestinationPort(remotePort);
    ackSeg.setAck(true);
    network.send(ackSeg);
  }

  @Override
  public void processReceivedSegment(TCPSegment rseg) {

    lock.lock();
    try {     
      if (rseg.isAck()){
        //nothing to be done in this exercise.
      }else {
        
        if (!rcvQueue.full()) {
            rcvQueue.put(rseg);
            printRcvSeg(rseg); 
            appCV.signal();
            sendAck(); 
        }
      }
    }
      finally {
      lock.unlock();
    }
  }

}
