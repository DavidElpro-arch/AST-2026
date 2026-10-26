package practica4;

import util.Protocol_base;
import util.TCPSegment;
import util.SimNet;
import util.TSocket_base;

public class Protocol extends Protocol_base {

    public Protocol(SimNet network) {
      super(network);
    }

    protected void ipInput(TCPSegment seg) {
        int localPort = seg.getDestinationPort();
        int remotePort = seg.getSourcePort();
        
        
        TSocket_base temp = getMatchingTSocket(localPort,remotePort);
        if(temp != null){
        temp.processReceivedSegment(seg);
        }
    }

    protected TSocket_base getMatchingTSocket(int localPort, int remotePort) {
        lk.lock();
        try {
            for(TSocket_base s: activeSockets){
            
                if(s.getLocalPort() == localPort && s.getRemotePort() == remotePort){
                return s;
                }
                
            }
            return null;
        } finally {
            lk.unlock();
        }
    }
}
