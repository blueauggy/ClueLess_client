package clueGame;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

/**
 * 
 * Board cell is the class that displays the actual gird layout of the boards.  
 * Coloring of board pieces happens here.
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
    
    //BEN: Addition of IF statment
    if(this.initial != 'X' && this.initial != 'W')
    {
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
    if (this.highlighted) 
    {
    	if(isRoom())
    	{
    		g.setColor(new Color(120,220,120)); 
	      	g.fillRect(this.x, this.y, PIECE_SIZE, PIECE_SIZE);
    	}
    	else
    	{
	    	g.setColor(Color.DARK_GRAY); 
	      	g.fillRect(this.x, this.y, PIECE_SIZE, PIECE_SIZE);
	      	if((this.x % 200) == 0)
		  	{
			    	g.setColor(new Color(120,220,120));
			    	g.fillRect(this.x+25, this.y, PIECE_SIZE-50, PIECE_SIZE);
		  	}
		  	else
		  	{
			    	g.setColor(new Color(120,220,120));
			    	g.fillRect(this.x, this.y+25, PIECE_SIZE, PIECE_SIZE-50);
		  	}
    	}
    } 
    else if (isBlank()) 
    {
        g.setColor(Color.DARK_GRAY);
        g.fillRect(this.x, this.y, PIECE_SIZE, PIECE_SIZE);
    } 
    else if (isRoom())
    {
    	g.setColor(new Color(80,170,255));
    	g.fillRect(this.x, this.y, PIECE_SIZE, PIECE_SIZE);
    }
    else if(isWalkway())
    {
    	g.setColor(Color.DARK_GRAY);
    	g.fillRect(this.x, this.y, PIECE_SIZE, PIECE_SIZE);
    	if((this.x % 200) == 0)
    	{
	    	g.setColor(Color.YELLOW);
	    	g.fillRect(this.x+25, this.y, PIECE_SIZE-50, PIECE_SIZE);
    	}
    	else
    	{
	    	g.setColor(Color.YELLOW);
	    	g.fillRect(this.x, this.y+25, PIECE_SIZE, PIECE_SIZE-50);
    	}
    	
    }
    if ((isWalkway()) || (this.highlighted))
    {
      g.setColor(Color.DARK_GRAY);
      g.drawRect(this.x, this.y, PIECE_SIZE, PIECE_SIZE);
    }
    if (this.displayName && isRoom())
    {
      g.setColor(Color.black);
      g.setFont(new Font("Sans Serif", Font.BOLD, 12));
      g.drawString(Board.getInstance().getRoomName(this.initial).toUpperCase(), this.x+3, this.y+PIECE_SIZE/4);
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
