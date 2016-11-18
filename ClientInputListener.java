

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Scanner;

public class ClientInputListener extends Thread {
    
    private final OutputStream outputStream; // Output Stream
    
    /**
     * ClientInputListener()
     * Constructor for the ClientInputListener class
     * @param outputStream Server Connection Output Stream
     */
    public ClientInputListener(OutputStream outputStream) {
        this.outputStream = outputStream;
    }
    
    /**
     * run()
     * Main thread execution procedures
     */
    @Override
    public void run() {
        // Create keyboard input listener
        Scanner input = new Scanner(System.in);
       
        // Get username
        System.out.print("Enter a username: ");
        String username = input.nextLine();
       
        try {
            DataOutputStream output = 
                    new DataOutputStream(this.outputStream);
            output.writeUTF(username);
            // Now get list of clients
            output.writeUTF("SHOW USERS");
        }
        catch(IOException ex) {
            System.err.println(ex.getMessage());
        }
        
        // Keep listening for input
        while(true) {
            System.out.print(":");
            String message = input.nextLine();
            
            // Attempt to send message
            try {
                DataOutputStream output = 
                        new DataOutputStream(this.outputStream);
                output.writeUTF(message);
            }
            catch(IOException ex) {
                System.err.println(ex.getMessage());
            }
        }
    }
}
