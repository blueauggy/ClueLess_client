package clueGame;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import javax.swing.JOptionPane;

public class A_Chat_Client implements Runnable {

	Socket SOCK;
	Scanner INPUT;
	Scanner SEND=new Scanner(System.in);
	PrintWriter OUT;
	
	public A_Chat_Client(Socket X){
		this.SOCK=X;
	}
	
	public void run(){
		try{
			try{
				INPUT=new Scanner (SOCK.getInputStream());
				OUT=new PrintWriter(SOCK.getOutputStream());
				OUT.flush();
				CheckStream();
				
			}finally{
				SOCK.close();
			}
			
			
		} catch (Exception X){
			System.out.println(X);
		}
		
		
	}
	
	public void DISCONNECT() throws Exception{
		OUT.println(LobbyClientGUI.UserName+ " has disconnected.");
		OUT.flush();
		SOCK.close();
		JOptionPane.showMessageDialog(null, "You Disconnected");
		System.exit(0);
	}
	
	public void CheckStream(){
		while(true){
			RECEIVE();
		}
	}
	
	public void RECEIVE(){
		if(INPUT.hasNext()){
			String MESSAGE=INPUT.nextLine();
			if(MESSAGE.contains("----")){
				String TEMP1=MESSAGE.substring(4);
				TEMP1=TEMP1.replace("[", "");
				TEMP1=TEMP1.replace("]", "");
				
				String[] CurrentUsers=TEMP1.split(", ");
				LobbyClientGUI.JL_ONLINE.setListData(CurrentUsers);
				
			} else {
			LobbyClientGUI.TA_CONVERSATION.append(MESSAGE + "\n");
			}
		}	
		
	}
	
	public void SEND(String x){
		OUT.println(LobbyClientGUI.UserName + ": "+x);
		OUT.flush();
		LobbyClientGUI.TF_Message.setText("");
	}
	
	
}
