package origin.views;

import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

/*
    A placeholder for the user's library of games
 */
public class Library extends VBox {
    public Library() {
        super();
        Text text = new Text("Library");
        text.setFill(Color.WHITE);
        this.getChildren().add(text);
    }
}
