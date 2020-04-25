package origin.views;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import origin.model.GameData;

/**
 * Tall game box. Shows image of the game with small description box.
 */
public class TallGame extends StackPane {
    private GameData game;
    private VBox gameInfo;
    private Button gameButton;
    private Label title;
    private Label description;

    //TODO remove this when we actually have chips
    private Label chips;

    private VBox createGameInfo() {
        VBox box = new VBox();
        this.title.wrapTextProperty().bind(box.fillWidthProperty());
        this.description.wrapTextProperty().bind(box.fillWidthProperty());

        box.getChildren().addAll(this.title, this.description, this.chips);
        box.setStyle("-fx-background-color: linear-gradient(from 0% 100% to 30% 0%, #000000, " + this.game.color + ")");
        box.getStyleClass().addAll("minimized-info", "game-info");
        return box;
    }

    private void expandInfo() {
        this.gameInfo.getStyleClass().remove("minimized-info");
        this.gameInfo.getStyleClass().add("expanded-info");
        this.description.setVisible(true);
        this.chips.setVisible(true);
    }

    private void shrinkInfo() {
        this.gameInfo.getStyleClass().remove("expanded-info");
        this.gameInfo.getStyleClass().add("minimized-info");
        this.description.setVisible(false);
        this.chips.setVisible(false);
    }

    private Button createButton() {
        Button button = new Button();
        button.getStyleClass().add("game-button");
        button.setOnAction((evt) -> {

        });
        button.setOnMouseEntered((evt) -> {
            this.expandInfo();
        });
        button.setOnMouseExited((evt) -> {
            this.shrinkInfo();
        });
        return button;
    }

    public TallGame(GameData game) {
        super();
        this.game = game;
        ImageView image = new ImageView(new Image(game.vertImgUri));

        this.title = new Label(this.game.title);
        this.title.getStyleClass().add("game-title");

        this.description = new Label(this.game.description);
        this.description.getStyleClass().add("game-desc");
        this.description.setVisible(false);

        //TODO remove this when we actually have chips
        this.chips = new Label("*chips go here*");
        this.chips.getStyleClass().add("game-chips");
        this.chips.setVisible(false);

        this.gameInfo = this.createGameInfo();
        this.gameButton = this.createButton();

        this.getStylesheets().add("/styles/tallGame.css");

        this.getChildren().addAll(image, this.gameInfo, this.gameButton);
        this.setAlignment(Pos.BOTTOM_CENTER);


    }
}
