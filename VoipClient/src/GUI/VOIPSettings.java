
package GUI;


/**
 *
 * @author James Baxter
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
    boolean extraData;
    String hostname;
    boolean redundantData = false;
    
    void generateDefault(){
        switch(socket){
            case 0:
                interleave=false;
                checksumPacket=false;
                reorderPacket=false;
                interleaverSize=0;
                bufferSize=0;
                repeatLastGoodPacket=false;
                extraData = false;
                break;
            case 1:
                interleave=true;
                checksumPacket=false;
                reorderPacket=true;
                interleaverSize=3;
                bufferSize=9;
                repeatLastGoodPacket=true;
                extraData = false;
                break;
            case 2:
                interleave=false;
                checksumPacket=false;
                reorderPacket=true;
                interleaverSize=0;
                bufferSize=6;
                repeatLastGoodPacket=true;
                extraData = false;
                break;
            case 3:
                interleave=false;
                checksumPacket=true;
                reorderPacket=false;
                interleaverSize=0;
                bufferSize=0;
                repeatLastGoodPacket=false;
                extraData = false;
                break;
            default:
                interleave=false;
                checksumPacket=false;
                reorderPacket=false;
                interleaverSize=0;
                bufferSize=0;
                repeatLastGoodPacket=false;
                extraData = false;
                break;
        }
    }
}
