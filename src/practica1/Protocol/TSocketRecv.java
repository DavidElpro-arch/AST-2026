package practica1.Protocol;

import util.TCPSegment;
import util.TSocket_base;
import util.SimNet;

public class TSocketRecv extends TSocket_base {

  public TSocketRecv(SimNet network) {
    super(network);
  }

  @Override
  public int receiveData(byte[] data, int offset, int length) {
    TCPSegment segmentRebut = network.receive(); 
    System.arraycopy(segmentRebut.getData(),0,data,offset,segmentRebut.getDataLength());
    
    if(length<segmentRebut.getDataLength()){
        throw new IllegalStateException("la mida demanada es mes petita que la rebuda");
    } else{
        return segmentRebut.getDataLength();
    }
  }
}
