package GUIwithSocket4;


import audiotools.AudioRecorder;
import GUIwithSocket4.ReceiverThread.SocketType;
import audiotools.AudioPlayer.AudioPreset;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.LineUnavailableException;

import uk.ac.uea.cmp.voip.*;
import GUIwithSocket4.FrameCheck;

/**
 *
 * @author James Baxter
 */
class SenderThread implements Runnable{
    
    static DatagramSocket sending_socket;
    public int PORT = 55555; //Port to send to
    public String HOSTNAME = "CMPLEWIN-16";
    public InetAddress clientIP = null;
    public String IP = null;
    private SocketType socketType;
    private ArrayList<String> data;
    public Thread thread;
    private AudioPreset preset;
    private boolean running;
    public int sentCount;
    public boolean hostFailed;
    private Compensator comp;
    public void start(){
        this.thread = new Thread(this);
	thread.start();
    }
    public void stop(){
        running = false;
    }
    
    public SenderThread(SocketType s,String host,int port,AudioPreset a,int interleaverSize,boolean checkedPackets){
        socketType = s;
        HOSTNAME = host;
        preset = a;
        PORT = port;
        if(interleaverSize != 0){
            comp = new AlternativeInterleaver(interleaverSize);
        }
    }
    
    public void setUpConnection(){
        
        try {
                clientIP = InetAddress.getByName(HOSTNAME);
                IP = clientIP.getHostAddress();
	} catch (UnknownHostException e) {
                System.err.println("ERROR: Unknown Host: "+ HOSTNAME);
                running = false;
                hostFailed = true;
	}
        
        // Open sending socket
         try{
		 switch(socketType){
                    case Socket1: sending_socket = new DatagramSocket();
                    break;
                    case Socket2: sending_socket = new DatagramSocket2();
                    break;
                    case Socket3: sending_socket = new DatagramSocket3();
                    break;
                    case Socket4: sending_socket = new DatagramSocket4();
                    break;
                    default:sending_socket = new DatagramSocket();
                    break;}
	} catch (SocketException e){
                System.err.println("ERROR: Could not open UDP socket to send from.");
		e.printStackTrace();
                System.exit(0);
	}
               
    }
    @Override
    public void run (){
        running = true;
         setUpConnection();
        AudioRecorder recorder = null;
                 try {
            recorder = new AudioRecorder(preset);
        } catch (LineUnavailableException ex) {
            System.err.println("ERROR: Could not open AudioRecorder.");
            System.out.println("No recording device available, you will be able to recieve but not send voice");
        }
       
        
        
        short count = 0;
        while (running){
            try{
                byte[] block = recorder.getBlock();
                if (count > 32767)
                    count = 0;
                
                FrameCheck f = new FrameCheck(count,block);
                count++;
                if(comp ==null ){
                  DatagramPacket packet = new DatagramPacket(f.toByteArray(), f.toByteArray().length, clientIP, PORT);
                  sentCount++;
                  sending_socket.send(packet);
                }
                else{
                    comp.push(f);
                    FrameCheck[] fr = comp.pop();
                     if(fr !=null){
                  DatagramPacket packet = new DatagramPacket(fr[0].toByteArray(), fr[0].toByteArray().length, clientIP, PORT);
                  sending_socket.send(packet);
                    }
                     else{
                         System.out.println("Missing Data");
                     }
                }
            } catch (IOException e){
                System.err.println("ERROR:IO error occured - Sending thread");
                e.printStackTrace();
            }
        }
        recorder.close();
        //Close the socket
        sending_socket.close();
        //***************************************************
    }
}