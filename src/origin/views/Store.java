package origin.views;

import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

//TODO: This whole thing
public class Store extends VBox {
    public Store() {
        super();
        Text text = new Text("Store");
        text.setFill(Color.WHITE);
        this.getChildren().add(text);
    }
}
