package origin.views;

import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

//TODO: This whole thing
public class GamePage extends VBox {
    public GamePage() {
        super();
        Text text = new Text("Game Page");
        text.setFill(Color.WHITE);
        this.getChildren().add(text);
    }
}
