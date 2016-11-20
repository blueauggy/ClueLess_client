//C:\Users\Van\workspace\Client-Server\bin

import java.net.ServerSocket;
import java.util.*;
import java.io.PrintWriter;
import java.net.*;
import javax.swing.JOptionPane;

public class Lobby {

	public static ArrayList<Socket> ConnectionArray=new ArrayList<Socket>();
	public static ArrayList<String> CurrentUsers= new ArrayList<String>();
	
	public static void main (String[] args) throws Exception {
		
		try{
			final int PORT=555;
			ServerSocket SERVER=new ServerSocket(PORT);
			System.out.println("Waiting for clients...");
			
			while(true){
				Socket SOCK= SERVER.accept();
				ConnectionArray.add(SOCK);
				
				System.out.println("Client connected from: " + SOCK.getLocalAddress().getHostName());
				
				AddUserName(SOCK);
							
				Chat_Server_Return CHAT=new Chat_Server_Return(SOCK);
				Thread X =new Thread(CHAT);
				X.start();
			}
			
			
		} catch (Exception X) {
			System.out.println(X);
		}
				
	}
	
	
	public static void AddUserName(Socket X) throws Exception {
		Scanner INPUT= new Scanner(X.getInputStream());
		System.out.println("Enter Username: ");
		String UserName=INPUT.nextLine();
		CurrentUsers.add(UserName);
		
		for(int i=1; i<= Lobby.ConnectionArray.size();i++){
			Socket tmp_sock=(Socket) Lobby.ConnectionArray.get(i-1);
			PrintWriter OUT = new PrintWriter(tmp_sock.getOutputStream());
			OUT.println("----"+ CurrentUsers);
			OUT.flush();
		}
		
		
		System.out.println(UserName + " has joined the game");
	}
	
	
}
