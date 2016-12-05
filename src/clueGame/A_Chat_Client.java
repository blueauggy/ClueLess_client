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
	
	@SuppressWarnings("unchecked")
	public void RECEIVE(){
		if(INPUT.hasNext()){
			String message =INPUT.nextLine();
			//Status Messages
			if(message.startsWith("----"))
			{
				String status = message.substring(4);
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
						gameInst.setWhoseTurn(0);
						gameInst.setCurrentPlayer(gameInst.getPlayers().get(0));
						
					}
				}
				else if(status.startsWith("Start_User"))
				{
					String name = status.split(":")[1];
					for (Player p : gameInst.getPlayers())
					{
						if (p.getName().equals(name))
						{
							System.out.println("The client is [[["+name+"]]]");
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
					System.out.println("'''"+status+"'''");
					int value = Integer.parseInt(status.split(":")[1]);
					gameInst.setWhoseTurn(value);
					System.out.println("Recieved Turn: "+value);
				}
				else if(status.startsWith("BoardState CP:"))
				{
					//TODO: Redo highlight cells; mark 1st players turn as done; cannot move to shared cell;
					String value = status.split(":")[1];
					System.out.println("Recieved Cur Player: "+value);
					
					for (Player p : gameInst.getPlayers())
					{
						if (p.getName().equals(value))
						{
							gameInst.setCurrentPlayer(p);
							gameInst.gameControl.showTurn(gameInst.getCurrentPlayer().getName());
						}
					}
					
				}
				else if(status.startsWith("BoardState:"))
				{
					recieveBoardState(status);
					gameInst.repaint();
					for (Player p : gameInst.getPlayers())
					{
						if(gameInst.getClient().equals(p) && gameInst.getCurrentPlayer().equals(p))
						{
							System.out.println("Cur Player "+p.getName()+" is the client");
							System.out.println("Highlighting targets for client "+p.getName());
							gameInst.calcTargets(p.getRow(), p.getColumn());
							System.out.println("Highlighting from BoardState");
							p.makeMove(gameInst, true);
							gameInst.repaint();
						}
						else if (gameInst.getClient().equals(p) && !gameInst.getCurrentPlayer().equals(p))
						{
							System.out.println("Highlighting false from BoardState");
							gameInst.highlightTargets(false);
							gameInst.repaint();
						}
					}
				}
				else if(status.startsWith("Guess Result:"))
				{
					String value = status.split(":")[1];
					System.out.println("Recieved Guess Result: "+value);
					if(value.equals("NONE"))
					{
						//No guess result
						gameInst.gameControl.setGuessResult("No new clue");
					}
					else
					{
						//Format ----GuessResult:<Card name>
						System.out.println("Guess result is "+value);
						gameInst.gameControl.setGuessResult(value);
						
					}
				}
				else if(status.startsWith("Accusation Result:"))
				{
					String player = status.split(":")[1];
					String result = status.split(":")[2];
					System.out.println("Recieved Accusation Result: "+status);
					for (Player p : gameInst.getPlayers())
					{
						System.out.println("Client "+gameInst.getClient().getName());
						System.out.println("Acc Player "+player);
						System.out.println("Inc Player "+p.getName());
						if(gameInst.getClient().equals(p) && player.equals(p.getName()))
						{
							if (result.equals("WIN"))
							{
								JOptionPane.showMessageDialog(null, player+" wins!", "YOU WIN!", 1);
								System.exit(0);
							}
							else
							{
								JOptionPane.showMessageDialog(null, "Sorry, not correct. You have lost!", "GAME OVER!", 1);
								System.exit(0);
								p.setIsDead(true);
							}
						}
						else if (gameInst.getClient().equals(p) && !player.equals(p.getName()))
						{
							if (result.equals("WIN"))
							{
								JOptionPane.showMessageDialog(null, player+" wins!", "GAME OVER!", 1);
								System.exit(0);
							}
							else
							{
								JOptionPane.showMessageDialog(null, player+" has accused incorrectly and has lost!");
								//Check if you are last player alive
								for (Player p2: gameInst.getPlayers())
								{
									if (p2.getName().equals(player))
										p2.setIsDead(true);
								}
								if (gameInst.onePlayerleft())
								{
									JOptionPane.showMessageDialog(null, p.getName()+" is the only remaining player.  You win!", "YOU WIN!", 1);
									System.exit(0);
								}
							}
						}
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
