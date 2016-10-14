package keyHero;

/**
 * Created by mawbye on 6/4/16.
 */
public interface ModelInt {
    /**
     * Constructor method for the game class which is the Model (MVC architecture)
     * @return void
     **/

    /**
     * returns the level object for the game
     * @return getNextKey returns the next key for the user to press
     **/
    public int getNextKey();

    /**
     * levels up user
     * @return void
     **/
    public void levelUp();

    /**
     * changes score of user, depending on whether key input is correct or not
     * @return void
     **/
    public void increment(boolean correct, int level);
}
