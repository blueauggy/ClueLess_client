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
  
  public Player() {}
  
  public Player(String name, int row, int column, Color color)
  {
    this.playerName = name;
    this.row = row;
    this.column = column;
    this.color = color;
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

  }
  
  /**
   * Draws two players on a given square instead of one.
   */
  public void redrawCollision(Graphics2D g, Board board, Color p2Color)
  {
    int size = BoardCell.PIECE_SIZE;
    int x = this.column * size;
    int y = this.row * size;
    
    //P1
    g.setColor(this.color);
    g.fillOval(x, y + (BoardCell.PIECE_SIZE/4), size/2, size/2);
    g.setColor(Color.black);
    g.drawOval(x, y + (BoardCell.PIECE_SIZE/4), size/2, size/2);
    
    //P2
    g.setColor(p2Color);
    g.fillOval(x+(BoardCell.PIECE_SIZE/2), y+ (BoardCell.PIECE_SIZE/4), size/2, size/2);
    g.setColor(Color.black);
    g.drawOval(x + (BoardCell.PIECE_SIZE/2), y + (BoardCell.PIECE_SIZE/4), size/2, size/2);	 
  }
  
  public void makeMove(Board board)
  {
    this.needToFinish = true;
    
    board.highlightTargets(true);
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
}
