package origin.views;

import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
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

/*
    Wide selectable box of game information shown in the vertical game list
 */
public class WideGame extends HBox {
    private GameData game;
    private ImageView gameImage;
    private HBox gameInfo;
    private Label title;
    private Label price;
    private RouteState routeState;
    private Chips chips;

    private ImageView createGameImage() {
        try {
            BufferedImage img = ImageIO.read(new File("src" + game.horzImgUri));
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

    public void initInfoPane() {
        title = createGameTitle();
        price = createGamePrice();
        gameImage = createGameImage();
        Label rating = this.createRating();
        VBox infoBox = new VBox(0);
        infoBox.getChildren().addAll(title, rating);
        if (game.owned) {
            infoBox.getChildren().add(this.createOwned());
        }
        else infoBox.getChildren().add(price);
        infoBox.getStyleClass().add("info-box");

        ArrayList<String> chipItems = new ArrayList<>();
        chipItems.addAll(game.categories);
        chipItems.addAll(game.filters);
        chips = new Chips(chipItems);

        gameInfo = new HBox();
        gameInfo.getStyleClass().add("game-info");
        gameInfo.setAlignment(Pos.CENTER_LEFT);
        gameInfo.getChildren().addAll(gameImage, infoBox, chips);
    }

    public WideGame(GameData game, RouteState routeState) {
        super();
        this.game = game;
        this.routeState = routeState;

        this.getStylesheets().add("/styles/wideGame.css");
        this.getStyleClass().add("wide-game");
        initInfoPane();

        this.setOnMouseClicked((evt) -> {
            routeState.pushState(new ArrayList<Pair<String, Object>>() {{
                add(new Pair<>("page", AppRoot.GAME_PAGE_NAME));
                add(new Pair<>("gameData", game));
            }});
        });

        this.getChildren().addAll(this.gameInfo);
    }
}
