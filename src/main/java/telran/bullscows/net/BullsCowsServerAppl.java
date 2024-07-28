package telran.bullscows.net;

import telran.net.*;

public class BullsCowsServerAppl {

	private static final int PORT = 5000;

    public static void main(String[] args) {
        Protocol protocol = new BullsCowsProtocol();
        TcpServer tcpServer = new TcpServer(protocol, PORT);
        tcpServer.run();
    }
}


