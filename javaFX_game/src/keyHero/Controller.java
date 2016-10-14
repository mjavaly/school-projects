/**
 * Controller.java
 * Matt Javaly & Elliot Mawby, 6 June 2016
 *
 * The Controller class for our KeyHero game. It handles all KeyEvents,
 * and accounts for any changes in game state during game play.
 */

package keyHero;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.AudioClip;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.*;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.ArrayList;
import javafx.fxml.FXMLLoader;

public class Controller implements EventHandler<KeyEvent> {
    private Model model;
    private ArrayList<Key> keyList;
    private ArrayList<Key> zoneList;
    final private double FRAMES_PER_SECOND = 20.0;
    private Timer timer;
    private int updateCounter;
    private int dialogueCounter;
    private double speedControl;
    private int scoreRequired;
    private AudioClip hit;
    private AudioClip miss;
    private AudioClip song;
    private AudioClip lostSong;
    private AudioClip levelUp;
    private boolean lost = false;
    private boolean started = false;
    private boolean paused = true;
    @FXML AnchorPane mainScreen;
    @FXML AnchorPane gameBoard;
    @FXML Label levelLabel;
    @FXML Rectangle boxLeft;
    @FXML Rectangle boxUp;
    @FXML Rectangle boxRight;
    @FXML Rectangle boxDown;
    @FXML Button startButton;
    @FXML Label dialogueLabel1;
    @FXML Label dialogueLabel2;
    @FXML Rectangle scoreBarFill;
    @FXML Rectangle scoreBar;
    @FXML Button battleButton;
    @FXML Label startLabel;

    /**
     * Constructor method for Controller writes values to instance variables and
     * gets the data from the model to construct list of keys.
     **/
    public Controller() {
        this.updateCounter = 0;
        this.dialogueCounter = 0;
        this.speedControl = 16;
        this.scoreRequired = 10;
        this.hit = new AudioClip(getClass().getResource("/res/ping.wav").toString()) ;
        this.miss = new AudioClip(getClass().getResource("/res/wrong.mp3").toString()) ;
        this.levelUp = new AudioClip(getClass().getResource("/res/levelup.mp3").toString()) ;
        this.song = new AudioClip(getClass().getResource("/res/PennyRoyal.mp3").toString()) ;
        this.lostSong = new AudioClip(getClass().getResource("/res/wompwomp.mp3").toString());
        this.song.setVolume(.65);
        this.hit.setVolume(.9);
        this.levelUp.setVolume(2);
        this.zoneList = new ArrayList<Key>(10);
        this.model = new Model();
        this.keyList = new ArrayList<Key>(1000);
        int[] modelKeyList = this.model.getKeys();

        for (int i = 0; i< modelKeyList.length; i++){
            this.keyList.add(new Key(modelKeyList[i]));
        }
    }

    /**
     * Need a separate initialize for board properties because board does not exist when
     * the controller is constructed.
     **/
    @FXML
    public void initialize() {
        this.gameBoard.getChildren().add(this.keyList.get(0));
        this.dialogueLabel1.setText("When the arrow is\nin the box, press\nthe corresponding key!\n" +
                "Press the key on\ntime or you lose health.");
    }

    /**
     * Sets animation speed by frames per second
     **/
    private void setUpAnimationTimer() {
        TimerTask timerTask = new TimerTask() {
            public void run() {
                Platform.runLater(new Runnable() {
                    public void run() {
                        updateAnimation();
                    }
                });
            }
        };

        final long startTimeInMilliseconds = 0;
        final long repetitionPeriodInMilliseconds = 100;
        long frameTimeInMilliseconds = (long) (1000.0 / FRAMES_PER_SECOND);
        this.timer = new java.util.Timer();
        this.timer.schedule(timerTask, 0, frameTimeInMilliseconds);
    }

    /**
     * Method for updating the view to account for changes in score, level, key positions,
     * and game status.
     **/
    private void updateAnimation() {
        if(!this.lost) {
            if(this.model.getUserScore() < -1 || this.keyList.size() < 2) {
                this.lost = true;
                this.paused = true;
                this.song.stop();
                this.lostSong.play();
                int finalScore = this.model.getFinalScore();
                this.dialogueLabel1.setText(" ");
                this.dialogueLabel2.setText("Final Score: " + finalScore);
                this.startButton.setText("Restart");
            }
            this.updateCounter++;
            int numKeysOnScreen = this.gameBoard.getChildren().size() - 5;
            // Add a new key
            if (this.updateCounter % this.speedControl < 1) {
                this.gameBoard.getChildren().add(this.keyList.get(numKeysOnScreen));
            }
            // Reset the box color every 3 updates
            if (this.updateCounter % 3 == 0) {
                this.boxLeft.setFill(Color.TRANSPARENT);
                this.boxRight.setFill(Color.TRANSPARENT);
                this.boxDown.setFill(Color.TRANSPARENT);
                this.boxUp.setFill(Color.TRANSPARENT);
            }
            // Move each key on the screen and test to see if they are in the Zone or past the Zone
            for (int i = 0; i < numKeysOnScreen; i++) {
                this.keyList.get(i).step();
                if (this.keyList.get(i).getPosition().getY() > 600 && this.keyList.get(i).getPosition().getY() < 670) {
                    if (!this.zoneList.contains(this.keyList.get(i))) {
                        this.zoneList.add(this.keyList.get(i));
                    }
                } else if (this.keyList.get(i).getPosition().getY() >= 670) {
                    this.zoneList.remove(this.keyList.get(i));
                }
                if (this.keyList.get(i).getPosition().getY() > 725) {
                    this.gameBoard.getChildren().remove(this.keyList.get(i));
                    this.keyList.remove(this.keyList.get(i));
                    this.model.increment(false);
                    i = i - 1; // Need to decrement i since we are decreasing the size of keyList
                }
            }
            int score = this.model.getUserScore();
            // Account for level up by resetting scoreBar and increasing speed
            if (score >= this.scoreRequired) {
                this.levelUp.play();
                this.model.levelUp();
                this.dialogueLabel1.setText(" ");
                this.dialogueLabel2.setText("Level up!");
                int level = this.model.getUserLevel();
                this.scoreRequired = score + (level * 10);
                this.levelLabel.setText("Level: " + level);
                this.speedControl = this.speedControl * .90;
                this.dialogueCounter = this.updateCounter + 40;
            }

            if (this.updateCounter == this.dialogueCounter) {
                this.dialogueLabel2.setText(" ");
            }
            double newScoreHeight = ((double) score/this.scoreRequired) * 300;
            this.scoreBarFill.setHeight(newScoreHeight);
        }
    }

