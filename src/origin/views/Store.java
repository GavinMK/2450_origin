package origin.views;

import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Pair;
import origin.AppRoot;
import origin.model.GameCollection;
import origin.model.GameData;
import origin.utils.RouteState;

import java.util.ArrayList;
import java.util.List;

//TODO: This whole thing
public class Store extends BorderPane {
    private GameCollection masterCollection;
    private RouteState routeState;
    private ScrollPane scroller;
    private VBox allLists;
    private HBox searchHBox;
    private VBox body;
    private Search searchBar;
    private VBox sales;
    private VBox mostPopular;
    private VBox mostRecent;

    private Button createShowButton(String buttonClass, String buttonText, String title, GameCollection collection) {
        Button showButton = new Button(buttonText);
        showButton.getStyleClass().add(buttonClass);
        showButton.setOnAction((evt) -> {
            //TODO make it so that the title isn't results for X and dont populate the search bar.
            this.routeState.pushState(new ArrayList<>() {{
                add(new Pair<>("page", AppRoot.SEARCH_PAGE_NAME));
                add(new Pair<>("search", title));
                add(new Pair<>("gameCollection", collection));
            }});

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
        Button showButton = this.createShowButton("show-button", "Show All", title, new GameCollection(games));
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
        Button showButton = createShowButton("sales-button", "See All Sales", title + " Super Sale", new GameCollection(games));
        controls.setAlignment(Pos.CENTER);
        controls.getChildren().add(showButton);
        controls.getStyleClass().add("sale-controls");

        box.getChildren().addAll(info, list, controls);

        return box;
    }

    public Store(GameCollection masterCollection, RouteState routeState) {
        super();
        this.masterCollection = masterCollection;
        this.routeState = routeState;

        this.getStylesheets().addAll("/styles/store.css");
        searchBar = new Search(masterCollection, routeState);
        this.routeState = routeState;
        this.scroller = new ScrollPane();
        this.allLists = new VBox();
        this.sales = createSales("Need 4 Speed", masterCollection.sortPrice());
        this.mostPopular = createGameList("Most Popular", masterCollection.sortDescendingPopular());
        this.mostRecent = createGameList("Most Recent", masterCollection.sortRecent());
        searchBar = new Search(masterCollection, this.routeState);
        searchHBox = new HBox();
        searchHBox.getStyleClass().add("search-h-box");
        searchHBox.setAlignment(Pos.CENTER_RIGHT);
        searchHBox.getChildren().add(searchBar);

        Region placeholder = new Region();
        placeholder.setMinHeight(60);
        this.allLists.getChildren().addAll(this.sales, this.mostPopular, this.mostRecent, placeholder);
        this.scroller.setContent(this.allLists);
        this.scroller.setFitToWidth(true);

        body = new VBox();
        body.getChildren().addAll(scroller);
        //BorderPane is used to set visibility order while maintaining positioning: make sure you set center first, then top
        this.setCenter(body);
        this.setTop(searchHBox);
    }
}
