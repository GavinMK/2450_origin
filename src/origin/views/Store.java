package origin.views;

import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import origin.model.GameCollection;
import origin.utils.RouteState;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Collections;

//TODO: This whole thing
public class Store extends VBox {
    private GameCollection masterGameCollection;
    private HBox searchHBox;
    private Search searchBar;
    private HorizontalGameList sales;
    private HorizontalGameList mostPopular;

    public Store(RouteState routeState) {
        super();
        this.getStylesheets().addAll("/styles/store.css");
        try {
            File gamesFile = new File("src/assets/games.csv");
            masterGameCollection = new GameCollection(gamesFile);
            this.mostPopular = new HorizontalGameList(masterGameCollection.sortDescendingPopular(), routeState);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        searchBar = new Search(masterGameCollection, routeState);
        searchHBox = new HBox();
        searchHBox.getStyleClass().add("search-h-box");
        searchHBox.setAlignment(Pos.CENTER_RIGHT);
        searchHBox.getChildren().add(searchBar);
        this.getChildren().addAll(searchHBox, mostPopular);
    }
}
