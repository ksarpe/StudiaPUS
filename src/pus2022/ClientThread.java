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
public class ClientThread {
    private final Socket socket;
    
    private int doConversation(BufferedReader input, PrintWriter output) {
        int numberOfLines = 0;
        for(;;) {
            String line = null;
            try {
                line = input.readLine();
                numberOfLines++;
            } catch(IOException ex) {}
            if(line == null) break;
            output.println(line);
        }
        return numberOfLines;
    }
    
    public void start() {
        try {
            try(socket) {
                System.out.println("Client connected: " + socket.getRemoteSocketAddress());
                BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
                int numberOfLines = doConversation(input, output);
                System.out.println("Lines entered: " + numberOfLines);
                System.out.println("Client disconnected: " + socket.getRemoteSocketAddress());
            }
        } catch(IOException ex) {
            System.err.println("Error during handling a client socket: " + ex.getMessage());
        }
    }
    
    public ClientThread(Socket socket) {
        this.socket = socket;
    }
}
