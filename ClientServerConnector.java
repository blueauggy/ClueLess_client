

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class ClientServerConnector extends Thread {
    
    private final Socket clientSocket; // Socket to Server
    
    /**
     * ClientServerConnector()
     * Constructor for the ClientServerConnector class
     * @param socket Server Socket
     */
    public ClientServerConnector(Socket socket) {
        this.clientSocket = socket;
    }
    
    /**
     * run()
     * Main thread execution procedures
     */
    @Override
    public void run() {
        try {
            // Keep listening for messages
            while(true) {
                InputStream inputStream = this.clientSocket.getInputStream();
                DataInputStream input = new DataInputStream(inputStream);
                // Wait for input
                String message = input.readUTF();
                // Output message (clear line before doing so
                System.out.println("\b" + message);
                // Output newline
                System.out.print(":");
            }
        }
        catch(IOException ex) {
            System.err.println(ex.getMessage());
        }
        catch(Exception ex) { 
            System.err.println(ex.getMessage());
        }
    }
}
