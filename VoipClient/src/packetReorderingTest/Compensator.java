package packetReorderingTest;


/**
 *
 * @author scamp
 */
public interface Compensator {
    
    public abstract void push(Frame f);
    public abstract Frame [] pop();
    public abstract void process();
    public abstract int getLength();
}
