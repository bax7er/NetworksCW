package GUI;

import GUI.ReceiverThread.SocketType;
import audiotools.AudioPlayer.AudioPreset;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author James Baxter
 */
public class VOIPservice {
    SocketType socket;
    AudioPreset preset;
    private ReceiverThread receiver;
    private SenderThread sender;
    public VOIPSettings settings;
    public VOIPservice(String hostname,int port,int socket,int preset){
        this.settings = new VOIPSettings();
        settings.hostname = hostname;
        settings.port = port;
        settings.socket = socket;
        settings.bitrate = preset;
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
        this.socket = SocketType.getSocket(socket);
        
    }
    public VOIPservice(VOIPSettings settings){
        this.settings = settings;
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
        receiver = new ReceiverThread(settings);
        sender = new SenderThread(settings);
        
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
