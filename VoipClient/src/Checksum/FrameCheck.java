/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Checksum;

import java.awt.Graphics;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.nio.ByteBuffer;
import java.util.Objects;

/**
 *
 * @author Baxter
 */
public class FrameCheck implements Comparable<FrameCheck>{
    public short frameNO;
    public byte[] framedata;
    public FrameCheck(short frameNO, byte[] framedata){
        this.frameNO = frameNO;
        this.framedata  = framedata;
    }
    public FrameCheck(){
        frameNO = 0;
        framedata = new byte[512];
        for (int i = 0; i <512; i++)
            framedata[i] = 0;
    }
    
    public FrameCheck(FrameCheck source){
        frameNO = source.frameNO;
        framedata = new byte[512];
        for (int i = 0; i <512; i++)
            framedata[i] = source.framedata[i];
    }
    
    public FrameCheck(byte[] packetdata){
        ByteBuffer buffer = ByteBuffer.wrap(packetdata, 0, 2);
        frameNO = buffer.getShort();
        framedata = new byte[packetdata.length-2];
        System.arraycopy( packetdata, 2, framedata, 0, packetdata.length-2 );
    }
    
    public byte[] getPacketdata(){
        byte[] packetdata = new byte[framedata.length+12];
        //  & 0xFF masks all but the lowest eight bits.
        //  >> 8 discards the lowest 8 bits by moving all bits 8 places to the right
        
        //frameNo
        packetdata[1] = (byte) (frameNO & 0xFF);
        packetdata[0] = (byte) ((frameNO >> 8) & 0xFF);
        //frameNo check
        packetdata[3] = (byte) (frameNO & 0xFF);
        packetdata[2] = (byte) ((frameNO >> 8) & 0xFF);
        
        short checksum = 0;
        //130-1
        for(int i = 0;i < 125;i++){
            checksum= (short) (checksum + this.framedata[i]);
        }
        packetdata[130] = (byte) (checksum & 0xFF);
        packetdata[129] = (byte) ((checksum >> 8) & 0xFF);
        //261-1
        checksum = 0;
        for(int i = 125;i < 254;i++){
            checksum= (short) (checksum + this.framedata[i]);
        }
        packetdata[261] = (byte) (checksum & 0xFF);
        packetdata[260] = (byte) ((checksum >> 8) & 0xFF);
        //392-1
        checksum = 0;
        for(int i = 254;i < 383;i++){
            checksum= (short) (checksum + this.framedata[i]);
        }
        packetdata[392] = (byte) (checksum & 0xFF);
        packetdata[391] = (byte) ((checksum >> 8) & 0xFF);
        //523-1
        checksum = 0;
        for(int i = 383;i < 512;i++){
            checksum= (short) (checksum + this.framedata[i]);
        }
        packetdata[523] = (byte) (checksum & 0xFF);
        packetdata[522] = (byte) ((checksum >> 8) & 0xFF);
        
        
        System.arraycopy( framedata, 0, packetdata, 4, 125 );
        System.arraycopy( framedata, 125, packetdata, 131, 129 );
        System.arraycopy( framedata, 254, packetdata, 262, 129 );
        System.arraycopy( framedata, 383, packetdata, 393, 129 );
        return packetdata;
    }

    public boolean[] getDataFromPacket(byte[] packetData){
        boolean check[] = new boolean[5];
        
        if(packetData[0] == packetData[2] && packetData[1] == packetData[3]){
            ByteBuffer buffer = ByteBuffer.wrap(packetData, 0, 2);
            frameNO = buffer.getShort();
            check[0] = true;
        }else{
            check[0] = false;
            check[1] = false;
            check[2] = false;
            check[3] = false;
            check[4] = false;
            return check;
        }
        
        short checksum = 0;
        short checksumStar = 0;
        
        //130-1
        for(int i = 4;i < 129;i++){
            checksum= (short) (checksum + packetData[i]);
        }
        ByteBuffer buffer = ByteBuffer.wrap(packetData, 129, 2);
        checksumStar = buffer.getShort();
        if(checksum == checksumStar){
            check[1] = true;
            System.arraycopy( packetData, 4, framedata, 0, 125 );
        }else
            check[1] = false;
        
        //261-1
        checksum = 0;
        for(int i = 131;i < 260;i++){
            checksum= (short) (checksum + packetData[i]);
        }
        buffer = ByteBuffer.wrap(packetData, 260, 2);
        checksumStar = buffer.getShort();
        if(checksum == checksumStar){
            check[2] = true;
            System.arraycopy( packetData, 131, framedata, 125, 129 );
        }else
            check[2] = false;
        
        //392-1
        checksum = 0;
        for(int i = 262;i < 391;i++){
            checksum= (short) (checksum + packetData[i]);
        }
        buffer = ByteBuffer.wrap(packetData, 391, 2);
        checksumStar = buffer.getShort();
        if(checksum == checksumStar){
            check[3] = true;
            System.arraycopy( packetData, 262, framedata, 254, 129 );
        }else
            check[3] = false;
        
        //523-1
        checksum = 0;
        for(int i = 393;i < 522;i++){
            checksum= (short) (checksum + packetData[i]);
        }
        buffer = ByteBuffer.wrap(packetData, 522, 2);
        checksumStar = buffer.getShort();
        if(checksum == checksumStar){
            check[4] = true;
            System.arraycopy( packetData, 393, framedata, 383, 129 );
        }else
            check[4] = false;
        
        return check;
    }
            
    @Override
    public int compareTo(FrameCheck o) {
        return this.frameNO - o.frameNO;
    }

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
    
    @Override
    public String toString(){
        StringBuilder str = new StringBuilder();
        str.append(frameNO);
        for (int i = 0; i < framedata.length; i++){
            str.append(framedata[i]);
        }
        
        return str.toString();
    }
}
