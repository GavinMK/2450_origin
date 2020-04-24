package origin.views;

import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class Access extends VBox {
    public Access() {
        super();
        Text text = new Text("Access");
        text.setFill(Color.WHITE);
        this.getChildren().add(text);
    }
}
