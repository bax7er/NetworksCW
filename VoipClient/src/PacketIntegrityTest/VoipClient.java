/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package PacketIntegrityTest;



/**
 *
 * @author xwd14rfu
 */
public class VoipClient {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        ReceiverThread receiver = new ReceiverThread();
        SenderThread sender = new SenderThread();
        
        receiver.start();
        sender.start();
    }
    
}
