/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package socketTests;

import CMPC3M06.AudioPlayer;
import fileIO.OutFile;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
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
    public ArrayList<String> data;
    public Thread thread;
    private LongWrapper timestamp;
    
    public ReceiverThread(socketType s,ArrayList<String> recievedData){
        socketType = s;
        data = recievedData;
        timestamp = null;
    }
    public ReceiverThread(socketType s,ArrayList<String> recievedData,LongWrapper l){
        socketType = s;
        data = recievedData;
        timestamp = l;
    }
    public void start(){
        this.thread = new Thread(this);
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
        OutFile of = new OutFile("socket4.csv");
        int i = 0;
        while (running){
            try{
                byte[] buffer = new byte[80];
                DatagramPacket packet = new DatagramPacket(buffer, 0, 80);
                receiving_socket.receive(packet);
                //Get a string from the byte buffer
                if(timestamp!=null){
                    timestamp.data = System.nanoTime();
                }
                String str = new String(buffer);
                //Display it
                //of.writeLine(String.valueOf(i)+','+str);
                //System.out.println(str);
                i++;
                data.add(str);
                }   
            catch (SocketTimeoutException e){
                running = false;
            }
            catch (IOException ex) {
                Logger.getLogger(ReceiverThread.class.getName()).log(Level.SEVERE, null, ex);
                }
            
        }
        //Close the socket
        of.closeFile();
        receiving_socket.close();
        //***************************************************
    }
}
