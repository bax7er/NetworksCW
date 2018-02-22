package GUI;

import java.util.LinkedList;
/**
 *
 * @author Shaun Leeks
 */
public class PacketFixer {
    
    FrameCheck frame;
    boolean repeat;
    FrameCheck blank = new FrameCheck();
    FrameCheck lastFrame = blank;
    int lastQuarter = 0;
    
    public PacketFixer(){
        frame = new FrameCheck();
        lastFrame = new FrameCheck();
    }
    
    
    public void push(FrameCheck f) {
            frame =  f;
    }

    
    public Frame[] pop() {
        boolean integrity[] = frame.verifyIntegrity();
        if(!integrity[0]){
            System.out.println("FRAME NUMBER CORRUPTED");
            if (repeat) {
                return new Frame[] {lastFrame.getHalvedAmp()};
            } else {
                return new Frame[] {blank};
            }
            
        }else{ 
            for(int i = 1; i <=4;i++)
            {
                if(!integrity[i]){
                    System.out.println("Error in chunk " + i);
                    if (repeat) {
                        frame.halveQuarter(i-1, lastFrame, lastQuarter);
                    } else {
                        frame.zeroQuarter(i-1);
                    }
                }
                
                lastFrame = frame;
                lastQuarter = (lastQuarter + 1) % 4;
            }
        }
        FrameCheck send[] = {frame};
        return send;
    }

   
}
