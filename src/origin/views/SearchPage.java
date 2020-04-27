package origin.views;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
    private HBox noMatchesBox;
    private BorderPane gamePane;
    private VBox body;
    private ScrollPane gameScrollPane;
    private FilterBar filterBar;
    private boolean showingSortList = true;

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

    public void initFilterBar() {
        filterBar = new FilterBar(null, true);
        filterBar.setListListener((games, changedField) -> {
            if (games.isEmpty()) {
                showNoMatches();
            } else {
                verticalGameList.setGames(games);
                showSortList();
            }
        });
        filterBar.linkWithRouteState(AppRoot.SEARCH_PAGE_NAME, routeState);
    }

    private void showNoMatches() {
        if (showingSortList) {
            listArea.getChildren().clear();
            listArea.getChildren().add(noMatchesBox);
            showingSortList = false;
        }
    }

    private void showSortList() {
        if (!showingSortList) {
            listArea.getChildren().clear();
            listArea.getChildren().add(verticalGameList);
            showingSortList = true;
        }
    }

    private void initNoMatchesBox() {
        noMatchesBox = new HBox();
        noMatchesBox.setAlignment(Pos.CENTER);
        noMatchesBox.setMinHeight(300.0);
        Label noMatchText = new Label("No Matching Games...");
        noMatchText.getStyleClass().add("no-match-text");
        Button noMatchButton = new Button("Back To Store");
        noMatchButton.getStyleClass().add("no-match-button");
        noMatchButton.setOnAction((evt) -> {
            routeState.pushState(new ArrayList<>() {{
                add(new Pair<>("page", AppRoot.STORE_PAGE_NAME));
            }});
        });
        noMatchesBox.getChildren().addAll(noMatchText, noMatchButton);
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

        verticalGameList = new VerticalGameList(routeState);
        listArea.getChildren().add(verticalGameList);
        gameScrollPane = new ScrollPane();
        gameScrollPane.setContent(listArea);
        gameScrollPane.setFitToWidth(true);

        initFilterBar();
        initNoMatchesBox();

        gamePane = new BorderPane();
        gamePane.setCenter(gameScrollPane);
        gamePane.setTop(filterBar);
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
                GameCollection gameCollection = (GameCollection)state.get("gameCollection");
                if (gameCollection != searchCollection) {
                    searchCollection = gameCollection;
                    verticalGameList.setGames(searchCollection.games);
                    filterBar.setGameCollection(searchCollection);
                    if (searchCollection.games.isEmpty()) {
                        showNoMatches();
                    } else {
                        showSortList();
                    }
                }
                if (!state.containsKey("title")) {
                    System.err.println("SearchPage: Missing title in routeState");
                    return;
                }
                String titleStr = (String)state.get("title");
                titleText.setText(titleStr);
                if (state.containsKey("search") && state.get("search") != null) {
                    searchBar.setSearch((String)state.get("search"));
                } else {
                    searchBar.setSearch("");
                }
            }
        });
    }
}
