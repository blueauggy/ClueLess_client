package clueGame;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;

/**
 * 
 * Creates the popup box menu for making an guess when your player arrives at a room.
 *
 */
@SuppressWarnings("serial")
public class GuessDialog
  extends JDialog
{
  private JComboBox<String> person;
  private JComboBox<String> weapon;
  private JComboBox<String> room;
  private String roomStr;
  private JButton submit;
  private JButton cancel;
  private boolean submitted;
  private Guess guess;
  private Board board;
  
  public GuessDialog(String whichRoom)
  {
    setTitle("Make a Guess");
    setSize(300, 200);
    setLayout(new GridLayout(4, 2));
    setModal(true);
    
    this.board = Board.getInstance();
    this.room = new JComboBox<String>();
    this.person = new JComboBox<String>();
    this.weapon = new JComboBox<String>();
    if (whichRoom == null)
    {
      setupComboBox(this.room, this.board.getRoomNames(), "Room");
    }
    else
    {
      this.roomStr = whichRoom;
      JLabel roomLabel = new JLabel("Your room");
      add(roomLabel);
      JTextField roomField = new JTextField(this.roomStr);
      roomField.setEditable(false);
      add(roomField);
      
      this.room.addItem(whichRoom);
    }
    setupComboBox(this.person, this.board.getPeopleNames(), "Person");
    setupComboBox(this.weapon, this.board.getWeaponNames(), "Weapon");
    setupButtons();
  }
  
  private void setupComboBox(JComboBox<String> combo, ArrayList<String> items, String whichItem)
  {
    for (String p : items) {
      combo.addItem(p);
    }
    JLabel label = new JLabel(whichItem);
    add(label);
    add(combo);
  }
  
  private void setupButtons()
  {
    this.submit = new JButton("Submit");
    add(this.submit);
    this.cancel = new JButton("Cancel");
    add(this.cancel);
    
    ActionListener listener = new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        if (e.getSource() == GuessDialog.this.submit)
        {
          GuessDialog.this.submitted = true;
          GuessDialog.this.guess = new Guess();
          GuessDialog.this.guess.person = GuessDialog.this.person.getSelectedItem().toString();
          GuessDialog.this.guess.weapon = GuessDialog.this.weapon.getSelectedItem().toString();
          GuessDialog.this.guess.room = GuessDialog.this.room.getSelectedItem().toString();
        }
        else
        {
          GuessDialog.this.submitted = false;
        }
        GuessDialog.this.setVisible(false);
      }
    };
    this.submit.addActionListener(listener);
    this.cancel.addActionListener(listener);
  }
  
  public boolean isSubmitted()
  {
    return this.submitted;
  }
  
  public Guess getGuess()
  {
    return this.guess;
  }
  

}
