package practica5;

import practica1.CircularQ.CircularQueue;
import practica4.Protocol;
import util.Const;
import util.TSocket_base;
import util.TCPSegment;

public class TSocket extends TSocket_base {

    // Sender variables:
    protected int MSS;
    protected int snd_sndNxt;
    protected int snd_rcvWnd;
    protected int snd_rcvNxt;
    protected boolean zero_wnd_probe_ON;

    // Receiver variables:
    protected CircularQueue<TCPSegment> rcv_Queue;
    protected int rcv_SegConsumedBytes;
    protected int rcv_rcvNxt;

    protected TSocket(Protocol p, int localPort, int remotePort) {
        super(p.getNetwork());
        this.localPort = localPort;
        this.remotePort = remotePort;
        p.addActiveTSocket(this);

        // init sender variables
        MSS = p.getNetwork().getMTU() - Const.IP_HEADER - Const.TCP_HEADER;
        snd_rcvWnd = Const.RCV_QUEUE_SIZE;

        // init receiver variables
        rcv_Queue = new CircularQueue<>(Const.RCV_QUEUE_SIZE);
        //rcv_Queue = new CircularQueue<>(2);

    }

    // -------------  SENDER PART  ---------------
    @Override
    public void sendData(byte[] data, int offset, int length) {
        lock.lock();
        try {
             int restant = length;
    int offrest = offset;
    
    while(restant > 0){
        
        
        int longitud = Math.min(MSS, restant);
        TCPSegment seg = segmentize(data, offrest, longitud);
        
        printSndSeg(seg);
        network.send(seg);
        
        while(snd_rcvNxt <= snd_sndNxt){
        appCV.awaitUninterruptibly();
        
        }
        
        snd_sndNxt ++;
        
        
        restant = restant - longitud;
        offrest = offrest + longitud;
        
    
    }
        } finally {
            lock.unlock();
        }
    }

    protected TCPSegment segmentize(byte[] data, int offset, int length) {
    TCPSegment temp = new TCPSegment();
    temp.setSourcePort(localPort);      
    temp.setDestinationPort(remotePort); 
    temp.setData(data, offset, length);
    temp.setPsh(true);
    temp.setSeqNum(snd_sndNxt);
    return temp;
    }

    @Override
    protected void timeout(TCPSegment seg) {
        lock.lock();
        try {
            throw new RuntimeException("//Completar...");
        } finally {
            lock.unlock();
        }
    }

    // -------------  RECEIVER PART  ---------------
    @Override
    public int receiveData(byte[] buf, int offset, int maxlen) {
        lock.lock();
    try {
        while (rcv_Queue.empty()) {
            appCV.awaitUninterruptibly();
        }
        
        int totalBytesRead = 0;
        
        while (totalBytesRead < maxlen && !rcv_Queue.empty()) {
            int currentOffset = offset + totalBytesRead;
            int remainingLength = maxlen - totalBytesRead;
            
            int bytesReadInThisIteration = consumeSegment(buf, currentOffset, remainingLength);
            totalBytesRead += bytesReadInThisIteration;
        }
        
        return totalBytesRead;
    } finally {
        lock.unlock();
    }
    }

    protected int consumeSegment(byte[] buf, int offset, int length) {
        TCPSegment seg = rcv_Queue.peekFirst();
        int a_agafar = Math.min(length, seg.getDataLength() - rcv_SegConsumedBytes);
        System.arraycopy(seg.getData(), rcv_SegConsumedBytes, buf, offset, a_agafar);
        rcv_SegConsumedBytes += a_agafar;
        if (rcv_SegConsumedBytes == seg.getDataLength()) {
            rcv_Queue.get();
            rcv_SegConsumedBytes = 0;
        }
        return a_agafar;
    }

    protected void sendAck() {
        TCPSegment ackSeg = new TCPSegment();
    ackSeg.setSourcePort(localPort);
    ackSeg.setDestinationPort(remotePort);
    ackSeg.setAck(true);
    rcv_rcvNxt++;
    ackSeg.setAckNum(rcv_rcvNxt);
    network.send(ackSeg);
    }

    // -------------  SEGMENT ARRIVAL  -------------
    @Override
    public void processReceivedSegment(TCPSegment rseg) {
                  printRcvSeg(rseg); 
 
    lock.lock();
    try {     
      if (rseg.isAck()){
        snd_rcvNxt = rseg.getAckNum();
        appCV.signal();
      }else {
        
        if (!rcv_Queue.full()) {
            rcv_Queue.put(rseg);
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
