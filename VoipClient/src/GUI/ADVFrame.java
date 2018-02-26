/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import java.nio.ByteBuffer;

/**
 *
 * @author xwd14rfu
 */
public class ADVFrame extends Frame {

    public byte[] next;

    public void addNext(byte[] nextFrame) {
        /*
        next = new byte[128];
        for(int i =0;i<126;i+=2){
            next[i]=nextFrame[i*4];
            next[i+1]=nextFrame[(i*4)+1];
        }
         */
        next = new byte[256];
        for (int i = 0; i < 254; i += 2) {
            next[i] = nextFrame[i * 2];
            next[i + 1] = nextFrame[(i * 2) + 1];
        }
    }

    @Override
    public byte[] toByteArray() {
        byte[] superdata = super.toByteArray();
        byte[] data = new byte[superdata.length + next.length];
        System.arraycopy(superdata, 0, data, 0, superdata.length);
        System.arraycopy(next, 0, data, superdata.length, next.length);
        return data;
    }

    public ADVFrame(byte[] packetdata) {

        ByteBuffer buffer = ByteBuffer.wrap(packetdata, 0, 2);
        frameNO = buffer.getShort();
        framedata = new byte[512];
        System.arraycopy(packetdata, 2, framedata, 0, 512);
        //next = new byte[128];
        //System.arraycopy(packetdata, 514, next, 0, 128);
        next = new byte[256];
        System.arraycopy(packetdata, 514, next, 0, 256);

    }

    public ADVFrame(short count, byte[] data) {
        super(count, data);
    }

    public byte[] decompressNext() {
        byte[] decomp = new byte[512];
        /*
        for(int i =0;i<127;i+=2){
            byte upper = next[i];
            byte lower = next[i+1];
            for(int j =0;j<4;j++){
                decomp[(i*4)+(j*2)] = upper;
                decomp[(i*4)+(j*2)+1] = lower;
            }
        }
        }
        return decomp;*/
        for (int i = 0; i < 255; i += 2) {
            byte upper = next[i];
            byte lower = next[i + 1];
            for (int j = 0; j < 2; j++) {
                decomp[(i * 2) + (j * 2)] = upper;
                decomp[(i * 2) + (j * 2) + 1] = lower;
            }
        }
        return decomp;
    }

}
