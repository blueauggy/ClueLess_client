

/**
 * ServerClientConnector.java
 * Contains the server client connector class
 * @author Cory Gehr (cmg5573)
 */
public class ServerClientConnector extends Thread {
    
    private final MafiaServer serverObject; // MafiaServer object
    private final int clientIndex;    // The client we're connected to
    
    /**
     * ServerClientConnector()
     * Constructor for the ServerClientConnector class
     * @param client Participant object
     * @param serverObject IMServer Object
     */
    public ServerClientConnector(int clientIndex, MafiaServer serverObject) {
        this.clientIndex = clientIndex;
        this.serverObject = serverObject;
    }
    
    /**
     * run()
     * Main thread execution procedures
     */
    @Override
    public void run() {
        while(true) {
            // Wait for a message from the participant
            this.parseInput(
                    this.serverObject.clients.get(clientIndex).getInput());
        }
    }
    
    /**
     * parseInput()
     * Determines an action to perform based on user input
     * @param input User Input
     */
    public void parseInput(String input) {
        // Separator string
        String separator = "---------------";
        // input Cases
        if(input.startsWith("HELP")) { // Display help message
            // Output help string
            String helpText = separator + "\nALL COMMANDS:\n" 
                    + separator + "\n"
                    + "HELP = Display this message\n"
                    + "STATUS = Display game status\n"
                    + "SHOW USERS = Displays a list of all player usernames\n"
                    + "PLAYER {player} = View the status of a player\n"
                    + "ACCUSE {player} = (Night - Cop Only) Reveal a player's role\n"
                    + "MURDER {player} = (Night - Mafia Only) Murder a player\n"
                    + "VOTE {player} = (Day) Vote a player for lynching";
            this.serverObject.clients.get(clientIndex).pushOutput(helpText);
            this.serverObject.outputToLog(this.serverObject.clients.get(clientIndex).getUsername() + " asked for the help text");
        }
        else if(input.startsWith("PLAYER")) { // Get player status
            // Get target player's username (7 is where player name starts)
            if(input.length() >= 7) {
                String username = input.substring(7);
                // Track if we found a match
                boolean hit = false;

                // Go through player list to find status
                for(Participant player : this.serverObject.clients) {
                    if(player.getUsername().equals(username)) {
                        // Set hit to true
                        hit = true;
                        String status = separator + "\n" + username + "\n" + separator
                                + "\nStatus: ";
                        if(player.isAlive()) {
                            status += "Alive";
                        }
                        else {
                            status += "Dead\nRole: \n";
                        }

                        this.serverObject.clients.get(clientIndex).pushOutput(status);
                    }
                }

                // If we didn't get a match, tell the user
                if(!hit) {
                    this.serverObject.clients.get(clientIndex).pushOutput("No match for that username.");
                }
                
                this.serverObject.outputToLog(this.serverObject.clients.get(clientIndex).getUsername() + " inquired about " + username + "'s status");
                
            }
            else {
                this.serverObject.clients.get(clientIndex).pushOutput("Please enter a valid vote after the PLAYER command.");
                this.serverObject.outputToLog(this.serverObject.clients.get(clientIndex).getUsername() + " entered an invalid player command");
            }
        }
        else if(input.startsWith("MURDER")) { // Murder a player
            // Get target player's username (7 is where player name starts)
            if(input.length() >= 7) {
                String username = input.substring(7);
                // If player can vote
                if(this.serverObject.clients.get(clientIndex).canVote() && this.serverObject.clients.get(clientIndex).getRole().isMafia()) {
                    this.serverObject.mafiaVotes.addVote(this.serverObject.clients.get(clientIndex), username);
                    this.serverObject.outputToLog(this.serverObject.clients.get(clientIndex).getUsername() + " voted to murder " + username);
                }
                else {
                    this.serverObject.clients.get(clientIndex).pushOutput("You cannot currently do that.");
                    this.serverObject.outputToLog(this.serverObject.clients.get(clientIndex).getUsername() + " unsuccessfully attempted to vote to murder " + username);
                }
            }
            else {
                this.serverObject.clients.get(clientIndex).pushOutput("Please enter a valid vote after the MURDER command.");
                this.serverObject.outputToLog(this.serverObject.clients.get(clientIndex).getUsername() + " entered an invalid murder command");
            }
        }
        else if(input.startsWith("ACCUSE")) { // Accuse a player of being part of the mafia
            // Get target player's username (7 is where player name starts)
            if(input.length() >= 7) {
                String username = input.substring(7);

                // If player can vote
                if(this.serverObject.clients.get(clientIndex).canVote() && this.serverObject.clients.get(clientIndex).getRole().getName().equals("Sheriff")) {
                    // Run vote action
                    this.serverObject.sheriffVotes.addVote(this.serverObject.clients.get(clientIndex), username);
                    Participant accused = this.serverObject.lookupParticipantByUsername(username);

                    if(accused != null) {
                        if(accused.getRole() != null && accused.getRole().isMafia())
                        {
                            // mafia
                            this.serverObject.clients.get(clientIndex).pushOutput("(NARRATOR) " + accused.getUsername() + " is indeed a mafioso. Stay alert.");
                        } else {
                            // not mafia
                            this.serverObject.clients.get(clientIndex).pushOutput("(NARRATOR) " + accused.getUsername() + " is not involved with the mafia.");
                        }
                    }
                    this.serverObject.outputToLog(this.serverObject.clients.get(clientIndex).getUsername() + " accused " + username + " of being part of the mafia");

                }
                else {
                    this.serverObject.clients.get(clientIndex).pushOutput("You cannot currently do that.");
                    this.serverObject.outputToLog(this.serverObject.clients.get(clientIndex).getUsername() + " unsuccessfully tried to accuse " + username);
                }
            }
            else {
                this.serverObject.clients.get(clientIndex).pushOutput("Please enter a valid vote after the ACCUSE command.");
                this.serverObject.outputToLog(this.serverObject.clients.get(clientIndex).getUsername() + " entered an invalid accuse command");
            }
        }
        else if(input.startsWith("SHOW USERS")) { // Show current user list
            this.serverObject.clients.get(clientIndex).pushOutput(separator);
            this.serverObject.clients.get(clientIndex).pushOutput("CURRENTLY CONNECTED USERS:");
            this.serverObject.clients.get(clientIndex).pushOutput(separator);
            this.serverObject.clients.get(clientIndex).pushOutput(this.serverObject.getClientList());
            this.serverObject.outputToLog(this.serverObject.clients.get(clientIndex).getUsername() + " inquired about the current players");
        }
        else if(input.equals("STATUS")) { // Get game status
            // TODO
            this.serverObject.outputToLog(this.serverObject.clients.get(clientIndex).getUsername() + " inquired about the game status");
        }
        else if(input.startsWith("VOTE")) { // Vote a player for lynching
            // Get target player's username (5 is where player name starts)
            if(input.length() >= 5) {
                String username = input.substring(5);

                // If player can vote
                if(this.serverObject.clients.get(clientIndex).canVote()) {
                    // Run vote action
                    this.serverObject.publicVotes.addVote(this.serverObject.clients.get(clientIndex), username);
                    this.serverObject.outputToLog(this.serverObject.clients.get(clientIndex).getUsername() + " voted to lynch " + username);
                }
                else {
                    this.serverObject.clients.get(clientIndex).pushOutput("You cannot currently do that.");
                    this.serverObject.outputToLog(this.serverObject.clients.get(clientIndex).getUsername() + " unsuccessfully tried to lynch " + username);
                }
            }
            else {
                this.serverObject.clients.get(clientIndex).pushOutput("Please enter a valid vote after the VOTE command.");
                this.serverObject.outputToLog(this.serverObject.clients.get(clientIndex).getUsername() + " incorrectly voted");
            }
        }
        else { // Broadcast message if user is allowed to chat
            if(this.serverObject.clients.get(clientIndex).canTalk()) {
                ServerMessageBroadcaster broadcast = 
                        new ServerMessageBroadcaster(this.serverObject.clients.get(clientIndex), 
                                this.serverObject.clients, input);
                broadcast.start();
            }
            else {
                this.serverObject.clients.get(clientIndex).pushOutput("You are not currently allowed to chat.");
                this.serverObject.outputToLog(this.serverObject.clients.get(clientIndex).getUsername() + " tried to chat");
            }
        }
    }
}
