package pus2022.server;

import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TcpServer {
    
    public static int tcpPort;
    private final ServerSocket serverSocket;
    public static HashSet<ClientThread> clientsPool = new HashSet<>();
    public static final Logger logger = Logger.getLogger(TcpServer.class.getName());

    
    public TcpServer(int port) throws IOException {
        serverSocket = new ServerSocket(port);
    }

    // Constantly wait for incoming client connections, accept it, add to pool and give him new thread.
    @SuppressWarnings("InfiniteLoopStatement")
    public void run() {
        while(true) {
            try {
                Socket clientSocket = serverSocket.accept();
                ClientThread clientThread = new ClientThread(clientSocket);
                clientsPool.add(clientThread);
                new Thread(clientThread).start();
            } catch(IOException ex) {
                logger.log(Level.WARNING, "Error ({0}) during accepting client:", ex.getMessage());
            }
        }
    }
    
    public static void main(String[] args) {
        try {
            String propFileName = "server.properties";
            Properties props = new Properties();
            props.load(new FileReader(propFileName));
            TcpServer.tcpPort = Integer.parseInt(props.getProperty("port"));
        } catch(IOException ex) {
            logger.log(Level.SEVERE, "Error reading configuration: {0}", ex.getMessage());
            System.exit(1);
        }
        logger.log(Level.INFO, "TCP Server is running...");
        try {
            TcpServer tcpServer = new TcpServer(TcpServer.tcpPort);
            tcpServer.run();
        } catch(IOException ex) {
            logger.log(Level.SEVERE, "Cannot create TcpServer instance (cause: {0}), aborting...", ex.getMessage());
        }

    }
}
