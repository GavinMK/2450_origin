package origin.views;

import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import origin.model.GameData;
import origin.utils.RouteState;

import java.util.List;

public class VerticalGameList extends VBox {
    private RouteState routeState;

    public VerticalGameList(RouteState routeState) {
        super();
        this.routeState = routeState;
    }

    public void setGames(List<GameData> games) {
        this.getChildren().clear();
        for (GameData game: games) {
            this.getChildren().add(new WideGame(game, routeState));
        }
    }
}
