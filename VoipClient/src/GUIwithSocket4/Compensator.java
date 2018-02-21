package GUIwithSocket4;
import GUIwithSocket4.FrameCheck;


public interface Compensator {
    
    public abstract void push(FrameCheck f);
    public abstract FrameCheck [] pop();
    public abstract void process();
    public abstract int getLength();
}
