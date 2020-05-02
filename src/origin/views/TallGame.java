package origin.views;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import javafx.util.Pair;
import origin.AppRoot;
import origin.model.GameData;
import origin.utils.GuiHelper;
import origin.utils.RouteState;

import java.text.NumberFormat;
import java.util.ArrayList;

/**
 * Tall game box. Shows image of the game with small description box.
 */
public class TallGame extends StackPane {
    private GameData game;
    private VBox gameInfo;
    private Button gameButton;
    private Label title;
    private Label price;
    private RouteState routeState;

    private Chips chips;

    private VBox createGameInfo() {
        VBox box = new VBox();
        this.title.wrapTextProperty().bind(box.fillWidthProperty());
        this.price.wrapTextProperty().bind(box.fillWidthProperty());

        box.getChildren().addAll(this.title, this.price, this.chips);
        box.setStyle("-fx-background-color: linear-gradient(from 0% 100% to 30% 0%, #000000, " + this.game.color + ")");
        box.getStyleClass().addAll("minimized-info", "game-info");
        return box;
    }

    private void expandInfo() {
        GuiHelper.SwapClasses(this.gameInfo, "minimized-info", "expanded-info");
        this.chips.setVisible(true);
    }

    private void shrinkInfo() {
        GuiHelper.SwapClasses(this.gameInfo, "expanded-info", "minimized-info");
        this.chips.setVisible(false);
    }

    private Button createButton() {
        Button button = new Button();
        button.getStyleClass().add("game-button");
        button.setOnAction((evt) -> {
            routeState.pushState(new ArrayList<>() {{
                add(new Pair<>("page", AppRoot.GAME_PAGE_NAME));
                add(new Pair<>("gameData", game));
            }});
        });
        button.setOnMouseEntered((evt) -> {
            this.expandInfo();
        });
        button.setOnMouseExited((evt) -> {
            this.shrinkInfo();
        });
        return button;
    }

    public TallGame(GameData game, RouteState routeState) {
        super();
        this.game = game;
        this.routeState = routeState;
        ImageView image = new ImageView(new Image(game.vertImgUri));

        this.title = new Label(this.game.title);
        this.title.getStyleClass().add("game-title");

        NumberFormat formatter = NumberFormat.getCurrencyInstance();
        String priceStr = (game.owned)? "Owned": formatter.format(game.price);
        this.price = new Label(priceStr);
        this.price.getStyleClass().add((game.owned)? "game-owned": "game-price");
        this.price.setTranslateX(10.0);

        ArrayList<String> chipItems = new ArrayList<>();
        chipItems.addAll(game.categories);
        chipItems.addAll(game.filters);
        chips = new Chips(chipItems);
        this.chips.getStyleClass().add("game-chips");
        this.chips.setVisible(false);

        this.gameInfo = this.createGameInfo();
        this.gameButton = this.createButton();

        this.getStylesheets().add("/styles/tallGame.css");
        this.getStyleClass().add("tall-game");

        this.getChildren().addAll(image, this.gameInfo, this.gameButton);
        this.setAlignment(Pos.BOTTOM_CENTER);
    }
}
