package clueGame;

import java.awt.Color;
import java.awt.Graphics2D;
import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * Common functions between the human player and the computer player.
 * In the final version this would be between guest and host players.
 */
public class Player
{
  private String playerName;
  private int row;
  private int column;
  private Color color;
  private ArrayList<Card> myCards = new ArrayList<Card>();
  protected ArrayList<String> seenCards = new ArrayList<String>();
  private boolean needToFinish = false;
  
  //BEN: Boolean added to indicate if Player object is dead from a false accusation
  private boolean isDead = false;
  
  //BEN: Boolean variable added to indicate if a Player object was forcibly moved by another Player's guess
  private boolean forceMove = false;
  
  public Player() {}
  
  public Player(String name, int row, int column, String color)
  {
    this.playerName = name;
    this.row = row;
    this.column = column;
    this.color = new Color(Integer.parseInt(color));
  }
  
  public void updateAttributes(String line)
  {
    String[] tokens = line.split(",");
    this.color = convertColor(tokens[3]);
    if (this.color == null) {
    	System.err.println("Can't convert color " + tokens[3]);
    }
    this.playerName = tokens[0];
    this.row = Integer.parseInt(tokens[1].trim());
    this.column = Integer.parseInt(tokens[2].trim());
  }
  
  public Color convertColor(String strColor)
  {
    Color color;
    try
    {
      Field field = Class.forName("java.awt.Color").getField(strColor.trim());
      color = (Color)field.get(null);
    }
    catch (Exception e)
    {
      color = null;
    }
    return color;
  }
  
  public void addCard(Card card)
  {
    this.myCards.add(card);
    this.seenCards.add(card.getCardName());
  }
  
  public void updateSeen(Card seenCard)
  {
    this.seenCards.add(seenCard.getCardName());
  }
    
  /**
   * This is the code that draws the small circle and plays it on a board piece. 
   * We will want better graphics than this.
   */
  public void drawPlayer(Graphics2D g, Board board)
  {
    int size = BoardCell.PIECE_SIZE;
    int x = this.column * size;
    int y = this.row * size;
   
    g.setColor(this.color);
    g.fillOval(x + (BoardCell.PIECE_SIZE/4), y + (BoardCell.PIECE_SIZE/4), size/2, size/2);
    g.setColor(Color.black);
    g.drawOval(x + (BoardCell.PIECE_SIZE/4), y + (BoardCell.PIECE_SIZE/4), size/2, size/2);
    
    //BEN: Added these lines to display the player name under the icon
    
    int x_grid = x+2;
    int y_grid = y+ (BoardCell.PIECE_SIZE-13);
    g.setColor(Color.BLACK);
    g.drawString(this.getName(), x_grid+1, y_grid+1);
    g.drawString(this.getName(), x_grid+1, y_grid-1);
    g.drawString(this.getName(), x_grid-1, y_grid+1);
    g.drawString(this.getName(), x_grid-1, y_grid-1);
    g.setColor(Color.ORANGE);
    g.drawString(this.getName(), x + 2, y + (BoardCell.PIECE_SIZE-13));

  }
  
  /**
   * Draws two players on a given square instead of one.
   */
  public void redrawCollision(Graphics2D g, Board board, Player p2)
  {
    int size = BoardCell.PIECE_SIZE;
    int x = this.column * size;
    int y = this.row * size;
    
    //P1
    g.setColor(this.color);
    g.fillOval(x, y + (BoardCell.PIECE_SIZE/4), size/2, size/2);
    g.setColor(Color.BLACK);
    g.drawOval(x, y + (BoardCell.PIECE_SIZE/4), size/2, size/2);
    g.setColor(Color.ORANGE);
    g.drawString(this.getName(), x + 2, y + (BoardCell.PIECE_SIZE-13));
    
    //P2
    g.setColor(p2.getColor());
    g.fillOval(x+(BoardCell.PIECE_SIZE/2), y+ (BoardCell.PIECE_SIZE/4), size/2, size/2);
    g.setColor(Color.BLACK);
    g.drawOval(x + (BoardCell.PIECE_SIZE/2), y + (BoardCell.PIECE_SIZE/4), size/2, size/2);
    g.setColor(Color.ORANGE);
    g.drawString(p2.getName(), x + 2, y + (BoardCell.PIECE_SIZE-2));
  }
  
  public void makeMove(Board board, Boolean highlight)
  {
    this.needToFinish = true;
    System.out.println("Highlighted from Player.makemove");
    board.highlightTargets(highlight);

  }
  
  public void finishTurn(BoardCell clicked)
  {
    this.needToFinish = false;
    
    setRow(clicked.getRow());
    setColumn(clicked.getCol());
  }
  
  public boolean mustFinish()
  {
    return this.needToFinish;
  }
  
  public void finished()
  {
    this.needToFinish = false;
  }
    
  public void setLocation(BoardCell location)
  {
    this.row = location.getRow();
    this.column = location.getCol();
  }
  
  public void setName(String name)
  {
    this.playerName = name;
  }
  
  public void setColor(Color color)
  {
    this.color = color;
  }
  
  public void setRow(int row)
  {
    this.row = row;
  }
  
  public void setColumn(int column)
  {
    this.column = column;
  }
  
  public Color getColor()
  {
    return this.color;
  }
  
  public int getRow()
  {
    return this.row;
  }
  
  public int getColumn()
  {
    return this.column;
  }
  
  public String getName()
  {
    return this.playerName;
  }
  
  public ArrayList<Card> getMyCards()
  {
    return this.myCards;
  }
  
  //BEN: Getter/Setter code to get/set  if a player is dead or not
  public boolean getIsDead()
  {
	  return isDead;
  }
  
  public void setIsDead(boolean isDead)
  {
	  this.isDead = isDead;
  }

  //BEN: getter and setter methods for the forceMove variable
  public boolean getForceMove()
  {
	  return forceMove;
  }
  
  public void setForceMove(boolean forceMove)
  {
	  this.forceMove = forceMove;
  }
  
}
