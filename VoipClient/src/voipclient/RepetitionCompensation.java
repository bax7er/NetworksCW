/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package voipclient;

/**
 *
 * @author shaun
 */
public class RepetitionCompensation {
    Frame lastFrame;
    RepetitionCompensation(){
        lastFrame = new Frame();
    }
    
    RepetitionCompensation(Frame lastFrame){
        this.lastFrame = lastFrame;
    }
    
    void fix(Frame[] frameBuffer){
        if(frameBuffer[0] == null)
            frameBuffer[0] = lastFrame.getHalvedAmp();
        
        for (int i = 0; i < frameBuffer.length;i++){
            if(frameBuffer[i] == null)
                frameBuffer[i] = frameBuffer[i-1].getHalvedAmp();
            
        }
        lastFrame = frameBuffer[frameBuffer.length-1];
    }
}
