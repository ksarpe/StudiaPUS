package pus2022;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 *
 * @author jaroc
 */
public class ClientThread implements Runnable {
    private final Socket socket;
    private BufferedReader input;
    private PrintWriter output;
    
    private int doConversation(BufferedReader input, PrintWriter output) {
        int numberOfLines = 0;
        output.println("Welcome, " + socket.getRemoteSocketAddress());
        for(;;) {
            String line = null;
            try {
                line = input.readLine();
                numberOfLines++;
            } catch(IOException ex) {}
            if(line == null) break;
            if(line.startsWith("?")) {
                output.println("Current number of threads: " + TcpServer.clientsPool.size());
            } else {
                int counter = 0;
                for(ClientThread clientThread: TcpServer.clientsPool) {
                    if(clientThread != this) {
                        clientThread.output.println(socket.getRemoteSocketAddress() + ">> " +line);
                        counter++;
                    }
                }
                output.println("Message sent to " + counter + " receivers");
            }
        }
        return numberOfLines;
    }
    
    public ClientThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            System.out.println("Client connected: " + socket.getRemoteSocketAddress());
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintWriter(socket.getOutputStream(), true);
            int numberOfLines = doConversation(input, output);
            System.out.println("Lines entered: " + numberOfLines);
            System.out.println("Client disconnected: " + socket.getRemoteSocketAddress());
            socket.close();
        } catch(IOException ex) {
            System.err.println("Error during handling a client socket: " + ex.getMessage());
        }
        TcpServer.clientsPool.remove(this);
    }
}
