/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package socketTests;
import java.util.ArrayList;
import socketTests.ReceiverThread.socketType;
import static socketTests.ReceiverThread.socketType.*;
/**
 *
 * @author scamp
 */
public class PacketLossTest {
    public static void testSocket(socketType s){
        socketType socket = s;
        
        ArrayList<String> data= new ArrayList();
        for(int i = 0;i<1000;i++){
            data.add("TestPacket");
        }
        ArrayList<String> recievedData= new ArrayList();
        ReceiverThread receiver = new ReceiverThread(socket,recievedData);
        SenderThread sender = new SenderThread(socket,"127.0.0.1",data);
        receiver.start();
        sender.start();
        
        try {
            receiver.thread.join();
             sender.thread.join();
        } catch (InterruptedException ex) {
            System.err.println("THREAD ERROR");
        }
       int sentNO = data.size();
       int receivedNO = recievedData.size();
       System.out.println("Sent "+ sentNO+" Packets. Recieved "+receivedNO + " Packets");
       int lost = sentNO-receivedNO;
       float percentage = (float)lost/(float)sentNO;
       percentage*=100;
        System.out.println(lost+" Packets Lost. "+percentage+"% packet loss");
    }
}