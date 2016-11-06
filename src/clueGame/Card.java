package clueGame;

/**
 * 
 * Parent card class for the room, weapon and person cards.
 * 
 */
public class Card
{
  private String cardName;
  private CardType cardType;
  
  public static enum CardType
  {
    ROOM,  WEAPON,  PERSON;
  }
  
  public Card(String cardName, CardType cardType)
  {
    this.cardName = cardName;
    this.cardType = cardType;
  }
  
  public boolean equals(Object aThat)
  {
    if (this == aThat) {
      return true;
    }
    if (!(aThat instanceof Card)) {
      return false;
    }
    Card that = (Card)aThat;
    return 
      (this.cardName.equals(that.cardName)) && 
      (this.cardType == that.cardType);
  }
  
  public CardType getCardType()
  {
    return this.cardType;
  }
  
  public String getCardName()
  {
    return this.cardName;
  }
}
