/**
 * Model.java
 * Matt Javaly & Elliot Mawby, 6 June 2016
 *
 * The Model class for our KeyHero game. Mainly responsible for
 * generating random list of keys, but also handles current user
 * score and level.
 */

package keyHero;

import java.util.Random;

public class Model {

    private int[] keys;
    private int userLevel;
    private int userScore;
    private int finalScore;

    /**
     * Constructor for the Model randomly generates 1000 integers and
     * puts them in an array.
     **/
    public Model() {
        this.userLevel = 1;
        this.userScore = 0;
        this.finalScore = 0;
        this.keys = new int[1000];
        Random randomGenerator = new Random();
        for (int i=0; i<1000; i++) {
            int randomInt = randomGenerator.nextInt(4);
            this.keys[i] = randomInt;
        }
    }

    public int getUserScore() {
        return this.userScore;
    }

    public int getUserLevel() {
        return this.userLevel;
    }

    public int getFinalScore() {
        return this.finalScore;
    }

    public int[] getKeys() {
        return this.keys;
    }

    /**
     * increments user level
     **/
    public void levelUp() {
        this.userLevel++;
    }

    /**
     * increments user score
     * @param correct whether to add or deduct points
     **/
    public void increment(boolean correct) {
        if (correct) {
            this.userScore = this.userScore + this.userLevel;
        }
        else {
            double currScore = (double) this.userScore;
            double newScore = currScore - Math.pow(this.userLevel,1.5);
            //System.out.println(newScore);
            this.userScore = (int) newScore;
        }
        if (this.finalScore <= this.userScore) {
            this.finalScore = this.userScore;
        }
    }
}
