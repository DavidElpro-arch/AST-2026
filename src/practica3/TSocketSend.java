package practica3;

import util.Const;
import util.TCPSegment;
import util.TSocket_base;
import util.SimNet;

public class TSocketSend extends TSocket_base {

  protected int MSS;       // Maximum Segment Size

  public TSocketSend(SimNet network) {
    super(network);
    MSS = network.getMTU() - Const.IP_HEADER - Const.TCP_HEADER;
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
    temp.setData(data,offset,length);
    temp.setPsh(true);
    return temp;
  }

}
