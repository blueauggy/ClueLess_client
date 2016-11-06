package clueGame;

import java.awt.GridLayout;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

/**
 * 
 * Creates the display that is passed to setupGUI() which places the PlayerDisplay in the right portion of the GUI.
 *
 */
@SuppressWarnings("serial")
public class PlayerDisplay
  extends JPanel
{
  public PlayerDisplay(ArrayList<Card> cards)
  {
    setLayout(new GridLayout(4, 1));
    TitledBorder title = BorderFactory.createTitledBorder(
      BorderFactory.createEtchedBorder(0), "My Cards");
    title.setTitleJustification(2);
    setBorder(title);
    createDisplay("People", Card.CardType.PERSON, cards);
    createDisplay("Rooms", Card.CardType.ROOM, cards);
    createDisplay("Weapons", Card.CardType.WEAPON, cards);
  }
  
  private void createDisplay(String labelString, Card.CardType cardType, ArrayList<Card> cards)
  {
    JPanel panel = new JPanel();
    panel.setLayout(new GridLayout(0, 1));
    panel.setBorder(new TitledBorder(new EtchedBorder(), labelString));
    for (Card card : cards) {
      if (card.getCardType() == cardType)
      {
        JTextField field = new JTextField(card.getCardName());
        panel.add(field);
      }
    }
    add(panel);
  }
}
