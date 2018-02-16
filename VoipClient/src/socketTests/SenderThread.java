package socketTests;


import CMPC3M06.AudioRecorder;
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
import socketTests.ReceiverThread.SocketType;
import uk.ac.uea.cmp.voip.*;

/**
 *
 * @author James Baxter
 */
class SenderThread implements Runnable{
    
    static DatagramSocket sending_socket;
    public static final int PORT = 55555; //Port to send to
    public String HOSTNAME = "CMPLEWIN-16";
    public InetAddress clientIP = null;
    public String IP = null;
    private SocketType socketType;
    private ArrayList<String> data;
    public Thread thread;
   
    
    public void start(){
        this.thread = new Thread(this);
	thread.start();
    }
    
    public SenderThread(SocketType s,String host,ArrayList<String> data){
        socketType = s;
        HOSTNAME = host;
        this.data = data;
    }
    
    public void setUpConnection(){
        
        try {
                clientIP = InetAddress.getByName(HOSTNAME);
                IP = clientIP.getHostAddress();
	} catch (UnknownHostException e) {
                System.err.println("ERROR: Unknown Host: "+ HOSTNAME);
		e.printStackTrace();
                System.exit(0);
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
    public void run (){
        setUpConnection();
       
        for(String str: data){
             try{
            byte[] buffer = str.getBytes();
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, clientIP, PORT);
            sending_socket.send(packet);
             }catch (IOException e){
                System.err.println("ERROR:IO error occured - Sending thread");
                e.printStackTrace();
            }
        }
        
        //Close the socket
        sending_socket.close();
        //***************************************************
    }
}
