/**
 * Key.java
 * Matt Javaly & Elliot Mawby, 6 June 2016
 *
 * The Key class for our KeyHero game. It contains the image,
 * starting location, and step method of each key.
 */

package keyHero;

import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Key extends Group {

    private final ImageView imageView;
    private Rectangle border;
    private double velocity = 10.0;

    /**
     * Constructor method for Key sets the image for the Key node and the border properties
     * of the node.
     * @param arrow An integer from the model to determine which arrow key image to use
     *              and the starting location of the key on the screen.
     **/
    public Key(int arrow) {
        String imagePath ="";
        if (arrow == 0) {
            imagePath = "/res/keyUp.png";
            this.setLayoutX(350);
            this.setLayoutY(0);
        } else if (arrow == 1) {
            imagePath = "/res/keyDown.png";
            this.setLayoutX(250);
            this.setLayoutY(10);
        } else if (arrow == 2) {
            imagePath = "/res/keyLeft.png";
            this.setLayoutX(150);
            this.setLayoutY(10);
        } else if (arrow == 3) {
            imagePath = "/res/keyRight.png";
            this.setLayoutX(450);
            this.setLayoutY(10);
        }
        Image image = new Image(getClass().getResourceAsStream(imagePath), 75.0, 75.0, false, false);
        this.imageView = new ImageView();
        this.imageView.setImage(image);
        this.getChildren().add(this.imageView);
        this.border = new Rectangle(0.0, 0.0, 0.0, 0.0);
        this.border.setFill(null);
        this.border.setStroke(Color.BLACK);
        this.getChildren().add(this.border);
    }

    public final Point2D getPosition() {
        Point2D position = new Point2D(this.getLayoutX(), this.getLayoutY());
        return position;
    }

    public final void setPosition(double x, double y) {
        this.setLayoutX(x);
        this.setLayoutY(y);
    }

    /**
     * Move the Key one step in the direction and magnitude
     * of its velocity, which should be down and 10.
     */
    public void step() {
        Point2D position = this.getPosition();
        this.setPosition(position.getX(), position.getY() + this.velocity);
    }
}