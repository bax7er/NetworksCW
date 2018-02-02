/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lab3;

import java.nio.ByteBuffer;

/**
 *
 * @author Baxter
 */
public class Frame {
    public short frameNO;
    public byte[] framedata;
    public Frame(short frameNO, byte[] framedata){
        this.frameNO = frameNO;
        this.framedata  = framedata;
    }
    public Frame(byte[] packetdata){
        ByteBuffer buffer = ByteBuffer.wrap(packetdata, 0, 2);
        frameNO = buffer.getShort();
        System.arraycopy( packetdata, 2, framedata, 0, packetdata.length-2 );
    }
    
    public byte[] getPacketdata(){
        byte[] packetdata = new byte[framedata.length+2];
        //  & 0xFF masks all but the lowest eight bits.
        //  >> 8 discards the lowest 8 bits by moving all bits 8 places to the right
        packetdata[0] = (byte) (frameNO & 0xFF);
        packetdata[1] = (byte) ((frameNO >> 8) & 0xFF);
        System.arraycopy( framedata, 0, packetdata, 2, framedata.length );
        return packetdata;
    }
}
