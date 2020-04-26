package origin.views;

import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;
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
public class Store extends BorderPane {
    private GameCollection masterCollection;
    private RouteState routeState;
    private HBox searchHBox;
    private VBox body;
    private Search searchBar;
    private HorizontalGameList sales;
    private HorizontalGameList mostPopular;

    public Store(GameCollection masterCollection, RouteState routeState) {
        super();
        this.masterCollection = masterCollection;
        this.routeState = routeState;

        this.getStylesheets().addAll("/styles/store.css");
        searchBar = new Search(masterCollection, routeState);
        searchHBox = new HBox();
        searchHBox.getStyleClass().add("search-h-box");
        searchHBox.setAlignment(Pos.CENTER_RIGHT);
        searchHBox.getChildren().add(searchBar);

        mostPopular = new HorizontalGameList(masterCollection.sortDescendingPopular(), routeState);
        body = new VBox();
        body.getChildren().addAll(mostPopular);
        //BorderPane is used to set visibility order while maintaining positioning: make sure you set center first, then top
        this.setCenter(body);
        this.setTop(searchHBox);
    }
}
