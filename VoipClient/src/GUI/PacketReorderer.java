package GUI;

import java.util.LinkedList;

/**
 * A class to reorder and output frame correctly
 * @author James Baxter
 */
public class PacketReorderer implements Compensator {

    Frame[] framearray;
    int FRAMEARRAYSIZE = 256;
    int framearrayindex = 999;
    Frame lastNulled = null;
    int initialDelay;
    int playedFrames = 0;
    boolean repeat = true;
    boolean extraData;
    boolean reused= false;
    Frame blank = new Frame((short) 0, new byte[512]);

    public PacketReorderer() {
        framearray = new Frame[FRAMEARRAYSIZE];
    }

    @Override
    public void push(Frame f) {
        framearray[f.frameNO % FRAMEARRAYSIZE] = f;
        //Sets starting point for the index
        if (playedFrames < initialDelay && f.frameNO % FRAMEARRAYSIZE < framearrayindex) {
            framearrayindex = f.frameNO % FRAMEARRAYSIZE - 1;
        }
    }

    @Override
    public Frame[] pop() {
        if (playedFrames < initialDelay) {
            // allows slight buffering
            playedFrames++;
            return new Frame[]{new Frame((short) 0, new byte[512])};
        }
        playedFrames++;
        framearrayindex++;
        framearrayindex = framearrayindex % FRAMEARRAYSIZE;
        if (framearray[framearrayindex] != null) {
            lastNulled = framearray[framearrayindex];
            framearray[framearrayindex] = null;
            reused = false;
            return new Frame[]{lastNulled};
        }if(extraData && !reused){
            System.out.println("LOWQUALITYFRAME");
            reused = true;
            ADVFrame adv = (ADVFrame)lastNulled;
            lastNulled = new Frame((short)999,adv.decompressNext());
            return new Frame[]{lastNulled};
        }else {
            //Compensate the repeat;
            System.out.println("REPEATING");
            LinkedList<Frame> ll = new LinkedList<>();
            lastNulled = lastNulled.getHalvedAmp();
            if (repeat) {
                ll.add(lastNulled);
            } else {
                ll.add(blank);
            }
            do {
                framearrayindex++;
                framearrayindex = framearrayindex % FRAMEARRAYSIZE;
                if (framearray[framearrayindex] != null) {
                    lastNulled = framearray[framearrayindex];
                    framearray[framearrayindex] = null;
                    ll.add(lastNulled);
                    return ll.toArray(new Frame[ll.size()]);
                } else {
                    //Compensate the repeat;
                    //ll.add(lastNulled);
                    lastNulled = lastNulled.getHalvedAmp();
                    if (repeat) {
                        ll.add(lastNulled);
                    } else {
                        ll.add(blank);
                    }
                }
            } while (true);

        }

    }


}
