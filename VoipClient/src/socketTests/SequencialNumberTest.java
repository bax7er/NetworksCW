/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package socketTests;

import java.util.ArrayList;
import static socketTests.ReceiverThread.socketType.*;

/**
 *
 * @author xwd14rfu
 */
public class SequencialNumberTest {
    
    public static void main(String[] args){
        ArrayList<String> data= new ArrayList();
        for(int i = 0;i<1000;i++){
            data.add("     "+String.valueOf(i));
        }
        ReceiverThread receiver = new ReceiverThread(Socket4);
        SenderThread sender = new SenderThread(Socket4,"127.0.0.1",data);
        receiver.start();
        sender.start();
    }
}
