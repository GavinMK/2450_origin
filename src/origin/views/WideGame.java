package origin.views;

import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
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
    private GridPane infoPane;
    private Text title;
    private Text price;
    private Text description;
    private RouteState routeState;

    private ImageView createGameImage() {
        try {
            BufferedImage img = ImageIO.read(new File("src" + game.horzImgUri));
            System.out.println("CROPPING: " + game.horzImgUri);
            ImageView imageView = new ImageView(SwingFXUtils.toFXImage(img.getSubimage(750, 0, 500, 500), null));
            imageView.getStyleClass().add("game-image");
            imageView.setPreserveRatio(true);
            imageView.setFitHeight(75);
            return imageView;
        } catch (IOException e) {
            System.out.println("Failed to crop: " + game.horzImgUri);
        }
        return null;
    }

    private Text createGameTitle() {
        Text text = new Text(game.title);
        text.getStyleClass().add("game-title");
        return text;
    }

    private Text createGamePrice() {
        NumberFormat formatter = NumberFormat.getCurrencyInstance();
        String priceStr = formatter.format(game.price);
        Text text = new Text(priceStr);
        text.getStyleClass().add("game-price");
        return text;
    }

    private Text createGameDescription() {
        Text text = new Text(game.description);
        text.getStyleClass().add("game-description");
        return text;
    }

    private Button createButton() {
        Button button = new Button();
        button.getStyleClass().add("game-button");

        return button;
    }

    public void initInfoPane() {
        title = createGameTitle();
        price = createGamePrice();
        description = createGameDescription();
        infoPane = new GridPane();
        infoPane.getStyleClass().add("info-pane");
        infoPane.add(title, 0, 0);
        infoPane.add(price, 0, 1);
        infoPane.add(description, 1, 0);
        infoPane.setHgap(10);
        infoPane.setVgap(10);
    }

    public WideGame(GameData game, RouteState routeState) {
        super();
        this.game = game;
        this.routeState = routeState;

        this.getStylesheets().add("/styles/wideGame.css");
        this.getStyleClass().add("wide-game");
        gameImage = createGameImage();
        initInfoPane();

        this.setOnMouseClicked((evt) -> {
            routeState.pushState(new ArrayList<>() {{
                add(new Pair<>("page", AppRoot.GAME_PAGE_NAME));
                add(new Pair<>("gameData", game));
            }});
        });

        this.getChildren().addAll(gameImage, infoPane);
    }
}
