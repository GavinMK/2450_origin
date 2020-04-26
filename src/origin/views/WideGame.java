package origin.views;

import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Pair;
import origin.AppRoot;
import origin.model.GameData;
import origin.utils.RouteState;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;

public class WideGame extends HBox {
    private GameData game;
    private ImageView gameImage;
    private HBox gameInfo;
    private Label title;
    private Label price;
    private RouteState routeState;

    private ImageView createGameImage() {
        try {
            BufferedImage img = ImageIO.read(new File("src" + game.horzImgUri));
            System.out.println("CROPPING: " + game.horzImgUri);
            ImageView imageView = new ImageView(SwingFXUtils.toFXImage(img.getSubimage(450, 0, 1100, 500), null));
            imageView.getStyleClass().add("game-image");
            imageView.setPreserveRatio(true);
            imageView.setFitHeight(120);
            return imageView;
        } catch (IOException e) {
            System.out.println("Failed to crop: " + game.horzImgUri);
        }
        return null;
    }

    private Label createGameTitle() {
        Label text = new Label(game.title);
        text.getStyleClass().add("game-title");
        return text;
    }

    private Label createGamePrice() {
        NumberFormat formatter = NumberFormat.getCurrencyInstance();
        String priceStr = formatter.format(game.price);
        Label text = new Label(priceStr);
        text.getStyleClass().add("game-price");
        return text;
    }

    private Label createGameDescription() {
        Label text = new Label(game.description);
        text.getStyleClass().add("game-description");
        return text;
    }

    private Label createRating() {
        Label rating = new Label("★★★★★");
        rating.getStyleClass().add("game-rating");
        return rating;
    }

    private Label createOwned() {
        Label owned = new Label("Owned");
        owned.getStyleClass().add("game-owned");
        return owned;
    }

    private Button createButton() {
        Button button = new Button();
        button.getStyleClass().add("game-button");

        return button;
    }

    public void initInfoPane() {
        title = createGameTitle();
        price = createGamePrice();
        gameImage = createGameImage();
        Label rating = this.createRating();
        VBox infoBox = new VBox(0);
        infoBox.getChildren().addAll(title, rating);
        System.out.println(game.owned);
        if (game.owned) {
            infoBox.getChildren().add(this.createOwned());
        }
        else infoBox.getChildren().add(price);
        infoBox.getStyleClass().add("info-box");
        gameInfo = new HBox();
        gameInfo.getStyleClass().add("game-info");
        gameInfo.getChildren().addAll(gameImage, infoBox);
    }

    public WideGame(GameData game, RouteState routeState) {
        super();
        this.game = game;
        this.routeState = routeState;

        this.getStylesheets().add("/styles/wideGame.css");
        this.getStyleClass().add("wide-game");
        initInfoPane();

        this.setOnMouseClicked((evt) -> {
            routeState.pushState(new ArrayList<>() {{
                add(new Pair<>("page", AppRoot.GAME_PAGE_NAME));
                add(new Pair<>("gameData", game));
            }});
        });

        this.getChildren().addAll(this.gameInfo);
    }
}
