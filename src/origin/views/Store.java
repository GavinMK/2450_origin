package origin.views;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import origin.model.GameCollection;
import origin.model.GameData;
import origin.utils.RouteState;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

//TODO: This whole thing
public class Store extends VBox {
    private GameCollection masterGameCollection;
    private ScrollPane scroller;
    private VBox allLists;
    private HBox searchHBox;
    private Search searchBar;
    private VBox sales;
    private VBox mostPopular;
    private VBox mostRecent;
    private RouteState routeState;

    private Button createShowButton(String buttonClass, String buttonText) {
        Button showButton = new Button(buttonText);
        showButton.getStyleClass().add(buttonClass);
        showButton.setOnAction((evt) -> {
            //TODO navigate to filtered search page
        });
        return showButton;
    }

    private VBox createGameList(String title, List<GameData> games) {
        VBox box = new VBox();

        HBox controls = new HBox();
        Label gameTitle = new Label(title);
        gameTitle.getStyleClass().add("list-title");
        Region gap = new Region();
        gap.getStyleClass().add("regular-region");
        Button showButton = this.createShowButton("show-button", "Show All");
        controls.getChildren().addAll(gameTitle, gap, showButton);

        HorizontalGameList list = new HorizontalGameList(games, this.routeState, null);
        box.getChildren().addAll(controls, list);
        return box;
    }

    private VBox createSales(String title, List<GameData> games) {
        VBox box = new VBox();

        HBox info = new HBox();
        Label saleName = new Label(title);
        saleName.getStyleClass().add("list-title");
        Label superSale = new Label(" Super Sale");
        superSale.getStyleClass().addAll("list-text", "orange");
        Region gap = new Region();
        gap.getStyleClass().add("sale-region");
        Label expires = new Label("Expires in");
        expires.getStyleClass().add("list-title");
        Label expireTime = new Label(" 3 days");
        expireTime.getStyleClass().addAll("list-text", "orange");
        info.getChildren().addAll(saleName, superSale, gap, expires, expireTime);
        info.getStyleClass().add("sale-info");

        //TODO hardcode in a list of sales?
        HorizontalGameList list = new HorizontalGameList(games, this.routeState, "/assets/games/salese.png");
        list.getStyleClass().add("sale-list");

        HBox controls = new HBox();
        Button showButton = createShowButton("sales-button", "See All Sales");
        controls.setAlignment(Pos.CENTER);
        controls.getChildren().add(showButton);
        controls.getStyleClass().add("sale-controls");

        box.getChildren().addAll(info, list, controls);

        return box;
    }

    public Store(RouteState routeState) {
        super();
        this.routeState = routeState;
        this.scroller = new ScrollPane();
        this.allLists = new VBox();
        this.getStylesheets().addAll("/styles/store.css");
        try {
            File gamesFile = new File("src/assets/games.csv");
            masterGameCollection = new GameCollection(gamesFile);
            this.sales = createSales("Need 4 Speed", masterGameCollection.sortPrice());
            this.mostPopular = createGameList("Most Popular", masterGameCollection.sortDescendingPopular());
            this.mostRecent = createGameList("Most Recent", masterGameCollection.sortRecent());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        searchBar = new Search(masterGameCollection, this.routeState);
        searchHBox = new HBox();
        searchHBox.getStyleClass().add("search-h-box");
        searchHBox.setAlignment(Pos.CENTER_RIGHT);
        searchHBox.getChildren().add(searchBar);
        Region placeholder = new Region();
        placeholder.setMinHeight(60);
        this.allLists.getChildren().addAll(this.sales, this.mostPopular, placeholder);
        this.scroller.setContent(this.allLists);
        this.scroller.setFitToWidth(true);
        this.getChildren().addAll(searchHBox, scroller);
    }
}
