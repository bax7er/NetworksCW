package GUI;

import java.util.Arrays;

/**
 * A class to run along side FrameCheck, allowing the detection and recover of 
 * errors
 * @author Shaun Leeks & James Baxter
 */
public class PacketFixer {

    FrameCheck frame;
    FrameCheck redFrame;
    boolean repeat;
    FrameCheck blank = new FrameCheck();
    FrameCheck lastFrame = blank;
    int lastQuarter = 0;
    boolean useRedundant;

    public PacketFixer() {
        frame = new FrameCheck();
        lastFrame = new FrameCheck();
    }

    public void push(FrameCheck f) {
        frame = f;
    }

    public void pushRedundant(byte[] buffer) {
        frame = new FrameCheck(Arrays.copyOfRange(buffer, 0, buffer.length / 2));
        redFrame = new FrameCheck(Arrays.copyOfRange(buffer, buffer.length / 2, buffer.length));
    }

    public Frame[] pop() {
        boolean integrity[] = frame.verifyIntegrity();
        if (!integrity[0]) {
            System.out.println("FRAME NUMBER CORRUPTED");
            if (repeat) {
                return new Frame[]{lastFrame.getHalvedAmp()};
            } else {
                return new Frame[]{blank};
            }

        } else {
            for (int i = 1; i <= 4; i++) {
                if (!integrity[i]) {
                    System.out.println("Error in chunk " + i);
                    if (repeat) {
                        frame.halveQuarter(i - 1, lastFrame, lastQuarter);
                    } else {
                        frame.zeroQuarter(i - 1);
                    }
                }

                lastFrame = frame;
                lastQuarter = (lastQuarter + 1) % 4;
            }
        }
        FrameCheck send[] = {frame};
        return send;
    }

    public Frame popRedundant() {
        boolean integrity[] = frame.verifyIntegrity();
        boolean redIntegrity[] = redFrame.verifyIntegrity();
        //System.out.println("Frame 1"+ Arrays.toString(integrity) + " Redundant Frame "+ Arrays.toString(redIntegrity));
        for(boolean b: redIntegrity){
            if(!b)
            System.out.println("OOPs");
        }
        if (!integrity[0]) {
            if (!redIntegrity[0]) {
                System.out.println("REDUNDANT FRAME NUMBER CORRUPTED");
                    return blank;
            } else {
                System.out.println("FIXED FRAME NUMBER");
                frame.frameNO = redFrame.frameNO;
            }

        } 
            for (int i = 1; i <= 4; i++) {
                if (!integrity[i]) {
                    if (!redIntegrity[i]) {
                         System.out.println("Could not Recover Error in chunk " + i);
                            frame.zeroQuarter(i - 1);
                    } else{
                        System.out.println("ERROR RECOVERED");
                        frame.setQuater(i-1, redFrame.getQuater(i-1));
                    }
                 

                }

                lastFrame = frame;
                lastQuarter = (lastQuarter + 1) % 4;
            }
        
        FrameCheck send = frame;
        return send;
    }

}
