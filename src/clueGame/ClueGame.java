package clueGame;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

/**
 * 
 * Creates overall GUI with buttons and graphical layout for card selection.
 * Loads board config file from boardConfigFile
 * Loads room config legend from roomConfigFile
 * 
 */
@SuppressWarnings("serial")
public class ClueGame
  extends JFrame
{
  private DetectiveNotes notes;
  private Board board;
  private GameControlPanel gameControl;
  public String boardConfigFile = "CR_ClueLayout.csv";
  public String roomConfigFile = "CR_ClueLegend.txt";
  private int GUIx = 620;
  private int GUIy = 750;
  
  public ClueGame(String client)
  {
    setUp(client);
  }
  
  public void setUp(String client)
  {
    setTitle("Clue Game - "+client);
    this.board = Board.getInstance();
    this.board.setConfigFiles(this.boardConfigFile, this.roomConfigFile);
    try
    {
      this.board.initialize();
    }
    catch (Exception e)
    {
      System.out.println("error!");
    }
    setupGUI();
  }
  
  public static void setUpClueGame(String client)
  {
	  ClueGame frame = new ClueGame(client);
	  frame.setVisible(true);
	  JOptionPane.showMessageDialog(frame, "You are "+client, "Welcome to Clue", 1);
  }
  
  /**
   * Creates the arrangement for where each box is located on the overal larger GUI.
   */
  public void setupGUI()
  {
    setDefaultCloseOperation(3);
    add(this.board, "Center");
    PlayerDisplay pd = new PlayerDisplay(this.board.getClientCards());
    add(pd, "East");
    this.gameControl = new GameControlPanel();
    this.board.setGameControl(this.gameControl);
    add(this.gameControl, "South");
    initMenus();
    this.notes = new DetectiveNotes();
    setSize(GUIx, GUIy);
  }
  
  private void initMenus()
  {
    JMenuBar menuBar = new JMenuBar();
    setJMenuBar(menuBar);
    menuBar.add(createFileMenu());
  }
  
  private JMenu createFileMenu()
  {
    JMenu menu = new JMenu("File");
    menu.add(createDetectiveNotes());
    menu.add(createFileExitItem());
    return menu;
  }
  
  private JMenuItem createFileExitItem()
  {
    JMenuItem item = new JMenuItem("Exit");
    
    item.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        System.exit(0);
      }
    });
    return item;
  }
  
  private JMenuItem createDetectiveNotes()
  {
    JMenuItem item = new JMenuItem("Show Detective Notes");
    
    item.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        ClueGame.this.notes.setVisible(true);
      }
    });
    return item;
  }
  
  public Board getBoard()
  {
    return this.board;
  }
  
  public static void main(String[] args)
  {
	new LobbyClientGUI("localhost", 5555);
  }
}
