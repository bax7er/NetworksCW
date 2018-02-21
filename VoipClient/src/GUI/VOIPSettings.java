/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import audiotools.AudioPlayer;

/**
 *
 * @author scamp
 */
public class VOIPSettings {
    boolean interleave;
    boolean checksumPacket;
    boolean reorderPacket;
    int interleaverSize=0;
    int bufferSize;
    boolean repeatLastGoodPacket;
    int port;
    int bitrate;
    int socket;
    String hostname;
    
    void generateDefault(){
        switch(socket){
            case 0:
                interleave=false;
                checksumPacket=false;
                reorderPacket=false;
                interleaverSize=0;
                bufferSize=0;
                repeatLastGoodPacket=false;
                break;
            case 1:
                interleave=true;
                checksumPacket=false;
                reorderPacket=true;
                interleaverSize=3;
                bufferSize=9;
                repeatLastGoodPacket=true;
                break;
            case 2:
                interleave=false;
                checksumPacket=false;
                reorderPacket=true;
                interleaverSize=0;
                bufferSize=6;
                repeatLastGoodPacket=true;
                break;
            case 3:
                interleave=false;
                checksumPacket=true;
                reorderPacket=false;
                interleaverSize=0;
                bufferSize=0;
                repeatLastGoodPacket=true;
                break;
            default:
                interleave=false;
                checksumPacket=false;
                reorderPacket=false;
                interleaverSize=0;
                bufferSize=0;
                repeatLastGoodPacket=false;
                break;
        }
    }
}
