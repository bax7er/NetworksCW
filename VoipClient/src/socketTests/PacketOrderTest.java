/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package socketTests;

import java.util.ArrayList;
import socketTests.ReceiverThread.SocketType;
import static socketTests.ReceiverThread.SocketType.*;

/**
 *
 * @author xwd14rfu
 */
public class PacketOrderTest {
    
    public static void testSocket(SocketType socket){
        ArrayList<String> data= new ArrayList();
        for(int i = 0;i<1000;i++){
            data.add(String.valueOf(i));
        }
        ArrayList<String> receivedData = new ArrayList();
        ReceiverThread receiver = new ReceiverThread(socket,receivedData);
        SenderThread sender = new SenderThread(socket,"127.0.0.1",data);
        receiver.start();
        sender.start();
        try {
            receiver.thread.join();
             sender.thread.join();
        } catch (InterruptedException ex) {
            System.err.println("THREAD ERROR");
        }
        int packetsOutOfOrder = 0;
        for(int i = 0;i<receivedData.size()-1;i++){
            int first = Integer.parseInt(receivedData.get(i).trim());
            int next = Integer.parseInt(receivedData.get(i+1).trim());
            if(first>next){
                packetsOutOfOrder++;
            }
        }
        System.out.println(packetsOutOfOrder+" packets out of order");
    }
}
