package GUI;

import java.util.LinkedList;
import voipclient.Frame;
/**
 *
 * @author Baxter
 */
public class PacketReorderer implements Compensator {

    Frame[] framearray ;
    int FRAMEARRAYSIZE = 256;
    int framearrayindex = 999;
    Frame lastNulled = null;
    int initialDelay = 10;
    int playedFrames =0;
    
    public PacketReorderer(){
        framearray = new Frame[FRAMEARRAYSIZE];
    }
    @Override
    public void push(Frame f) {
        framearray[f.frameNO%FRAMEARRAYSIZE] = f;
        //Sets starting point for the index
        if(playedFrames<initialDelay &&f.frameNO%FRAMEARRAYSIZE<framearrayindex){ 
            framearrayindex = f.frameNO%FRAMEARRAYSIZE-1;
        }
    }

    @Override
    public Frame[] pop() {
        if(playedFrames<initialDelay){
            // allows slight buffering
            playedFrames++;
            return new Frame[]{new Frame((short)0,new byte[512])};
        }
        playedFrames++;
        framearrayindex++;
        framearrayindex = framearrayindex%FRAMEARRAYSIZE;
        if(framearray[framearrayindex] != null){
        lastNulled = framearray[framearrayindex];
        framearray[framearrayindex] = null;
        return new Frame[]{lastNulled};
        }
        else{
            //Compensate the repeat;
            LinkedList<Frame> ll = new LinkedList<>();
            lastNulled = lastNulled.getHalvedAmp();
            ll.add(lastNulled);
            do{
                framearrayindex++;
                framearrayindex = framearrayindex%FRAMEARRAYSIZE;
                if(framearray[framearrayindex] != null){
                    lastNulled = framearray[framearrayindex];
                    framearray[framearrayindex] = null;
                    ll.add(lastNulled);
                    return ll.toArray(new Frame[ll.size()]);
                }
                else{
                    //Compensate the repeat;
                    //ll.add(lastNulled);
                      lastNulled = lastNulled.getHalvedAmp();
                      ll.add(lastNulled);
                }
            }
            while(true);
            
        }
        
    }

    @Override
    public void process() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getLength() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
