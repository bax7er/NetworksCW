/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package packetReorderingTest;

import java.util.LinkedList;

/**
 *
 * @author Baxter
 */
public class PacketReorderer implements Compensator {

    Frame[] framearray ;
    int FRAMEARRAYSIZE = 256;
    int framearrayindex = 0;
    Frame lastNulled = null;
    @Override
    public void push(Frame f) {
        framearray[f.frameNO%FRAMEARRAYSIZE] = f;
    }

    @Override
    public Frame[] pop() {
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
