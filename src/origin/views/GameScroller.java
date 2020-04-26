package origin.views;

import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import origin.utils.RouteState;

public class GameScroller extends VBox {
    private RouteState routeState;

    public GameScroller(RouteState routeState) {
        super();
        this.routeState = routeState;

        Text text = new Text("Game Scroller");
        text.setFill(Color.WHITE);
        this.getChildren().add(text);
    }
}
