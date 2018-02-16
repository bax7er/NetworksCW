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
    private static final int TESTSIZE = 1000;
     public static void testSocket(ReceiverThread.SocketType socket){
        ArrayList<String> data= new ArrayList();
        ArrayList<byte[]> bytes= new ArrayList();;
        byte[] shortPacket ={0};//1
        byte[] shortPacket2 ={0,0};//2
        byte[] midPacket ={0,0,0,0};//4
        byte[] midPacket2 ={0,0,0,0,0,0,0,0};//8
        byte[] longPacket ={0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};//32
        byte[] longPacket3 = new byte[514];
        bytes.add(shortPacket);
        bytes.add(shortPacket2);
        bytes.add(midPacket);
        bytes.add(midPacket2);
        bytes.add(longPacket);
        bytes.add(longPacket3);
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
            int lostBytes = 0;
            for(String str:receivedData){
                byte[] recFul = str.getBytes();
                byte[] rec = Arrays.copyOfRange(recFul, 0, b.length);
                if(!Arrays.equals(rec,b)){
                    corruptedPackets++;
                    int badBytes = 0;
                    for(byte recievedbyte :rec){
                        if(recievedbyte != (byte)0)
                            badBytes++;
                    }
                    lostBytes+=badBytes;
                    System.out.println("Bad Bytes: "+badBytes+"  Expected: "+Arrays.toString(b)+ " Got: "+Arrays.toString(rec));
                }
            }
            
            System.out.println(corruptedPackets+" " +100*(float)corruptedPackets/TESTSIZE+"%bad packets. " +(TESTSIZE-corruptedPackets)+" healthy packets.");
            if(corruptedPackets!=0){
            System.out.println(lostBytes+" corrupted bytes "+ lostBytes/corruptedPackets + " Average byte loss");
            }
        }
       
    }
}
