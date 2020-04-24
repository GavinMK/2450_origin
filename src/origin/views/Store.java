package origin.views;

import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import origin.model.GameCollection;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;

//TODO: This whole thing
public class Store extends VBox {
    public Store() {
        super();
        Text text = new Text("Store");
        text.setFill(Color.WHITE);
        this.getChildren().add(text);

        File gamesFile = new File("src/assets/games.csv");
        System.out.println("PATH: " + gamesFile.getAbsolutePath());
        try {
            GameCollection gameCollection = new GameCollection(gamesFile);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
