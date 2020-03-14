package HttpPackage;

import java.io.IOException;
import java.net.*;

public class Server {

    private static final int PORT = 80;
    private static ServerSocket serverSocket;


    public Server() throws IOException {
    }

    public static void main(String arg[]) throws Exception {

        serverSocket = new ServerSocket(PORT);
        System.out.println("HttpPackage.Server started on port: " + PORT);

        while (true) {
            Socket clientSocket = serverSocket.accept(); //Listens for a connection to be made to this socket and accepts it.
            System.out.println("New client connected");
            //ClientHandler clientHandler = new ClientHandler(clientSocket);
            //Thread thread = new Thread(clientHandler);
            //thread.start();
        }

    }
}


