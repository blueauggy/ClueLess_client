import java.io.*;
import java.net.*;
import java.util.*;

public class Chat_Server_Return implements Runnable {

	Socket SOCK;
	private Scanner INPUT;
	private PrintWriter OUT;
	String MESSAGE="";
	
	public Chat_Server_Return(Socket X){
		
		this.SOCK=X;
	}
	
	public void CheckConnection() throws Exception{
		if(!SOCK.isConnected()){
			for (int i=1; i<=Lobby.ConnectionArray.size();i++){
				if(Lobby.ConnectionArray.get(i)==SOCK){
					Lobby.ConnectionArray.remove(i);
				}
			}
			
			for (int i=1; i<=Lobby.ConnectionArray.size();i++){
				Socket tmp_sock=(Socket) Lobby.ConnectionArray.get(i-1);
				PrintWriter tmp_out=new PrintWriter(tmp_sock.getOutputStream());
				tmp_out.println(tmp_sock.getLocalAddress().getHostName() + " is disconnected");
				tmp_out.flush();
				
				System.out.println(tmp_sock.getLocalAddress().getHostName() + " is disconnected");
				
			}
			
			
		}
		
		
	}
	
	
	public void run(){
		try{
			try{
				INPUT=new Scanner (SOCK.getInputStream());
				OUT=new PrintWriter(SOCK.getOutputStream());
				
				while(true){
					CheckConnection();
					
					if(!INPUT.hasNext()){
						return;
					}
					
					MESSAGE=INPUT.nextLine();
					System.out.println("CLIENT Said: " + MESSAGE);
					
					for(int i=1; i<=Lobby.ConnectionArray.size(); i++){
						Socket tmp_sock=(Socket) Lobby.ConnectionArray.get(i-1);
						PrintWriter tmp_out=new PrintWriter(tmp_sock.getOutputStream());
						tmp_out.println(MESSAGE);
						tmp_out.flush();
						System.out.println("Sent to: " + tmp_sock.getLocalAddress().getHostName());
					}
				} 
				
				
			} finally {
				SOCK.close();
			}
		} catch (Exception X){
			System.out.println(X);
		}
		
	}
	
	
	
}
