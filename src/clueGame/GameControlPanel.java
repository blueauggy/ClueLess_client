package clueGame;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

/**
 * 
 * The game control mechanics, such as next player and reactions to guesses.  Needs some work
 * 
 */
@SuppressWarnings("serial")
public class GameControlPanel
  extends JPanel
{
  private JButton accuse;
  private JButton nextPlayer;
  private JTextField theGuess;
  private JTextField guessResult;
  private JTextField turnDisplay;
  
  public GameControlPanel()
  {
    setLayout(new GridLayout(2, 0));
    createButtons();
    createStatus();
  }
  
  private class ButtonListener
    implements ActionListener
  {
    private ButtonListener() {}
    
    public void actionPerformed(ActionEvent e)
    {
      if (e.getSource() == GameControlPanel.this.nextPlayer) {
        GameControlPanel.this.nextPlayer();
      } else if (e.getSource() == GameControlPanel.this.accuse) {
        Board.getInstance().makeAccusation();
      }
    }
  }
  
  public void nextPlayer()
  {	
    this.theGuess.setText("");
    this.guessResult.setText("");
    
    Board.getInstance().nextPlayer();
  }
  
  public void showTurn(String playerName)
  {
    this.turnDisplay.setText(playerName);
  }
  
  public String displayTurn()
  {
    return this.turnDisplay.getText();
  }
  
  public void setGuess(String guess)
  {
    this.theGuess.setText(guess);
  }
  
  public void setGuessResult(String result)
  {
    this.guessResult.setText(result);
  }
  
  private void createStatus()
  {
    JPanel panel = new JPanel();
    panel.add(createGuessStatus());
    panel.add(createGuessResult());
    add(panel);
  }
  
  
  private JPanel createGuessStatus()
  {
    JLabel label = new JLabel("Guess");
    this.theGuess = new JTextField(30);
    this.theGuess.setEditable(false);
    JPanel panel = new JPanel();
    panel.setLayout(new GridLayout(2, 0));
    panel.add(label);
    panel.add(this.theGuess);
    panel.setBorder(new TitledBorder(new EtchedBorder(), "Guess"));
    return panel;
  }
  
  private JPanel createGuessResult()
  {
    JLabel label = new JLabel("Response");
    this.guessResult = new JTextField(10);
    this.guessResult.setEditable(false);
    JPanel panel = new JPanel();
    panel.setLayout(new GridLayout(1, 2));
    panel.add(label);
    panel.add(this.guessResult);
    panel.setBorder(new TitledBorder(new EtchedBorder(), "Guess Result"));
    return panel;
  }
  
  private void createButtons()
  {
    JPanel panel = new JPanel();
    panel.setLayout(new GridLayout(1, 0));
    ButtonListener listener = new ButtonListener();
    panel.add(createTurnDisplay());
    
    this.nextPlayer = new JButton("Next player");
    this.nextPlayer.addActionListener(listener);
    panel.add(this.nextPlayer);
    
    this.accuse = new JButton("Make an accusation");
    this.accuse.addActionListener(listener);
    panel.add(this.accuse);
    add(panel);
  }
  
  private JPanel createTurnDisplay()
  {
    JPanel panel = new JPanel();
    JLabel label = new JLabel("Whose turn?");
    
    this.turnDisplay = new JTextField(15);
    this.turnDisplay.setEditable(false);
    panel.add(label);
    panel.add(this.turnDisplay);
    return panel;
  }
  
  public static void main(String[] args)
  {
    GameControlPanel panel = new GameControlPanel();
    JFrame frame = new JFrame();
    frame.setContentPane(panel);
    frame.setSize(750, 200);
    frame.setDefaultCloseOperation(3);
    frame.setVisible(true);
  }
}
