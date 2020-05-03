package origin.views;

import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

/*
    Placeholder for the Origin Access page
 */
public class Access extends VBox {
    public Access() {
        super();
        Text text = new Text("Access");
        text.setFill(Color.WHITE);
        this.getChildren().add(text);
    }
}
