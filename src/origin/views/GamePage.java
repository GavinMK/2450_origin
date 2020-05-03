package origin.views;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import origin.AppRoot;
import origin.model.GameData;
import origin.utils.RouteState;

import java.util.HashMap;

/*
    A page where games can be viewed and purchased
 */
public class GamePage extends VBox {
    private GameData game;
    private RouteState routeState;
    private VBox infoBox;
    private Label title;
    private Label price;
    private Label desc;
    private Button buyButton;
    private ImageView gameBanner;

    private void updatePage() {
        this.gameBanner.setImage(new Image(this.game.horzImgUri));
        this.title.setText(this.game.title);
        this.price.setText("$" + String.format("%.2f", this.game.price));
        this.desc.setText(this.game.description);
    }

    private ImageView createBanner() {
        ImageView img = new ImageView();
        img.getStyleClass().add("game-banner");
        img.setFitWidth(AppRoot.UI_WIDTH);
        img.setPreserveRatio(true);
        return img;
    }

    private Button createBuyButton() {
        Button button = new Button("Purchase");
        button.getStyleClass().add("buy-button");
        button.setOnAction((evt) -> {

        });
        return button;
    }

    private VBox createInfoBox() {
        VBox box = new VBox();

        this.title = new Label();
        this.title.getStyleClass().addAll("game-title", "game-text");

        HBox bottomBox = new HBox(20);
        VBox rightBox = new VBox(20);
        VBox leftBox = new VBox();

        this.price = new Label();
        this.price.getStyleClass().addAll("game-price", "game-text");
        this.buyButton = this.createBuyButton();
        this.desc = new Label();
        this.desc.getStyleClass().addAll("game-desc", "game-text");
        this.desc.wrapTextProperty().bind(leftBox.fillWidthProperty());

        rightBox.getStyleClass().add("bottom-right-box");
        rightBox.getChildren().addAll(this.price, this.buyButton);
        rightBox.setAlignment(Pos.CENTER);
        leftBox.getStyleClass().add("bottom-left-box");
        leftBox.getChildren().addAll(this.desc);
        bottomBox.getStyleClass().add("bottom-box");
        bottomBox.getChildren().addAll(leftBox, rightBox);

        box.getStyleClass().add("info-box");
        box.getChildren().addAll(this.title, bottomBox);

        return box;
    }

    public GamePage(RouteState routeState) {
        super();
        this.routeState = routeState;

        this.gameBanner = this.createBanner();
        this.infoBox = this.createInfoBox();

        this.getStylesheets().add("/styles/gamePage.css");
        this.getChildren().addAll(this.gameBanner, this.infoBox);

        this.routeState.subscribe((HashMap<String, Object> state) -> {
            String pageName = (String)state.get("page");
            GameData game = (GameData)state.get("gameData");
            if (pageName.equals("Game") && game != null && game != this.game) {
                System.out.println("Loading game: " + game.title);
                this.game = game;
                this.updatePage();
            }
        });
    }


}
