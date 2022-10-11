package pus2022;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author jaroc
 */
public class TcpServer {
    
    public static int tcpPort = 5555;
    
    private ServerSocket serverSocket;
    
    public TcpServer(int port) throws IOException {
        serverSocket = new ServerSocket(port);
    }
    
    public void run() {
        for(;;) {
            try {
                Socket clientSocket = serverSocket.accept();
                ClientThread clientThread = new ClientThread(clientSocket);
                clientThread.start();
            } catch(IOException ex) {
                System.err.println("Error during accepting tcpServer.serverSocket");
            }
        }
    }
    
    public static void main(String[] args) {
        System.out.println("TCP Server 0.0.1");
        try {
            TcpServer tcpServer = new TcpServer(TcpServer.tcpPort);
            tcpServer.run();
        } catch(IOException ex) {
            System.err.println("Cannot create TcpServer instance, aborting...");
        }
    }
}
