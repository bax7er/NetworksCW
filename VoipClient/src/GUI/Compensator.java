package GUI;
import voipclient.Frame;


public interface Compensator {
    
    public abstract void push(Frame f);
    public abstract Frame [] pop();
}
