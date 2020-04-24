package origin.views;

import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class Profile extends VBox {
    public Profile() {
        super();
        Text text = new Text("Profile");
        text.setFill(Color.WHITE);
        this.getChildren().add(text);
    }
}
