package clueGame;

/**
 * 
 * Class used to send information about a guess to the server to 
 * verify if it equals the correct solution to the game.
 *
 */
public class Guess
{
  public String person;
  public String weapon;
  public String room;
  
  public Guess(String person, String room, String weapon)
  {
    this.person = person;
    this.room = room;
    this.weapon = weapon;
  }
  
  public Guess() {}
}
