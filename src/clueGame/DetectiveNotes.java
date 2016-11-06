package clueGame;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.ArrayList;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

/**
 * 
 * Creates the popup menu for Detective Notes.  None of the form fields actually do anything. Just used for player convienence.
 *
 */
@SuppressWarnings("serial")
public class DetectiveNotes
  extends JDialog
{
  private Board board;
  
  public DetectiveNotes()
  {
    this.board = Board.getInstance();
    createCheckBoxes();
    setSize(500, 550);
    setLayout(new GridLayout(3, 2));
  }
  
  private void createCheckBoxes()
  {
    add(createBoxes("People", this.board.getPeopleNames()));
    add(createCombo("Person Guess", this.board.getPeopleNames()));
    add(createBoxes("Rooms", this.board.getRoomNames()));
    add(createCombo("Room Guess", this.board.getRoomNames()));
    add(createBoxes("Weapons", this.board.getWeaponNames()));
    add(createCombo("Weapon Guess", this.board.getWeaponNames()));
  }
  
  private JPanel createBoxes(String labelString, ArrayList<String> items)
  {
    JPanel panel = new JPanel();
    panel.setLayout(new GridLayout(0, 2));
    panel.setPreferredSize(new Dimension(200, 50));
    panel.setBorder(new TitledBorder(new EtchedBorder(), labelString));
    for (String item : items)
    {
      JCheckBox field = new JCheckBox(item);
      panel.add(field);
    }
    return panel;
  }
  
  private JPanel createCombo(String labelString, ArrayList<String> items)
  {
    JPanel panel = new JPanel();
    panel.setLayout(new GridLayout(0, 2));
    panel.setPreferredSize(new Dimension(100, 50));
    panel.setBorder(new TitledBorder(new EtchedBorder(), labelString));
    JComboBox<String> combo = new JComboBox<String>();
    combo.addItem("Don't Know");
    for (String item : items) {
      combo.addItem(item);
    }
    panel.add(combo);
    return panel;
  }
}
