/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUIwithSocket4;

import audiotools.AudioPlayer;
import audiotools.AudioPlayer.AudioPreset;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import javax.sound.sampled.LineUnavailableException;
import uk.ac.uea.cmp.voip.*;
import GUIwithSocket4.FrameCheck;

/**
 *
 * @author James Baxter
 */
class ReceiverThread implements Runnable{
    
    static DatagramSocket receiving_socket;
    private int PORT = 55555;
    public enum SocketType{Socket1,Socket2,Socket3,Socket4;
    public static SocketType getSocket(int i){
        return SocketType.values()[i];
    }};
    private SocketType socketType;
    public ArrayList<String> data;
    public Thread thread;
    private AudioPreset preset;
    private boolean running;
    public int recCount;
    public boolean timeout = false;
    
    public ReceiverThread(SocketType s,int port,AudioPreset a){
        socketType = s;
        PORT = port;
        preset = a;
    }
    public void start(){
        this.thread = new Thread(this);
	thread.start();
    }
    
    public void stop(){
        running = false;
    }
    public void setUpSocket(){
         try{
                switch(socketType){
                    case Socket1: receiving_socket = new DatagramSocket(PORT);
                    break;
                    case Socket2: receiving_socket = new DatagramSocket2(PORT);
                    break;
                    case Socket3: receiving_socket = new DatagramSocket3(PORT);
                    break;
                    case Socket4: receiving_socket = new DatagramSocket4(PORT);
                    break;
                    default:receiving_socket = new DatagramSocket(PORT);
                    break;
                }
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
            player = new AudioPlayer(preset);
        } catch (LineUnavailableException ex) {
           System.err.println("ERROR: Could not open AudioRecorder.");
           System.out.println("No playback device available");
           System.exit(0);
        }
        //***************************************************
        //Main loop.
        
        running = true;
        PacketFixer fixer = new PacketFixer();
        fixer.repeat = false;
        while (running){
            try{
                    byte[] buffer = new byte[524];
                    DatagramPacket packet = new DatagramPacket(buffer, 0, 524);
                    receiving_socket.receive(packet);
                    recCount++;
                    timeout = false;
                    FrameCheck temp = new FrameCheck(buffer);
                    fixer.push(temp);
                    FrameCheck[] playback = fixer.pop();
                    int tempNo = -1;
                    for(FrameCheck f:playback){
                        if(tempNo == f.frameNO)
                        System.out.println("Playing packet: "+f.frameNO);
                        tempNo = f.frameNO;
                       player.playBlock(f.framedata);
                    }
                //player.playBlock(packet.getData());
            }catch (SocketTimeoutException e) {
                timeout=true;
            } catch (IOException e){
                System.err.println("ERROR:IO error occured - Reciever thread");
                e.printStackTrace();
            }
        }
        player.close();
        player = null;
        //Close the socket
        receiving_socket.close();
        //***************************************************
    }
}