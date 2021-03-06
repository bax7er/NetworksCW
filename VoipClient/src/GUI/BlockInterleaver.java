package GUI;

import static java.lang.Math.sqrt;
import java.util.ArrayList;

/**
 * An initial implementation of a block interleaver
 * @author Shaun Leeks
 */
public class BlockInterleaver implements Compensator {
    private int[] depth;
    //private static final int[] DEPTH2 = {0,1,2,3};
    private static final int[] DEPTH2 = {1,3,0,2};
    private static final int[] DEPTH3 = {2,5,8,1,4,7,0,3,6};
    private ArrayList<Frame> framedata;
    private int counter = 0;
    
    public void push(Frame f){
        framedata.add(f);
    };
    
    public void process(){
        counter = 0;
        depth = new int[framedata.size()];
        switch (framedata.size()) {
            case 9:
                depth = DEPTH3;
                break;
            case 4:
                depth = DEPTH2;
                break;
            default:
                int d = (int) sqrt(framedata.size());
                if(d*d != framedata.size()){
                    //should create custom error
                    throw new IllegalArgumentException("The number of frames must be a perfect square.");
                } else{
                    int counter = 0;
                    for(int i = 0; i < d;i++){
                        for(int j = 0; j < d;j++){
                            depth[counter] = j*d+(d-1-i);
                            counter++;
                        }
                    }
                }   
                break;
        }
    };
    
    public Frame[] pop()
    { 
        Frame toReturn = framedata.get(depth[counter++]);
        if(counter == depth.length){
            framedata.clear();
        }
        return new Frame[]{toReturn};
    };
    public int getLength(){return 0;};
    public BlockInterleaver(){
        framedata = new ArrayList<>(4);
    }
}
