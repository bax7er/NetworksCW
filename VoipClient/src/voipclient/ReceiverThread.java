package voipclient;

import CMPC3M06.AudioPlayer;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Arrays;
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
		receiving_socket = new DatagramSocket2(PORT);
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
        try {
            player = new AudioPlayer();
        } catch (LineUnavailableException ex) {
           System.err.println("ERROR: Could not open AudioRecorder.");
           System.out.println("No playback device available");
           System.exit(0);
        }
        //***************************************************
        //Main loop.
        /*
        boolean running = true;
        int blockCount = 9;
        short count = 9;
        Frame[] frameBufferNext = new Frame[blockCount];
        RepetitionCompensation Rep = new RepetitionCompensation();
        while (running){
            try{
                Frame[] frameBuffer = frameBufferNext;
                frameBufferNext = new Frame[blockCount];

                boolean done = false;
                do{
                    byte[] buffer = new byte[514];
                    DatagramPacket packet = new DatagramPacket(buffer, 0, 514);
                    receiving_socket.receive(packet);
                    Frame temp = new Frame(buffer);
                    if(temp.frameNO <count){
                         frameBuffer[temp.frameNO%blockCount] = temp;
                    }else{
                        frameBufferNext[temp.frameNO%blockCount] = temp;
                        done = true;
                    }
                }while (!done);
                count+= blockCount;
                if (count > 32767)
                    count = 0;
                /**/
        
        boolean running = true;
        int blockCount = 9;
        short lowerBound = 0;
        short upperBound = (short) blockCount;
        short count = 9;
        boolean init = true;
        Frame[] frameBufferNext = new Frame[blockCount];
        RepetitionCompensation Rep = new RepetitionCompensation();
        while (running){
            try{
                Frame[] frameBuffer = frameBufferNext;
                frameBufferNext = new Frame[blockCount];

                boolean done = false;
                do{
                    byte[] buffer = new byte[514];
                    DatagramPacket packet = new DatagramPacket(buffer, 0, 514);
                    receiving_socket.receive(packet);
                    Frame temp = new Frame(buffer);
                    short frameNumber = (short) (temp.frameNO/blockCount);
                    if(init){
                        lowerBound = (short) (frameNumber* blockCount);
                        upperBound = (short) (lowerBound+ blockCount);
                        init = false;
                    }
                    if(temp.frameNO > upperBound){
                        frameBufferNext[temp.frameNO%blockCount] = temp;
                        done = true;
                    } else if (temp.frameNO > lowerBound){
                        frameBuffer[temp.frameNO%blockCount] = temp;
                    }
                    
                    
                    /*
                    if(temp.frameNO <count){
                         frameBuffer[temp.frameNO%blockCount] = temp;
                    }else{
                        frameBufferNext[temp.frameNO%blockCount] = temp;
                        done = true;
                    }
                    /**/
                }while (!done);
                lowerBound = upperBound;
                upperBound = (short) (lowerBound+ blockCount);
                count+= blockCount;
                if (count > 32767)
                    count = 0;
                
                Rep.fix(frameBuffer);
                
                /*
                for(int i = 0; i < frameBuffer.length; i ++){
                    if(frameBuffer[i] == null)
                        frameBuffer[i] = empty;
                }
                /**/
                //byte[] buffer = new byte[512];
                //DatagramPacket packet = new DatagramPacket(buffer, 0, 512);
                // Scumbag method for socket4
                /* for(int i = 0; i<512;i+=2){
                    byte[] subbuffer = new byte[2];
                    DatagramPacket packet = new DatagramPacket(subbuffer, 0, 2);
                    receiving_socket.receive(packet);
                    buffer[i]=subbuffer[0];
                    buffer[i+1]=subbuffer[1];
                }*/
                for(int i =0;i<blockCount;i++){
                    player.playBlock(frameBuffer[i].framedata);
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
