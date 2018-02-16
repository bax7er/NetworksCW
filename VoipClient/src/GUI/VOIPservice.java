/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import GUI.ReceiverThread.SocketType;
import audiotools.AudioPlayer.AudioPreset;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author scamp
 */
public class VOIPservice {
    String host;
    int port;
    SocketType socket;
    AudioPreset preset;
    private ReceiverThread receiver;
    private SenderThread sender;
    public VOIPservice(String hostname,int port,int socket,int preset){
        switch(preset){
            case 0:
                this.preset = AudioPreset.Standard;
                break;
            case 1:
                this.preset = AudioPreset.Low;
                break;
             case 2:
                this.preset = AudioPreset.High;
                break;
             default:
                 this.preset = AudioPreset.Standard;
        }
        this.port = port;
        this.socket = SocketType.getSocket(socket);
        this.host = hostname;
        
    }
    public int[] status(){
        int i = 0;
        if(receiver.timeout){
            i = 1;
        }
        int j = 0;
        if(sender.hostFailed){
            j = 1;
        }
        return new int[]{receiver.recCount,sender.sentCount,i,j};
    }
    public void startVOIP(){
        receiver = new ReceiverThread(socket,port,preset);
        sender = new SenderThread(socket,host,port,preset);
        
        receiver.start();
        sender.start();
    }
    public void stopVOIP(){
        sender.stop();
        receiver.stop();
        try {
            receiver.thread.join();
            sender.thread.join();
        } catch (InterruptedException ex) {
            Logger.getLogger(VOIPservice.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
