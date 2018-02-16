package socketTests;

import java.util.ArrayList;

public class LatencyTest {
    private static int pingCount = 5;
    public static void testSocket(ReceiverThread.SocketType s){
        ReceiverThread.SocketType socket = s;
        
        for(int i = 0;i<pingCount;i++){
        long nanoTime = System.nanoTime();
        ArrayList<String> data= new ArrayList();
        data.add(String.valueOf(nanoTime));
        ArrayList<String> recievedData= new ArrayList();
        LongWrapper timestamp = new LongWrapper();
        timestamp.data = 0L;
        ReceiverThread receiver = new ReceiverThread(socket,recievedData,timestamp);
        SenderThread sender = new SenderThread(socket,"127.0.0.1",data);
        receiver.start();
        sender.start();
        
        try {
            receiver.thread.join();
             sender.thread.join();
        } catch (InterruptedException ex) {
            System.err.println("THREAD ERROR");
        }
       
       if(timestamp.data !=0L && timestamp.data!=null){
        long taken = timestamp.data-nanoTime;
       double millis = taken/ 1000000D;
       System.out.println("Round Trip time: "+millis+" ms.");
        }
       else{
           System.out.println("Ping Timed Out, trying again");
       }
       }
    }
}
