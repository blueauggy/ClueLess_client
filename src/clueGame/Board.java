package clueGame;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * 
 * The majority of the logic of what I have made so far.
 *
 */
@SuppressWarnings("serial")
public class Board
  extends JPanel
  implements MouseListener
{

  public String boardConfigFile;
  public String roomConfigFile;
  public static final String weaponConfigFile = "ClueWeapons.txt";
  private int numRows;
  private int numColumns;
  public static final int MAX_BOARD_SIZE = 50;
  private BoardCell[][] board = new BoardCell[50][50];
  private Map<Character, String> legend = new HashMap<Character, String>();
  private Set<BoardCell> targets = null;
  private ArrayList<String> peopleNames;
  private ArrayList<String> weaponNames;
  private ArrayList<String> roomNames;
  private ArrayList<Player> players = new ArrayList<Player>();
  private Player client;
  private ArrayList<Card> cards = new ArrayList<Card>();
  private GameControlPanel gameControl;
  private Player currentPlayer;
  private int whoseTurn;
  private static Board theInstance = new Board();
  
  //TODO: Remove final answer
  private Guess finalAnswer;
  
 /**
  * 
  * Returns the instance of the board
  */
  public static Board getInstance()
  {
    return theInstance;
  }
  
  /**
   * Pulls in config files based on external config files listed above
   */
  public void setConfigFiles(String boardConfig, String roomConfig)
  {
    this.boardConfigFile = boardConfig;
    this.roomConfigFile = roomConfig;
  }
  
  /**
   * Starts the game, finds your possible neighbors for moves and deals cards.
   */
  public void initialize()
  {	  
    loadConfigFiles();
    addMouseListener(this);
    selectAnswer();
    deal();
    this.whoseTurn = (this.players.size() - 1);
  }
  
  /**
   * Load room, board, people, weapon configs and errors if there is a failure to load.
   */
  public void loadConfigFiles()
  {
    try
    {
      loadRoomConfig();
      
      loadBoardConfig();
      
      loadPeopleConfig();
      loadWeaponConfig();
    }
    catch (Exception e)
    {
      System.out.println(e.getMessage());
    }
  }
  
  /**
   * Error checking with board size. Loads board config.
   */
  public void loadBoardConfig() throws FileNotFoundException
  {
    InputStream is = getClass().getResourceAsStream("/data/" + this.boardConfigFile);
    
    Scanner boardConfig = new Scanner(is);
    int rowCount = 0;
    while (boardConfig.hasNextLine())
    {
      String line = boardConfig.nextLine();
      String[] tokens = line.split(",");
      if (rowCount == 0)
      {
        this.numColumns = tokens.length;
      }
      else if (this.numColumns != tokens.length)
      {
        boardConfig.close();
        System.err.println("Rows do not all have the same number of columns");
      }
      for (int col = 0; col < this.numColumns; col++)
      {
        Character key = Character.valueOf(tokens[col].charAt(0));
        String room = (String)this.legend.get(key);
        if (room == null)
        {
          boardConfig.close();
          System.err.println("Room not defined " + tokens[col]);
        }
        this.board[rowCount][col] = new BoardCell(rowCount, col, tokens[col].toUpperCase());
      }
      rowCount++;
    }
    this.numRows = rowCount;
    boardConfig.close();
  }
  
  /**
   * Error checking with people list. Loads people config.
   * This section needs an overhaul when we move to multiperson mode.
   */
  public void loadPeopleConfig() throws FileNotFoundException
  {
	//TODO: Remove this once server handles cards.
    this.peopleNames = new ArrayList<String>();
    InputStream is = getClass().getResourceAsStream("/data/CluePeople.txt");
    Scanner peopleConfig = new Scanner(is);
    while (peopleConfig.hasNextLine())
    {
      String line = peopleConfig.nextLine();
      Player player;
      if (this.players.size() == 0)
      {
        this.client = new Player();
        player = this.client;
      }
      else
      {
        player = new Player();
      }
      player.updateAttributes(line);
      
      this.cards.add(new Card(player.getName(), Card.CardType.PERSON));
      this.peopleNames.add(player.getName());
      
      this.players.add(player);
    }
    this.whoseTurn = this.players.size();
    peopleConfig.close();
    
    
    
  }
  
  /**
   * 
   * 
   */
  public void loadRoomConfig() throws FileNotFoundException
  {
    InputStream is = getClass().getResourceAsStream("/data/" + this.roomConfigFile);
    Scanner roomConfig = new Scanner(is);
    this.roomNames = new ArrayList<String>();
    while (roomConfig.hasNextLine())
    {
      String line = roomConfig.nextLine();
      String[] tokens = line.split(",");
      if (tokens.length != 3)
      {
        roomConfig.close();
        System.err.println("Room file format incorrect " + line);
      }
      Character key = new Character(tokens[0].charAt(0));
      String roomName = tokens[1].trim();
      this.legend.put(key, roomName);
      
      String roomType = tokens[2].trim();
      if ((!roomType.equals("Card")) && (!roomType.equals("Other")))
      {
        roomConfig.close();
        System.err.println("Room file format incorrect " + line);
      }
      if (roomType.equals("Card"))
      {
        this.cards.add(new Card(tokens[1].trim(), Card.CardType.ROOM));
        this.roomNames.add(roomName);
      }
    }
    roomConfig.close();
  }
  
  /**
   * Basic error checking on weapon config; loads weapons. 
   * @throws FileNotFoundException
   * TODO: Remove once server has this
   */
  public void loadWeaponConfig()
    throws FileNotFoundException
  {
    this.weaponNames = new ArrayList<String>();
    
    InputStream is = getClass().getResourceAsStream("/data/ClueWeapons.txt");
    Scanner weaponsConfig = new Scanner(is);
    while (weaponsConfig.hasNextLine())
    {
      String line = weaponsConfig.nextLine();
      this.cards.add(new Card(line.trim(), Card.CardType.WEAPON));
      this.weaponNames.add(line.trim());
    }
    weaponsConfig.close();
  }
  
  public Set<BoardCell> calcAdjacencies(int row, int col)
  {
	  Set<BoardCell> neighbors = new HashSet<BoardCell>();
	  BoardCell cell = this.board[row][col];
	  if(cell.isBlank())
		  return null;
	  checkNeighbor(row - 1, col, neighbors);
      checkNeighbor(row + 1, col, neighbors);
      checkNeighbor(row, col - 1, neighbors);
      checkNeighbor(row, col + 1, neighbors);
      System.out.println(cell.getInitial());
	  if(cell.getInitial() == 'K')
	  {
		  checkNeighbor(0,0, neighbors);
	  }
	  else if (cell.getInitial() == 'S')
	  {
		  checkNeighbor(4,4, neighbors);
	  }
	  else if (cell.getInitial() == 'O')
	  {
		  checkNeighbor(4,0, neighbors);
	  }
	  else if (cell.getInitial() == 'C')
	  {
		  checkNeighbor(0,4, neighbors);
	  }
	  //BEN: added logic to deal with allowing a player to stay in a room if moved by another player's guess
	  if (currentPlayer.getForceMove())
	  {
		  neighbors.add(cell);
		  currentPlayer.setForceMove(false);
	  }
	  return neighbors;
	}

  private void checkNeighbor(int row, int col, Set<BoardCell> neighbors)
  {
	  
    if ((row < 0) || (col < 0) || (row >= this.numRows) || (col >= this.numColumns)) {
      return;
    }
    System.out.println("Checking "+row+","+col);
    BoardCell cell = this.board[row][col];
    
    //Check to see if player is already on the cell
    for (Player p: players)
    {
    	//BEN: added third && conditional to account for dead Players, allowing alive Players to not be obstructed by the dead ones.
    	if(p.getColumn() == col && p.getRow() == row && (p.getIsDead()==false))
    	{
    		return;
    	}
    }
    if (cell.isBlank())
    {
		return;
    }
    else  //Walkway or Room
    {
    	neighbors.add(cell);
    }
  }
  
  public void paintComponent(Graphics g)
  {
    super.paintComponent(g);
    Graphics2D g2 = (Graphics2D)g;
    drawSquares(g2);
    drawPlayers(g2);
  }
  
  public void drawSquares(Graphics2D g)
  {
    for (int row = 0; row < this.numRows; row++) {
      for (int col = 0; col < this.numColumns; col++) {
        this.board[row][col].draw(g);
      }
    }
  }
  
  public void drawPlayers(Graphics2D g)
  {
    for (Player p1 : this.players) {
      p1.drawPlayer(g, this);
    }
    checkForCollisions(g);
  }
  
  /**
   * This class is called after drawPlayers and sees if there is two players on a board piece.
   * It redraws the square and the player icons to incorporate two spots instead of one.
   */
  public void checkForCollisions(Graphics2D g)
  {
    for (Player p1 : this.players) {
      for (Player p2: this.players) {
    	  if (( p1.getName() != p2.getName()) && 
    		  (p1.getColumn() == p2.getColumn()) && 
    		  ( p1.getRow() == p2.getRow())) {
    		  //redraw square on collision
    		  this.board[(int)p1.getRow()][(int)p1.getColumn()].draw(g);
    		  p1.redrawCollision(g, this, p2);
    		  //return;
    	  }
	  for (Player p3 : this.players)
    	  {
    		  if (( p1.getName() != p2.getName()) && (p1.getName() != p3.getName()) && (p2.getName() != p3.getName())
    				  && (p1.getColumn() == p2.getColumn()) && ( p1.getRow() == p2.getRow())
    			  	&& (p1.getColumn() == p3.getColumn()) && (p1.getRow() == p3.getRow()))
    		  {
    			  this.board[(int)p1.getRow()][(int)p1.getColumn()].draw(g);
    			  p1.redrawCollision(g, this, p2, p3);
    		  }
    	  }
      }
    }
    return;
  }
  
  /**
   * Highlight target cells blue.
   */
  public void highlightTargets(boolean highlighted)
  {
    if (this.targets != null) {
      for (BoardCell cell : this.targets) {
        cell.setHighlight(highlighted);
      }
    }
  }
  

  
  /**
   * TODO: Fix back to just this.client after testing is done
   */
  public void mouseClicked(MouseEvent e)
  {
    //if (!this.client.mustFinish()) {
  //    return;
   // }
	if (!this.currentPlayer.mustFinish()){
	  return;
	}
    BoardCell clicked = findClickedCell(e.getX(), e.getY());
    if (clicked == null)
    {
      JOptionPane.showMessageDialog(null, "That is not a target");
    }
    else
    {
      //this.client.finishTurn(clicked);
      this.currentPlayer.finishTurn(clicked);
      highlightTargets(false);
      repaint();
      if (clicked.isRoom())
      {
        String roomName = getRoomName(clicked.getInitial());
        GuessDialog dialog = new GuessDialog(roomName);
        dialog.setVisible(true);
        if (dialog.isSubmitted()) {
          //handleGuess(dialog.getGuess(), this.client, clicked);
        	handleGuess(dialog.getGuess(), this.currentPlayer, clicked);
        }
      }
    }
  }
  
  public BoardCell findClickedCell(int mouseX, int mouseY)
  {
    if (this.targets != null)
    {
      int col = mouseX / BoardCell.PIECE_SIZE;
      int row = mouseY / BoardCell.PIECE_SIZE;
      BoardCell clicked = this.board[row][col];
      if (this.targets.contains(clicked)) {
        return clicked;
      }
    }
    return null;
  }
  
  public void mouseReleased(MouseEvent e) {}
  
  public void mouseEntered(MouseEvent e) {}
  
  public void mousePressed(MouseEvent e) {}
  
  public void mouseExited(MouseEvent e) {}
  
  public void movePlayer(String playerName, BoardCell location)
  {
    for (Player p : this.players) {
      if (p.getName().equals(playerName))
      {
        p.setLocation(location);
	//BEN: set flag for indicating the player was forcibly moved due to another player's guess
        p.setForceMove(true);
        break;
      }
    }
    repaint();
  }
  
  public void calcTargets(int row, int col)
  {    
    this.targets = new HashSet<BoardCell>();
    Set<BoardCell> targets = calcAdjacencies(row, col);
    for (BoardCell t : targets) {
            this.targets.add(t);
      }
  }
  
  /**
   * Make an accusation for your turn.
   */
  public boolean makeAccusation()
  {
    boolean result = false;
    if (!this.client.mustFinish())
    {
      JOptionPane.showMessageDialog(null, "It is not your turn!");
      return result;
    }
    GuessDialog dialog = new GuessDialog(null);
    dialog.setVisible(true);
    if (dialog.isSubmitted())
    {
      Guess guess = dialog.getGuess();
      result = checkAccusation(guess);
      if (result)
      {
        JOptionPane.showMessageDialog(null, "You win!");
        
        System.exit(0);
      }
      else
      {
        JOptionPane.showMessageDialog(null, "Sorry, not correct!");
	//BEN: set isDead indicator for player with incorrect accusation 
        this.client.setIsDead(true);
	//BEN: calls this method to de-highlight the incorrect accusers possible moves before the accusation
        highlightTargets(false);
      }
      this.client.finished();
    }
    return result;
  }
  
  /**
   * Next player button push triggers this action which cycles to next player.
   */
  public void nextPlayer()
  {
    if (this.client.mustFinish())
    {
      JOptionPane.showMessageDialog(null, "You need to finish your turn");
      
      return;
    }
    this.whoseTurn = ((this.whoseTurn + 1) % this.players.size());
    this.currentPlayer = ((Player)this.players.get(this.whoseTurn));
    
    //BEN: Skips dead player (incorrect accusation) 
    if(currentPlayer.getIsDead())
    {
    	 this.whoseTurn = ((this.whoseTurn + 1) % this.players.size());
    	 this.currentPlayer = ((Player)this.players.get(this.whoseTurn));
    }
    
    this.gameControl.showTurn(this.currentPlayer.getName());
    calcTargets(this.currentPlayer.getRow(), this.currentPlayer.getColumn());
    
    this.currentPlayer.makeMove(this);
    
    repaint();
  }
  
  /**
   * 
   */
  public boolean checkAccusation(Guess guess)
  {
	//TODO: Add server code
	//Send to server to check if guess is the correct answer; Set to false for now;
	  return false;

  }
  
  public Card handleGuess(Guess guess, Player accusingPlayer, BoardCell clicked)
  {
	  
    this.gameControl.setGuess(guess.person + " - " + guess.room + " - " + 
      guess.weapon);
    
    movePlayer(guess.person, clicked);
    
    int playersAsked = 0;
    
    int whichPlayer = this.players.indexOf(accusingPlayer);
    while (playersAsked < this.players.size())
    {
      whichPlayer = (whichPlayer + 1) % this.players.size();
      Player player = (Player)this.players.get(whichPlayer);
      if (player != accusingPlayer)
      {
    	  System.out.println(player.getName());
    	  //TODO: This information should be coming from the server when a player asks for cards back.
    	  Card tmp = new Card("Wrench", Card.CardType.WEAPON);
    	  this.gameControl.setGuessResult(tmp.getCardName());
    	  return tmp;
       /* Card result = player.disproveSuggestion(guess);
        if (result != null)
        {
          for (Player p : this.players) {
            p.updateSeen(result);
          }
          this.gameControl.setGuessResult(result.getCardName());
          
          return result;
        }*/
      }
      playersAsked++;
    }
    this.gameControl.setGuessResult("No new clue");
    
	System.out.println("Player guessed ["+guess.person+"] in the ["+guess.room+"] with the ["+guess.weapon+"]");
    return null;
  }
  
/**
 * Choosing the final solution.  
 * TODO: This will be done by the server but we will hardcode something in here for now.
 */
  public void selectAnswer()
  {
	//Final answer is held by the server; Made up one for now
    this.finalAnswer = new Guess();
    this.finalAnswer.person = "Miss Scarlet";
    this.finalAnswer.weapon = "Wrench";
    this.finalAnswer.room = "Library";
  }
  
  /**
   * Deal initial cards.
   */
  public void deal()
  {
	//Cards are sent from server; picked 3 randoms
	  
    Card first = new Card("Wrench", Card.CardType.WEAPON);
    Card second = new Card("Ballroom", Card.CardType.ROOM);
    Card third = new Card("Revolver", Card.CardType.WEAPON);
    
    client.addCard(first);
    client.addCard(second);
    client.addCard(third);
  }
  
  
  /**
   * Returns string for name of room from input initial.  Defined by data/CR_ClueLegend.
   */
  public String getRoomName(char initial)
  {
    return (String)this.legend.get(Character.valueOf(initial));
  }
  
  /**
   * Returns list of the rooms names
   */
  public ArrayList<String> getRoomNames()
  {
    return this.roomNames;
  }
  
  public ArrayList<Player> getPlayers()
  {
	  return this.players;
  }
  
  /**
   * Returns list of the weapons names
   */
  public ArrayList<String> getWeaponNames()
  {
    return this.weaponNames;
  }
  
  /**
   * Returns list of the people names
   * TODO: Remove when server is up
   */
  public ArrayList<String> getPeopleNames()
  {
    return this.peopleNames;
  }
  
  /**
   * Returns cards currently given to the client
   */
  public ArrayList<Card> getClientCards()
  {
    return this.client.getMyCards();
  }
  
  /**
   * Return gameControl handle
   */
  public void setGameControl(GameControlPanel gameControl)
  {
    this.gameControl = gameControl;
  }
  
  /**
   * Returns list of possible targets to move to
   */
  public Set<BoardCell> getTargets()
  {
    return this.targets;
  }
  
  public Player getCurrentPlayer()
  {
    return this.currentPlayer;
  }

}
