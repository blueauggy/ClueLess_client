package clueGame;

import java.awt.Color;
import java.awt.Graphics2D;

/**
 * 
 * Board cell is the class that displays the actual gird layout of the boards.  Coloring of board pieces happens here.
 * 
 */
public class BoardCell
{
  private int row;
  private int column;
  private boolean displayName;
  private char initial;
  public static int PIECE_SIZE = 100;
  protected int x;
  protected int y;
  protected boolean highlighted;
  
  BoardCell(int row, int col, String parameters)
  {
    this.row = row;
    this.column = col;
    this.initial = parameters.charAt(0);
    
    this.x = (col * PIECE_SIZE);
    this.y = (row * PIECE_SIZE);
    
    this.displayName = false;
    if ((parameters.length() == 2) && (parameters.charAt(1) == 'N')) {
        this.displayName = true;
    }
  }
  
  /**
   * CYAN - options for moving to rooms.
   * YELLOW - hallways
   * BLACK - blank space (not on the board)
   * LIGHT_GRAY - rooms
   */
  public void draw(Graphics2D g)
  {
    if (this.highlighted) {
      g.setColor(Color.CYAN);
    } else if (isWalkway()) {
      g.setColor(Color.YELLOW);
    } else if (isBlank()) {
        g.setColor(Color.BLACK);
    } else {
      g.setColor(Color.LIGHT_GRAY);
    }
    g.fillRect(this.x, this.y, PIECE_SIZE, PIECE_SIZE);
    if ((isWalkway()) || (this.highlighted))
    {
      g.setColor(Color.black);
      g.drawRect(this.x, this.y, PIECE_SIZE, PIECE_SIZE);
    }
    if (this.displayName)
    {
      g.setColor(Color.BLUE);
      g.drawString(Board.getInstance().getRoomName(this.initial).toUpperCase(), this.x, this.y);
    }
  }
  
  public void setHighlight(boolean status)
  {
    this.highlighted = status;
  }
  
  public boolean isWalkway()
  {
    return this.initial == 'W';
  }
  
  public boolean isBlank()
  {
    return this.initial == 'X';
  }
  
  public boolean isRoom()
  {
    return (this.initial != 'W') && (this.initial != 'X');
  }
  
  public int getRow()
  {
    return this.row;
  }
  
  public int getCol()
  {
    return this.column;
  }
  
  public char getInitial()
  {
    return this.initial;
  }
  
}
