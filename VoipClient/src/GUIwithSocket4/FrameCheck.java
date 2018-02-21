/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUIwithSocket4;

import java.awt.Graphics;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.nio.ByteBuffer;
import java.util.Objects;
import voipclient.Frame;

/**
 *
 * @author Baxter
 */
public class FrameCheck extends Frame {

    public short frameNOChecksum;
    public short[] checkSums = new short[4];

    public FrameCheck(short frameNO, byte[] framedata) {
        super(frameNO,framedata);
        generateChecksums();
    }

    public FrameCheck() {
        super();
    }

    public FrameCheck(FrameCheck source) {
        super(source);
        frameNOChecksum = source.frameNOChecksum;
        checkSums = source.checkSums.clone();
    }

    /**
     * Construct a frame from a received datagram packet
     * @param packetdata 
     */
    public FrameCheck(byte[] packetdata) {
        ByteBuffer buffer = ByteBuffer.wrap(packetdata, 0, 2);
        frameNO = buffer.getShort();
        
        frameNOChecksum = ByteBuffer.wrap(packetdata, 2, 2).getShort();
        
        checkSums[0] = ByteBuffer.wrap(packetdata, 132, 2).getShort();
        checkSums[1] = ByteBuffer.wrap(packetdata, 262, 2).getShort();
        checkSums[2] = ByteBuffer.wrap(packetdata, 392, 2).getShort();
        checkSums[3] = ByteBuffer.wrap(packetdata, 522, 2).getShort();
        
        framedata = new byte[512];
        
        System.arraycopy(packetdata, 4, framedata, 0, 128);
        System.arraycopy(packetdata, 134, framedata, 128, 128);
        System.arraycopy(packetdata, 264, framedata, 256, 128);
        System.arraycopy(packetdata, 394, framedata, 384, 128);
        
        generateChecksums();
    }

    @Override
    public FrameCheck getHalvedAmp(){
        FrameCheck halved = new FrameCheck(this);
        byte dif = 2;
        for (int i = 0; i <512; i+=2){
            ByteBuffer buffer = ByteBuffer.wrap(halved.framedata, i, 2);
            short temp = buffer.getShort();
            temp = (short) (temp/dif);
            halved.framedata[1] = (byte) (temp & 0xFF);
            halved.framedata[0] = (byte) ((temp >> 8) & 0xFF);
            //halved.framedata[i] = (byte) (halved.framedata[i]/dif);
        }
        return halved;
    }
    
    public void halveQuarter(int quarterDest, FrameCheck frameSource, int quarterSource){
        byte dif = 2;
        int startSource = quarterSource * 128;
        int startDest = quarterDest * 128;
        ByteBuffer buffer;
        
        for (int i = 0; i <128; i+=2){
            buffer = ByteBuffer.wrap(frameSource.framedata, startSource + i, 2);
            short temp = buffer.getShort();
            temp = (short) (temp/dif);
            this.framedata[startDest+ i + 1] = (byte) (temp & 0xFF);
            this.framedata[startDest + i] = (byte) ((temp >> 8) & 0xFF);
        }
        
        /*Alternative
        int endDest = (quarterSource+1) * 128;
        int i = startSource;
        int j = startDest;
        while(i < endDest){
            buffer = ByteBuffer.wrap(frameSource.framedata, i, 2);
            short temp = buffer.getShort();
            temp = (short) (temp/dif);
            this.framedata[j + 1] = (byte) (temp & 0xFF);
            this.framedata[j] = (byte) ((temp >> 8) & 0xFF);
            i+=2;
            j+=2;
        }
        /**/
    }
    
    public void zeroQuarter(int quarter){
        int start = quarter * 128;
        int end = (quarter + 1) * 128;
        
        for (int i = start; i <end; i++){
            this.framedata[i] = 0;
        }
    }
    
    /**
     * Generates a byte array representation for sending across the network
     * @return 
     */
    @Override
    public byte[] toByteArray() {
        // 512bytes of audio data + 2 bytes checksum on frame number
        // 2byte checksum for each quater + 2byte frame number
        // total of 12 bytes
        byte[] packetdata = new byte[framedata.length + 12];
        //  & 0xFF masks all but the lowest eight bits.
        //  >> 8 discards the lowest 8 bits by moving all bits 8 places to the right

        //frameNo
        packetdata[1] = (byte) (frameNO & 0xFF);
        packetdata[0] = (byte) ((frameNO >> 8) & 0xFF);
        
        //frameNo check
        packetdata[3] = (byte) (frameNO & 0xFF);
        packetdata[2] = (byte) ((frameNO >> 8) & 0xFF);
        
        //First Checksum
        //Data starts at 4 and runs to 131
        packetdata[133] = (byte) (checkSums[0] & 0xFF);
        packetdata[132] = (byte) ((checkSums[0] >> 8) & 0xFF);
        System.arraycopy(framedata, 0, packetdata, 4, 128);
        
        //Second Checksum
        //Data starts at 134 and runs to 261
        packetdata[263] = (byte) (checkSums[1] & 0xFF);
        packetdata[262] = (byte) ((checkSums[1] >> 8) & 0xFF);
        System.arraycopy(framedata, 128, packetdata, 134, 128);
        
        //Third Checksum
        //Data starts at 264 and runs to 391
        packetdata[393] = (byte) (checkSums[2] & 0xFF);
        packetdata[392] = (byte) ((checkSums[2] >> 8) & 0xFF);
        System.arraycopy(framedata, 256, packetdata, 264, 128);
        
        //Forth Checksum
        //Data starts at 394 and runs to 521
        packetdata[523] = (byte) (checkSums[3] & 0xFF);
        packetdata[522] = (byte) ((checkSums[3] >> 8) & 0xFF);
        System.arraycopy(framedata, 384, packetdata, 394, 128);
                
        return packetdata;
    }

    /**
     * Verifies checksums for the data
     * @return first index checks packet number, following index for each quarter
     * false indicates failed integrity
     */
    public boolean[] verifyIntegrity(){
        boolean valid[] = new boolean[5];
        if(frameNOChecksum != frameNO){
            for(boolean b : valid){
                b = false;
            }
        }
        else{
            valid[0] = true;
            for(int i = 1;i<=4;i++){
                valid[i] = (checkSums[i-1]==generateChecksums(i-1));
            }
        }
        return valid;
    }
    

    @Override
    public String toString() {
        return super.toString();
    }

    private void generateChecksums() {
        for(int i = 0;i<4;i++){
            checkSums[i]=generateChecksums(i);
        }
    }
    private short generateChecksums(int quarter) {


            short checksum = 0;
            int start = quarter * 128;
            int end = (quarter + 1) * 128;
            for (int i = start; i < end; i++) {
                checksum = (short) (checksum + this.framedata[i]);
            }
            return checksum;
    }
    
    public void fix(FrameCheck previous){
        boolean valid[] = this.verifyIntegrity();
        if(valid[0] ==false){
            FrameCheck temp = previous.getHalvedAmp();
            this.checkSums = temp.checkSums;
            this.frameNO = temp.frameNO;
            this.frameNOChecksum = temp.frameNOChecksum;
            this.framedata = temp.framedata;
        }else{
            for(int i = 0; i < 4; i++){
                if(valid[i] == false){
                    int start = i * 128;
                    int end = (i + 1) * 128;
                    for (int j = start; j < end; j++) {
                        this.framedata[i] = 0;
                    }
                }
            }
        }
    }
}
