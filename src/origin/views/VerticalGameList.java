package origin.views;

import javafx.scene.layout.VBox;
import origin.model.GameData;
import origin.utils.RouteState;

import java.util.List;

/*
    Simple wrapper for a list of games
 */
public class VerticalGameList extends VBox {
    private RouteState routeState;

    public VerticalGameList(RouteState routeState) {
        super();
        this.routeState = routeState;
        this.getStyleClass().add("/styles/vert-gameList.css");
        this.setSpacing(15);
    }

    public void setGames(List<GameData> games) {
        this.getChildren().clear();
        for (GameData game: games) {
            this.getChildren().add(new WideGame(game, routeState));
        }
    }
}
