/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package socketTests;

import static socketTests.ReceiverThread.socketType.*;



/**
 *
 * @author scamp
 */
public class socketTester {

    public static void main(String[] args){
        System.out.println("*** TESTING SOCKET 1 ***");
        PacketLossTest.testSocket(Socket1);
        PacketOrderTest.testSocket(Socket1);
        PacketIntegrityTest.testSocket(Socket1);
        System.out.println("*** TESTING SOCKET 2 ***");
        PacketLossTest.testSocket(Socket2);
        PacketOrderTest.testSocket(Socket2);
        PacketIntegrityTest.testSocket(Socket2);
        System.out.println("*** TESTING SOCKET 3 ***");
        PacketLossTest.testSocket(Socket3);
        PacketOrderTest.testSocket(Socket3);
        PacketIntegrityTest.testSocket(Socket3);
        System.out.println("*** TESTING SOCKET 4 ***");
        PacketLossTest.testSocket(Socket4);
        PacketOrderTest.testSocket(Socket4);
        PacketIntegrityTest.testSocket(Socket4);
    }
}
