/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package voipclient;

import java.awt.Graphics;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.nio.ByteBuffer;

/**
 *
 * @author Baxter
 */
public class Frame implements Comparable<Frame>{
    public short frameNO;
    public byte[] framedata;
    public Frame(short frameNO, byte[] framedata){
        this.frameNO = frameNO;
        this.framedata  = framedata;
    }
    public Frame(){
        frameNO = 0;
        framedata = new byte[512];
        for (int i = 0; i <512; i++)
            framedata[i] = 0;
    }
    
    public Frame(Frame source){
        frameNO = source.frameNO;
        framedata = new byte[512];
        for (int i = 0; i <512; i++)
            framedata[i] = source.framedata[i];
    }
    
    public Frame(byte[] packetdata){
        ByteBuffer buffer = ByteBuffer.wrap(packetdata, 0, 2);
        frameNO = buffer.getShort();
        framedata = new byte[packetdata.length-2];
        System.arraycopy( packetdata, 2, framedata, 0, packetdata.length-2 );
    }
    
    public byte[] getPacketdata(){
        byte[] packetdata = new byte[framedata.length+2];
        //  & 0xFF masks all but the lowest eight bits.
        //  >> 8 discards the lowest 8 bits by moving all bits 8 places to the right
        packetdata[1] = (byte) (frameNO & 0xFF);
        packetdata[0] = (byte) ((frameNO >> 8) & 0xFF);
        System.arraycopy( framedata, 0, packetdata, 2, framedata.length );
        return packetdata;
    }

    @Override
    public int compareTo(Frame o) {
        return this.frameNO - o.frameNO;
    }

    public Frame getHalvedAmp(){
        Frame halved = new Frame(this);
        byte dif = 2;
        for (int i = 0; i <512; i++)
            halved.framedata[i] = (byte) (halved.framedata[i]/dif);
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
