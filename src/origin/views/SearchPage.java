package origin.views;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.util.Pair;
import origin.AppRoot;
import origin.model.GameCollection;
import origin.utils.RouteState;

import java.util.ArrayList;

public class SearchPage extends BorderPane {
    private GameCollection masterCollection;
    private GameCollection searchCollection;
    private RouteState routeState;

    private Button storeButton;
    private Search searchBar;
    private VBox listArea;
    private VerticalGameList verticalGameList;
    private HBox searchHBox;
    private HBox topHBox;
    private Text titleText;
    private HBox titleHBox;
    private HBox sortHBox;
    private BorderPane gamePane;
    private VBox body;
    private ScrollPane gameScrollPane;
    private DropDownButton sortButton;
    private DropDownButton filterButton;

    private Button createStoreButton() {
        Button button = new Button("Back to Store");
        button.getStyleClass().add("store-button");
        button.setOnAction((evt) -> {
            routeState.pushState(new ArrayList<>() {{
                add(new Pair<>("page", AppRoot.STORE_PAGE_NAME));
            }});
        });
        return button;
    }

    private Text createTitleText() {
        Text text = new Text();
        text.getStyleClass().add("title-text");
        return text;
    }

    public SearchPage(GameCollection masterCollection, RouteState routeState) {
        this.masterCollection = masterCollection;
        this.routeState = routeState;

        this.getStylesheets().add("/styles/searchPage.css");

        listArea = new VBox();
        listArea.getStyleClass().add("list-area");
        storeButton = createStoreButton();
        searchBar = new Search(masterCollection, routeState);
        searchHBox = new HBox();
        searchHBox.getChildren().addAll(searchBar);
        searchHBox.setAlignment(Pos.CENTER_RIGHT);
        topHBox = new HBox();
        topHBox.getStyleClass().add("top-box");
        topHBox.getChildren().addAll(storeButton, searchHBox);
        topHBox.setHgrow(searchHBox, Priority.ALWAYS);
        titleText = createTitleText();
        titleHBox = new HBox();
        titleHBox.getStyleClass().add("title-box");
        titleHBox.getChildren().add(titleText);
        sortButton = new DropDownButton("Sort By", new ArrayList<>(){{
            add("Relevant");
            add("Popular");
            add("Recent");
        }}, SelectionMode.SINGLE);
        filterButton = new DropDownButton("Filter", new ArrayList<>(){{
            add("Relevant");
            add("Popular");
            add("Recent");
        }}, SelectionMode.MULTIPLE);
        filterButton.setSelectedItems(new ArrayList<>(){{
            add("Relevant");
            add("Popular");
        }});
        sortHBox = new HBox();
        sortHBox.getStyleClass().add("sort-box");
        sortHBox.getChildren().addAll(sortButton, filterButton);
        sortHBox.setAlignment(Pos.CENTER_LEFT);
        verticalGameList = new VerticalGameList(routeState);
        listArea.getChildren().add(verticalGameList);
        gameScrollPane = new ScrollPane();
        gameScrollPane.setContent(listArea);
        gameScrollPane.setFitToWidth(true);
        gamePane = new BorderPane();
        gamePane.setCenter(gameScrollPane);
        gamePane.setTop(sortHBox);
        body = new VBox();
        body.getChildren().addAll(titleHBox, gamePane);
        this.setCenter(body);
        this.setTop(topHBox);

        routeState.subscribe((state) -> {
            if (state.get("page") == AppRoot.SEARCH_PAGE_NAME) {
                if (!state.containsKey("gameCollection")) {
                    System.err.println("SearchPage: Missing gameCollection in routeState");
                    return;
                }
                searchCollection = (GameCollection)state.get("gameCollection");
                verticalGameList.setGames(searchCollection.games);
                if (!state.containsKey("search")) {
                    System.err.println("SearchPage: Missing search in routeState");
                    return;
                }
                String searchText = (String)state.get("search");
                titleText.setText("Results for \"" + searchText + "\"");
                searchBar.setSearch(searchText);
            }
        });
    }
}
