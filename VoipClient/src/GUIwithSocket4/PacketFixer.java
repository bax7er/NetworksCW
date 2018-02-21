package GUIwithSocket4;

import java.util.LinkedList;
import voipclient.Frame;
/**
 *
 * @author Baxter
 */
public class PacketFixer implements Compensator {
    
    FrameCheck frame;
    boolean repeat = true;
    FrameCheck blank = new FrameCheck();
    FrameCheck lastFrame = blank;
    int lastQuarter = 0;
    
    public PacketFixer(){
        frame = new FrameCheck();
        lastFrame = new FrameCheck();
    }
    
    @Override
    public void push(FrameCheck f) {
        frame = f;
    }

    @Override
    public FrameCheck[] pop() {
        boolean integrity[] = frame.verifyIntegrity();
        if(!integrity[0]){
            if (repeat) {
                frame = lastFrame.getHalvedAmp();
            } else {
                frame = blank;
            }
            
        }else{ 
            for(int i = 1; i <4;i++)
            {
                if(!integrity[i]){
                    if (repeat) {
                        frame.halveQuarter(i, lastFrame, lastQuarter);
                    } else {
                        frame.zeroQuarter(i);
                    }
                }
                
                lastFrame = frame;
                lastQuarter = (lastQuarter + 1) % 4;
            }
        }
        FrameCheck send[] = {frame};
        return send;
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
