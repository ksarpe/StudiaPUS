package pus2022.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;

/**
 *
 * @author Kasper Janowski
 */
public class TcpServer {
    
    public static int tcpPort = 5555;
    
    private final ServerSocket serverSocket;
    
    public static HashSet<ClientThread> clientsPool = new HashSet<>();
    
    public TcpServer(int port) throws IOException {
        serverSocket = new ServerSocket(port);
    }
    
    public void run() {
        for(;;) {
            try {
                Socket clientSocket = serverSocket.accept();
                ClientThread clientThread = new ClientThread(clientSocket);
                clientsPool.add(clientThread);
                new Thread(clientThread).start();
            } catch(IOException ex) {
                System.err.println("Error (" + ex.getMessage() + ") during accepting tcpServer.serverSocket");
            }
        }
    }
    
    public static void main(String[] args) {
        System.out.println("TCP Server 0.0.1");
        try {
            TcpServer tcpServer = new TcpServer(TcpServer.tcpPort);
            tcpServer.run();
        } catch(IOException ex) {
            System.err.println("Cannot create TcpServer instance (cause: " + ex.getMessage() + "), aborting...");
        }
    }
}
