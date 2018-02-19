package Checksum;

import GUI.FrameCheck;
import voipclient.*;
import CMPC3M06.AudioRecorder;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.LineUnavailableException;
import uk.ac.uea.cmp.voip.*;

/**
 *
 * @author James Baxter
 */
class SenderThread implements Runnable {

    static DatagramSocket sending_socket;
    public static final int PORT = 55555; //Port to send to
    public final String HOSTNAME = "127.0.0.1";
    public InetAddress clientIP = null;
    public String IP = null;

    public void start() {
        Thread thread = new Thread(this);
        thread.start();
    }

    public void setUpConnection() {

        try {
            clientIP = InetAddress.getByName(HOSTNAME);
            IP = clientIP.getHostAddress();
        } catch (UnknownHostException e) {
            System.err.println("ERROR: Unknown Host: " + HOSTNAME);
            e.printStackTrace();
            System.exit(0);
        }

        // Open sending socket
        try {
            sending_socket = new DatagramSocket2();
        } catch (SocketException e) {
            System.err.println("ERROR: Could not open UDP socket to send from.");
            e.printStackTrace();
            System.exit(0);
        }

    }

    public void run() {
        setUpConnection();
        AudioRecorder recorder = null;
        try {
            recorder = new AudioRecorder();
        } catch (LineUnavailableException ex) {
            System.err.println("ERROR: Could not open AudioRecorder.");
            System.out.println("No recording device available, you will be able to recieve but not send voice");
        }

        boolean running = true;

        short count = 5;
        while (running) {
            try {
                byte[] seqData = new byte[512];
                for(int i = 0;i<512;i++){
                    seqData[i] = (byte) (i%64);
                }
              //  FrameCheck f = new FrameCheck(count, recorder.getBlock());
               FrameCheck f = new FrameCheck(count, seqData);
               
               
                count++;

                
                DatagramPacket packet = new DatagramPacket(f.toByteArray(), f.toByteArray().length, clientIP, PORT);

                byte[] output = f.toByteArray();
                FrameCheck post = new FrameCheck(packet.getData());

                for(int i = 0;i<512;i++){
                    byte a = seqData[i];
                    if(a != post.framedata[i]){
                        System.out.println("Error at index: " + i);
                        System.out.println("Was: "+ a + " Got: " + post.framedata[i]);
                    }
                }
                post.framedata[66] = (byte)99;
                boolean boolAray[] = post.verifyIntegrity();
                System.out.println("");
                //boolAray = test.getDataFromPacket(packet.getData());
                sending_socket.send(packet);
                //Make a DatagramPacket from it, with client address and port number

            } catch (IOException e) {
                System.err.println("ERROR:IO error occured - Sending thread");
                e.printStackTrace();
            }
        }

        //Close the socket
        sending_socket.close();
        //***************************************************
    }

    private void setUpAudioRecorder(AudioRecorder recorder) {
        try {
            recorder = new AudioRecorder();
        } catch (LineUnavailableException ex) {
            System.err.println("ERROR: Could not open AudioRecorder.");
            System.out.println("No recording device available, you will be able to recieve but not send voice");
        }
    }
}
