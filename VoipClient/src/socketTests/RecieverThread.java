/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package socketTests;

import CMPC3M06.AudioPlayer;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.LineUnavailableException;
import uk.ac.uea.cmp.voip.*;

/**
 *
 * @author xwd14rfu
 */
class ReceiverThread implements Runnable{
    
    static DatagramSocket receiving_socket;
    private static final int PORT = 55555;
    public enum socketType{Socket1,Socket2,Socket3,Socket4};
    private socketType socketType;
    
    public ReceiverThread(socketType s){
        socketType = s;
    }
    public void start(){
        Thread thread = new Thread(this);
	thread.start();
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
        //***************************************************
        //Main loop.
        
        boolean running = true;
        
        while (running){
            try{
                byte[] buffer = new byte[80];
                DatagramPacket packet = new DatagramPacket(buffer, 0, 80);
                receiving_socket.receive(packet);
                //Get a string from the byte buffer
                String str = new String(buffer);
                //Display it
                System.out.println(str);
                }   
            catch (SocketTimeoutException e){
                
            }
            catch (IOException ex) {
                Logger.getLogger(ReceiverThread.class.getName()).log(Level.SEVERE, null, ex);
                }
            
        }
        //Close the socket
        receiving_socket.close();
        //***************************************************
    }
}