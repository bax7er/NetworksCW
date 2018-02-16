/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package PacketIntegrityTest;
import voipclient.Frame;

/**
 *
 * @author scamp
 */
public class PacketNumberer implements Compensator {

    Frame f;
    @Override
    public void push(Frame f) {
        this.f = f;
    }

    @Override
    public Frame[] pop() {
       return new Frame[]{f};
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
