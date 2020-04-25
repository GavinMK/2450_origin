package origin.views;

import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import origin.model.GameCollection;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Collections;

//TODO: This whole thing
public class Store extends VBox {

    private HorizontalGameList sales;
    private HorizontalGameList mostPopular;

    public Store() {
        super();
        Text text = new Text("Store");
        text.setFill(Color.WHITE);
        this.getChildren().add(text);

        File gamesFile = new File("src/assets/games.csv");
        System.out.println("PATH: " + gamesFile.getAbsolutePath());
        try {
            GameCollection gameCollection = new GameCollection(gamesFile);
            this.mostPopular = new HorizontalGameList(gameCollection.sortDescendingPopular());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.getChildren().add(mostPopular);
    }
}
