/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package socketTests;

import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author scamp
 */
public class PacketIntegrityTest {
    private static final int TESTSIZE = 10;
     public static void testSocket(ReceiverThread.socketType socket){
        ArrayList<String> data= new ArrayList();
        ArrayList<byte[]> bytes= new ArrayList();;
        byte[] shortPacket ={0};
        byte[] shortPacket2 ={0,0};
        byte[] midPacket ={0,0,0,0};
        byte[] midPacket2 ={0,0,0,0,0,0,0,0};
        byte[] longPacket ={0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
        bytes.add(shortPacket);
        bytes.add(shortPacket2);
        bytes.add(midPacket);
        bytes.add(midPacket2);
        bytes.add(longPacket);
        for(byte[]b:bytes){
            for(int i = 0;i<TESTSIZE;i++){
                data.add(new String(b));
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
            int corruptedPackets=0;
            System.out.println("Packet size(bytes) " + b.length +":");
            for(String str:receivedData){
                byte[] recFul = str.getBytes();
                byte[] rec = Arrays.copyOfRange(recFul, 0, b.length);
                if(!Arrays.equals(rec,b)){
                    corruptedPackets++;
                    System.out.println("Expected: "+Arrays.toString(b)+ " Got: "+Arrays.toString(rec));
                }
            }
            
            System.out.println(corruptedPackets+" bad packets. " +(TESTSIZE-corruptedPackets)+" healthy packets.");
        }
       
    }
}
