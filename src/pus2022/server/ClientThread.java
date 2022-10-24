package pus2022.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import static pus2022.server.TcpServer.clientsPool;
import static pus2022.server.TcpServer.logger;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONString;

public class ClientThread implements Runnable {
    private final Socket socket;
    private BufferedReader input;
    private PrintWriter output;
    private String login = null;
    private String line = null;

    private String getJsonError(String message) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", "ERROR");
        jsonObject.put("message", message);
        return jsonObject.toString();
    }

    private void doConversation() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", "WELCOME");
        jsonObject.put("from", socket.getRemoteSocketAddress());
        output.println(jsonObject);
        for(;;) {
            try {
                line = input.readLine();
                try {
                    jsonObject = new JSONObject(line);
                } catch (JSONException ex) {
                    logger.log(Level.SEVERE, "Error ({0}), while trying to pares ({1})", new Object[]{ex.getMessage(), line});
                    continue;
                }

                String type = jsonObject.get("type").toString();
                if (type == null) type = "null";

                switch (type) {
                    case "LOGIN" -> {
                        String newLogin = jsonObject.get("login").toString();
                        if (newLogin == null) {
                            jsonObject.put("type", "LOGOUT");
                            output.println(jsonObject);
                            login = null;
                            break;
                        }
                        login = newLogin;
                        jsonObject.put("type", "PROFILE");
                        output.println(jsonObject);
                    }
                    case "UNICAST" -> {
                        if (login == null) {
                            output.println(getJsonError("Not logged in"));
                            break;
                        }
                        String to = (String) jsonObject.get("to");
                        if (to == null) {
                            output.println(getJsonError("No destination for the unicast"));
                            break;
                        }
                        jsonObject.remove("to");
                        jsonObject.put("from", login);
                        String routedString = jsonObject.toString();
                        for (ClientThread clientThread : TcpServer.clientsPool) {
                            if (clientThread != this && clientThread.login != null && clientThread.login.equals(to)) {
                                clientThread.output.println(routedString);
                            }
                        }
                    }
                    default ->
                            logger.log(Level.WARNING, "Unknown type of JSON");

                }

            } catch (IOException ex) {
                logger.log(Level.SEVERE, "Problem with doing a conversation, cause: ({0})", ex.getMessage());
                return;
            }
        }

    }
    
    public ClientThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            logger.log(Level.INFO, "Client connected: {0}", socket.getRemoteSocketAddress());
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintWriter(socket.getOutputStream(), true);
            doConversation();
            logger.log(Level.INFO, "Client disconnected: {0}", socket.getRemoteSocketAddress());
            socket.close();
        } catch(IOException ex) {
            logger.log(Level.WARNING, "Error during handling a client socket: {0}", ex.getMessage());
        }
        clientsPool.remove(this);
    }

}
