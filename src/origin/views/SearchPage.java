package origin.views;

import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class SearchPage extends VBox {
    public SearchPage() {
        Text text = new Text("Search Page");
        text.setFill(Color.WHITE);
        this.getChildren().add(text);
    }
}
