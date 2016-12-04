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
	
	public A_Chat_Client(Socket sock){
		this.SOCK=sock;
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
			
			
		} catch (Exception ex){
			System.out.println(ex);
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
			String message =INPUT.nextLine();
			//Status Messages
			if(message.startsWith("----"))
			{
				String status = message.substring(4);
				//System.out.println("System Message Recieved:");
				System.out.println(status);
				Board gameInst = Board.getInstance();
				if(status.startsWith("CurrentUsers:"))
				{
					String value = status.split(":")[1];
					value=value.replace("[", "");
					value=value.replace("]", "");
					String[] CurrentUsers=value.split(", ");
					LobbyClientGUI.JL_ONLINE.setListData(CurrentUsers);
				}
				else if(status.startsWith("Start_Board"))
				{
					String [] indiv = status.split(":")[1].split(";");
					for (String name_x_y : indiv)
					{
						String[] parts = name_x_y.split(",");
						//Player 
						String name = parts[0];
						int row = Integer.parseInt(parts[1]);
						int col = Integer.parseInt(parts[2]);
						String color = parts[3];
						Player p = new Player(name, row, col, color);
						gameInst.getPlayers().add(p);
						gameInst.addPeoplesNames(p.getName());
						
					}
				}
				else if(status.startsWith("Start_User"))
				{
					String name = status.split(":")[1];
					for (Player p : gameInst.getPlayers())
					{
						if (p.getName().equals(name))
						{
							System.out.println("The client is "+name);
							gameInst.setClient(p);
						}
					}
				}
				else if(status.startsWith("Start_Cards"))
				{
					String[] values = status.split(":")[1].split(";");
							
					for (String line : values)
					{
						String[] parts = line.split(",");
						Card clientCard = new Card(parts[0], Card.stringCardType(parts[1]));
						gameInst.addCardtoClient(clientCard);
					}
				}
				else if(status.startsWith("Game_Commence"))
				{
					ClueGame.setUpClueGame(gameInst.getClient().getName());

				}
				else if(status.startsWith("BoardState Turn:"))
				{
					System.out.println("Recieved Turn");
					int value = Integer.parseInt(status.split(":")[1]);
					gameInst.setWhoseTurn(value);
				}
				else if(status.startsWith("BoardState CP:"))
				{
					//TODO: Redo highlight cells; mark 1st players turn as done; cannot move to shared cell;
					System.out.println("Recieved Cur Player");
					String value = status.split(":")[1];
					for (Player p : gameInst.getPlayers())
					{
						if (p.getName().equals(value))
						{
							gameInst.setCurrentPlayer(p);
							gameInst.gameControl.showTurn(gameInst.getCurrentPlayer().getName());
							if (gameInst.getClient().equals(p))
							{
								System.out.println("Cur Player "+value+" is the client");
								System.out.println("Highlighting targets for client "+p.getName());
								gameInst.calcTargets(p.getRow(), p.getColumn());
								gameInst.highlightTargets(true);
							}
						}
					}
					
				}
				else if(status.startsWith("BoardState:"))
				{
					//Parse input
					recieveBoardState(status);
					
					//Update Board
					for (Player p : gameInst.getPlayers())
					{
						p.makeMove(gameInst, false);
						gameInst.repaint();
					}
				}

			} 
			else 
			{
				LobbyClientGUI.TA_CONVERSATION.append(message + "\n");
			}
		}	
		
	}
	
	public void SEND(String message){
		OUT.println(LobbyClientGUI.UserName + ": "+message);
		OUT.flush();
		LobbyClientGUI.TF_Message.setText("");
	}
	
	public void STATUS_SEND(String message){
		OUT.println("----"+message);
		OUT.flush();
		LobbyClientGUI.TF_Message.setText("");
	}
	
	private void recieveBoardState(String state)
	  {
		  Board board = Board.getInstance();
		  String [] positions = state.split(":")[1].split(";");
		  for (String pos : positions)
		  {
			  String[] parts = pos.split(",");
			  String name = parts[0];
			  int row = Integer.parseInt(parts[1]);
			  int col = Integer.parseInt(parts[2]);
			  for (Player p :board.getPlayers())
			  {
				  if(p.getName().equals(name))
				  {
					  p.setColumn(col);
					  p.setRow(row);
				  }
			  }
		  }
	  }
	
	
}
