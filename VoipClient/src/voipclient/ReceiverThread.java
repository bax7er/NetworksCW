package voipclient;

import CMPC3M06.AudioPlayer;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import javax.sound.sampled.LineUnavailableException;
import uk.ac.uea.cmp.voip.*;

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
		receiving_socket = new DatagramSocket(PORT);
                receiving_socket.setSoTimeout(500);
	} catch (SocketException e){
                System.out.println("ERROR: TextReceiver: Could not open UDP socket to receive from.");
		e.printStackTrace();
                System.exit(0);
	}
    }
    public void setUpPlayer(AudioPlayer player){
         try {
            player = new AudioPlayer();
        } catch (LineUnavailableException ex) {
           System.err.println("ERROR: Could not open AudioRecorder.");
           System.out.println("No playback device available");
           System.exit(0);
        }
    }
    public void run (){
          
        setUpSocket();
        AudioPlayer player = null;
        setUpPlayer(player);
        //***************************************************
        //Main loop.
        
        boolean running = true;
        
        while (running){
            try{
                byte[] buffer = new byte[512];
                DatagramPacket packet = new DatagramPacket(buffer, 0, 512);

                receiving_socket.receive(packet);

                player.playBlock(packet.getData());
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
