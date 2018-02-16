package PacketIntegrityTest;


//import CMPC3M06.AudioPlayer;
import audiotools.AudioPlayer;
import audiotools.AudioPlayer.AudioPreset;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import javax.sound.sampled.LineUnavailableException;
import uk.ac.uea.cmp.voip.*;
import voipclient.Frame;
/**
 *
 * @author xwd14rfu
 */
class ReceiverThread implements Runnable{
    
    static DatagramSocket receiving_socket;
    private static final int PORT = 55555;
    
    public void start(){
        Thread thread = new Thread(this);
	thread.start();
    }
    
    public void setUpSocket(){
         try{
		receiving_socket = new DatagramSocket3(PORT);
                receiving_socket.setSoTimeout(500);
	} catch (SocketException e){
                System.out.println("ERROR: TextReceiver: Could not open UDP socket to receive from.");
		e.printStackTrace();
                System.exit(0);
	}
    }

    public void run (){
          
        setUpSocket();
        AudioPlayer player = null;
        try {
            player = new AudioPlayer(AudioPreset.High);
        } catch (LineUnavailableException ex) {
           System.err.println("ERROR: Could not open AudioRecorder.");
           System.out.println("No playback device available");
           System.exit(0);
        }
        //***************************************************
        //Main loop.
        
        boolean running = true;
        PacketReorderer reorder = new PacketReorderer();
        while (running){
            try{
                    byte[] buffer = new byte[514];
                    DatagramPacket packet = new DatagramPacket(buffer, 0, 514);
                    receiving_socket.receive(packet);
                    Frame temp = new Frame(buffer);
                    reorder.push(temp);
                    Frame[] playback = reorder.pop();
                    for(Frame f:playback){
                        System.out.println("Playing packet: "+f.frameNO);
                       player.playBlock(f.framedata);
                    }
                //player.playBlock(packet.getData());
            }catch (SocketTimeoutException e) {
                System.out.println("Socket timeout");
            } catch (IOException e){
                System.err.println("ERROR:IO error occured - Reciever thread");
                e.printStackTrace();
            }
        }
        //Close the socket
        receiving_socket.close();
        //***************************************************
    }
}