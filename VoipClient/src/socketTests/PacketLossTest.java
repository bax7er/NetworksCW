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
 * @author scamp
 */
public class PacketLossTest {
    public static void testSocket(SocketType s){
        SocketType socket = s;
        boolean[] recievedIndicator = new boolean[1000];
        ArrayList<String> data= new ArrayList();
        for(int i = 0;i<1000;i++){
            data.add(String.valueOf(i));
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
       for(String str: recievedData){
           int packetNO = Integer.parseInt(str.trim());
           recievedIndicator[packetNO] = true;
       }
       System.out.println("Sent "+ sentNO+" Packets. Recieved "+receivedNO + " Packets");
       int lost = sentNO-receivedNO;
       float percentage = (float)lost/(float)sentNO;
       percentage*=100;
       System.out.println(lost+" Packets Lost. "+percentage+"% packet loss");
       int burstlength=0;
       int burstCount = 0;
       int longestBurst = 0;
       float avgBurst;
       for(boolean b : recievedIndicator){
           if(b==false){
               burstlength++;
           }
           else{
               if (burstlength!=0){
                   burstCount++;
                   if(burstlength>longestBurst){
                       longestBurst = burstlength;
                   }
                   burstlength = 0;
               }
           }
       }
        System.out.println("Longest Burst: " + longestBurst +" packets");
        System.out.println("Average Burst: " + (lost/(float)burstCount));
    }
}
