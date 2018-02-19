
package GUI;

import static java.lang.Math.sqrt;
import java.util.ArrayList;
import voipclient.Frame;

/**
 *
 * @author scamp
 */
public class AlternativeInterleaver implements Compensator {
    private int[] depth;
    private int depthArrayPTR = 0;
    private Frame[] frameBlock;
    private int blockSize;
    
    private static final int[] DEPTH2 = {1,3,0,2};
    private static final int[] DEPTH3 = {2,5,8,1,4,7,0,3,6};
    
    public AlternativeInterleaver(int i_depth){
        blockSize = i_depth*i_depth;
        frameBlock = new Frame[blockSize];
         switch (i_depth) {
            case 3:
                depth = DEPTH3;
                break;
            case 2:
                depth = DEPTH2;
                break;
            default:
                    depth = new int[blockSize];
                    int counter = 0;
                    for(int i = 0; i < i_depth;i++){
                        for(int j = 0; j < i_depth;j++){
                            depth[counter] = j*i_depth+(i_depth-1-i);
                            counter++;
                        }
                    }
 
                break;
        }
    }
    
    @Override
    public void push(Frame f){
       frameBlock[f.frameNO%blockSize] = f;
    };
    
    
    @Override
    public Frame[] pop()
    { 
        int index = depth[depthArrayPTR];
        Frame f = frameBlock[index];
        if(f!=null){
            frameBlock[index] = null;
            depthArrayPTR++;
            depthArrayPTR %= blockSize;
            return new Frame[]{f};
        }
        else{
            return null;
        }
    };
    public int getLength(){return 0;};

    @Override
    public void process() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
