/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package socketTests;

import static socketTests.ReceiverThread.SocketType.*;



/**
 *
 * @author scamp
 */
public class socketTester {

    public static void main(String[] args){
        System.out.println("*** TESTING SOCKET 1 ***");
       //runSocketTest(Socket1);
        System.out.println("*** TESTING SOCKET 2 ***");
       // runSocketTest(Socket2);
        System.out.println("*** TESTING SOCKET 3 ***");
        //runSocketTest(Socket3);
        System.out.println("*** TESTING SOCKET 4 ***");
        runSocketTest(Socket4);
    }
    static void runSocketTest(ReceiverThread.SocketType s){
        PacketLossTest.testSocket(s);
        PacketOrderTest.testSocket(s);
        PacketIntegrityTest.testSocket(s);
        LatencyTest.testSocket(s);
    }
}
