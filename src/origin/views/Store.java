package origin.views;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Pair;
import origin.AppRoot;
import origin.model.GameCollection;
import origin.model.GameData;
import origin.utils.RouteState;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/*
    A page showing game sales, filterable lists of the most popular and most recent games or a vertical list of games
 */
public class Store extends BorderPane {
    private GameCollection masterCollection;
    private RouteState routeState;
    private KeyManager keyManager;
    private ScrollPane scroller;
    private VBox body;
    private VBox allLists;
    private HBox searchHBox;
    private Search searchBar;
    private VBox sales;
    private VBox mostPopular;
    private VBox mostRecent;
    private HorizontalGameList popularList;
    private HorizontalGameList recentList;
    private VerticalGameList sortedGameList;
    private FilterBar filterBar;
    private VBox horzGameBox;
    private VBox vertGameBox;
    private VBox listBody;
    private BorderPane listPane;
    private VBox noMatchesBox;
    private String keyManagerSubID = null;
    private static final int NUM_GAMES_SCROLL = 4;
    private static final double SCROLL_BEHAVIOR_MARGIN = 20.0;

    private boolean showingSortList = false;
    private boolean showingHorzList = false;

    private Button createShowButton(String buttonClass, String buttonText) {
        Button showButton = new Button(buttonText);
        showButton.getStyleClass().add(buttonClass);
        return showButton;
    }

    private Button createShowButton(String buttonClass, String buttonText, String title, GameCollection collection) {
        Button showButton = new Button(buttonText);
        showButton.getStyleClass().add(buttonClass);
        showButton.setOnAction((evt) -> {
            this.routeState.pushState(new ArrayList<Pair<String, Object>>() {{
                add(new Pair<>("page", AppRoot.SEARCH_PAGE_NAME));
                add(new Pair<>("search", title));
                add(new Pair<>("gameCollection", collection));
                add(new Pair<>("page", AppRoot.SEARCH_PAGE_NAME));
                add(new Pair<>("title", title));
                add(new Pair<>("search", null));
                add(new Pair<>(AppRoot.SEARCH_PAGE_NAME + "SortBy", FilterBar.MOST_POPULAR));
            }});
        });
        return showButton;
    }

    private Pair<VBox, HorizontalGameList> createGameList(String title, List<GameData> games, Runnable onShowAll) {
        VBox box = new VBox();

        HBox controls = new HBox();
        Label gameTitle = new Label(title);
        gameTitle.getStyleClass().add("list-title");
        Region gap = new Region();
        gap.getStyleClass().add("regular-region");
        Button showButton = this.createShowButton("show-button", "Show All");
        showButton.setOnAction((evt) -> {
            onShowAll.run();
        });
        controls.getChildren().addAll(gameTitle, gap, showButton);

        HorizontalGameList list = new HorizontalGameList(games, this.routeState, null);
        list.getStyleClass().add("horz-game-list");
        box.getChildren().addAll(controls, list);
        return new Pair<>(box, list);
    }