    /**
     * Varies function and text of start button depending on game status
     **/
    public void onStartButton() throws IOException {
        if (!this.started) {
            this.startLabel.setVisible(false);
            this.song.play();
            this.started = true;
            this.startButton.setText("Pause");
            this.paused = false;
            this.setUpAnimationTimer();
        } else if (lost) {
            restart();
        } else {
            this.song.stop();
            this.startButton.setText("Start");
            this.started = false;
            this.timer.cancel();
            this.paused = true;
        }
    }

    /**
     * Resets scene and model for new game
     **/
    private void restart() throws IOException {
        //Get the current stage so that we do not need to close the window
        Stage stage = (Stage) this.mainScreen.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("KeyHero.fxml"));
        Parent root = (Parent)loader.load();
        // Set up a KeyEvent handler so we can respond to keyboard activity.
        root.setOnKeyPressed(loader.getController());
        stage.setTitle("Key Hero");
        stage.setScene(new Scene(root, 990, 775));
        stage.show();
        this.model = new Model();
    }

    /**
     * Handles key presses, deducting points for an incorrect one and rewarding points
     * for a correct one. Also changes box colors.
     * @param  keyEvent contains information on the key press.
     **/
    @Override
    public void handle(KeyEvent keyEvent) {
        if(!this.paused) {
            KeyCode code = keyEvent.getCode();
            boolean correct = false;
            if (code == KeyCode.LEFT || code == KeyCode.A) {
                for (int i = 0; i < this.zoneList.size(); i++) {
                    if (this.zoneList.get(i).getPosition().getX() == 150) {
                        this.keyList.remove(this.zoneList.get(i));
                        gameBoard.getChildren().remove(this.zoneList.get(i));
                        this.zoneList.remove(this.zoneList.get(i));
                        correct = true;
                        this.hit.play();
                        this.boxLeft.setFill(Color.GREEN);
                    }
                }
                this.model.increment(correct);
                if (!correct) {
                    this.boxLeft.setFill(Color.RED);
                    this.miss.play();
                }
                keyEvent.consume();
            } else if (code == KeyCode.RIGHT || code == KeyCode.D) {
                for (int i = 0; i < this.zoneList.size(); i++) {
                    if (this.zoneList.get(i).getPosition().getX() == 450) {
                        this.keyList.remove(this.zoneList.get(i));
                        this.gameBoard.getChildren().remove(this.zoneList.get(i));
                        this.zoneList.remove(this.zoneList.get(i));
                        correct = true;
                        this.hit.play();
                        this.boxRight.setFill(Color.GREEN);
                    }
                }
                this.model.increment(correct);
                if (!correct) {
                    this.boxRight.setFill(Color.RED);
                    this.miss.play();
                }
                keyEvent.consume();
            } else if (code == KeyCode.UP || code == KeyCode.W) {
                for (int i = 0; i < this.zoneList.size(); i++) {
                    if (this.zoneList.get(i).getPosition().getX() == 350) {
                        this.keyList.remove(this.zoneList.get(i));
                        this.gameBoard.getChildren().remove(this.zoneList.get(i));
                        this.zoneList.remove(this.zoneList.get(i));
                        correct = true;
                        this.hit.play();
                        this.boxUp.setFill(Color.GREEN);
                    }
                }
                this.model.increment(correct);
                if (!correct) {
                    this.boxUp.setFill(Color.RED);
                    this.miss.play();
                }
                keyEvent.consume();
            } else if (code == KeyCode.DOWN || code == KeyCode.S) {
                for (int i = 0; i < this.zoneList.size(); i++) {
                    if (this.zoneList.get(i).getPosition().getX() == 250) {
                        this.keyList.remove(this.zoneList.get(i));
                        this.gameBoard.getChildren().remove(this.zoneList.get(i));
                        this.zoneList.remove(this.zoneList.get(i));
                        correct = true;
                        this.hit.play();
                        this.boxDown.setFill(Color.GREEN);
                    }
                }
                this.model.increment(correct);
                if (!correct) {
                    this.boxDown.setFill(Color.RED);
                    this.miss.play();
                }
                keyEvent.consume();
            }
        }
    }
}