    private VBox createSales(String title, List<GameData> games) {
        VBox box = new VBox();

        HBox info = new HBox();
        Label saleName = new Label(title);
        saleName.getStyleClass().addAll("list-title" , "large-text");
        Label superSale = new Label(" Super Sale");
        superSale.getStyleClass().addAll("list-text", "orange", "large-text");
        Region gap = new Region();
        gap.getStyleClass().add("sale-region");
        Label expires = new Label("Expires in");
        expires.getStyleClass().add("list-title");
        Label expireTime = new Label(" 3 days");
        expireTime.getStyleClass().addAll("list-text", "orange");
        info.getChildren().addAll(saleName, superSale, gap, expires, expireTime);
        info.getStyleClass().add("sale-info");

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

    private void initFilterBar() {
        filterBar = new FilterBar(masterCollection, false);
        filterBar.setCollectionListener((collection, changedField) -> {
            if (collection.games.isEmpty()) {
                showNoMatches();
            } else {
                popularList.setGames(collection.sortDescendingPopular());
                recentList.setGames(collection.sortRecent());
                showHorzLists();
            }
        });
        filterBar.setListListener((games, changedField) -> {
            if (games.isEmpty()) {
                showNoMatches();
            } else {
                sortedGameList.setGames(games);
                showSortList();
            }
        });
        filterBar.linkWithRouteState(AppRoot.STORE_PAGE_NAME, routeState);
    }

    private void showNoMatches() {
        if (showingHorzList || showingHorzList) {
            listBody.getChildren().clear();
            listBody.getChildren().add(noMatchesBox);
            showingSortList = false;
            showingHorzList = false;
        }
    }

    private void showSortList() {
        if (!showingSortList) {
            listBody.getChildren().clear();
            listBody.getChildren().add(vertGameBox);
            showingSortList = true;
            showingHorzList = false;
        }
    }

    private void showHorzLists() {
        if (!showingHorzList) {
            listBody.getChildren().clear();
            listBody.getChildren().add(horzGameBox);
            showingHorzList = true;
            showingSortList = false;
        }
    }

    private void initHorzGames() {
        horzGameBox = new VBox();
        Pair<VBox, HorizontalGameList> popularViews = createGameList("Most Popular", masterCollection.sortDescendingPopular(), () -> {
            filterBar.setSortBy(FilterBar.MOST_POPULAR);
        });
        this.mostPopular = popularViews.getKey();
        this.popularList = popularViews.getValue();
        Pair<VBox, HorizontalGameList> recentViews = createGameList("Most Recent", masterCollection.sortRecent(), () -> {
            filterBar.setSortBy(FilterBar.MOST_RECENT);
        });
        this.mostRecent = recentViews.getKey();
        this.recentList = recentViews.getValue();
        horzGameBox.getChildren().addAll(mostPopular, mostRecent);
    }

    private void initVertGameBox() {
        vertGameBox = new VBox();
        this.sortedGameList = new VerticalGameList(routeState);
        sortedGameList.getStyleClass().add("sort-list");
        vertGameBox.getChildren().add(sortedGameList);
    }

    private void initNoMatchesBox() {
        noMatchesBox = new VBox();
        noMatchesBox.setAlignment(Pos.CENTER);
        noMatchesBox.setMinHeight(300.0);
        Label noMatchText = new Label("No Matching Games... Try Removing Some Genres or Filters");
        noMatchText.getStyleClass().add("no-match-text");
        Button unfilterButton = new Button("Clear Filters");
        unfilterButton.getStyleClass().add("no-match-button");
        unfilterButton.setOnAction((evt) -> {
            filterBar.setGenres(null);
            filterBar.setFilters(null);
        });
        noMatchesBox.getChildren().addAll(noMatchText, unfilterButton);
    }

    public Store(GameCollection masterCollection, RouteState routeState, KeyManager keyManager) {
        super();
        this.masterCollection = masterCollection;
        this.routeState = routeState;
        this.keyManager = keyManager;

        this.getStylesheets().addAll("/styles/store.css");
        searchBar = new Search(masterCollection, routeState);
        searchHBox = new HBox();
        searchHBox.getStyleClass().add("search-h-box");
        searchHBox.setAlignment(Pos.CENTER_RIGHT);
        searchHBox.getChildren().add(searchBar);

        initNoMatchesBox();

        this.scroller = new ScrollPane();
        this.allLists = new VBox();
        this.sales = createSales("Need 4 Speed", masterCollection.getTitlesContainingString("Need for Speed").sortRecent());

        listBody = new VBox();
        initHorzGames();
        initVertGameBox();
        listBody.getChildren().add(horzGameBox);

        listPane = new BorderPane();
        listPane.setCenter(listBody);

        initFilterBar();
        listPane.setTop(filterBar);

        body = new VBox();
        body.getChildren().addAll(sales, listPane);

        this.scroller.setContent(body);
        this.scroller.setFitToWidth(true);

        //BorderPane is used to set visibility order while maintaining positioning: make sure you set center first, then top
        this.setCenter(scroller);
        this.setTop(searchHBox);

        //Controls the pagination of the scroll bar which uses a crazy measurement system
        routeState.subscribe((HashMap<String, Object> state) -> {
            String pageName = (String)state.get("page");
            if (pageName == AppRoot.STORE_PAGE_NAME) {
                if (keyManagerSubID == null) {
                    keyManagerSubID = keyManager.addListener(Arrays.asList(KeyCode.PAGE_UP, KeyCode.PAGE_DOWN, KeyCode.CLOSE_BRACKET, KeyCode.OPEN_BRACKET), (keyCode -> {
                        if (!showingSortList) {
                            if (keyCode == KeyCode.OPEN_BRACKET) {
                                scroller.setVvalue(scroller.getVmin());
                            }
                            if (keyCode == KeyCode.CLOSE_BRACKET) {
                                scroller.setVvalue(scroller.getVmax());
                            }
                        } else {
                            //Ensures pages are consistent and the top of the page shows a complete game
                            double scrollContentHeight = body.getHeight();
                            double aboveScrollH = sales.getHeight() + ((Region) listPane.getTop()).getHeight();

                            int scrollPixel = (int) (scroller.getVvalue() * (scrollContentHeight - scroller.getHeight()) - aboveScrollH);
                            if (keyCode == KeyCode.OPEN_BRACKET) {
                                if (scrollPixel <= SCROLL_BEHAVIOR_MARGIN) {
                                    scroller.setVvalue(0.0);
                                } else {
                                    int gameNum = (int) Math.ceil(scrollPixel / (sortedGameList.getHeight() / sortedGameList.getChildren().size()));
                                    if (gameNum % NUM_GAMES_SCROLL != 0) {
                                        gameNum = (gameNum / NUM_GAMES_SCROLL) * NUM_GAMES_SCROLL;
                                    } else {
                                        gameNum -= NUM_GAMES_SCROLL;
                                    }
                                    int gamePixel = (int) (gameNum * (sortedGameList.getHeight() / sortedGameList.getChildren().size()));
                                    scroller.setVvalue(((gamePixel + aboveScrollH) / (scrollContentHeight - scroller.getHeight())));
                                }
                            } else if (keyCode == KeyCode.CLOSE_BRACKET) {
                                if (scrollPixel < -SCROLL_BEHAVIOR_MARGIN) {
                                    scroller.setVvalue(aboveScrollH / (scrollContentHeight - scroller.getHeight()));
                                } else {
                                    int gameNum = (int) Math.ceil(scrollPixel / (sortedGameList.getHeight() / sortedGameList.getChildren().size()));
                                    gameNum = (gameNum / NUM_GAMES_SCROLL) * NUM_GAMES_SCROLL + NUM_GAMES_SCROLL;
                                    int gamePixel = (int) (gameNum * (sortedGameList.getHeight() / sortedGameList.getChildren().size()));
                                    scroller.setVvalue((gamePixel + aboveScrollH) / (scrollContentHeight - scroller.getHeight()));
                                }
                            }
                        }
                    }));
                }
            } else {
                keyManager.removeListener(keyManagerSubID);
                keyManagerSubID = null;
            }
        });
    }
}
