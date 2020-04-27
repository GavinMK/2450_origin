package origin.views;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.util.Pair;
import origin.AppRoot;
import origin.model.GameCollection;
import origin.model.GameData;
import origin.utils.RouteState;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

//TODO: This whole thing
public class Store extends BorderPane {
    private static final String POPULAR_KEY = "Most Popular";
    private static final String RECENT_KEY = "Most Recent";
    private static HashMap<String, Function<GameCollection, List<GameData>>> SORT_BYS = new HashMap<>() {{
        put(POPULAR_KEY, (collection) -> collection.sortDescendingPopular());
        put(RECENT_KEY, (collection) -> collection.sortRecent());
        put("Lowest Price", (collection) -> collection.sortPrice());
        put("Highest Price", (collection) -> {
            List<GameData> games = collection.sortPrice();
            Collections.reverse(games);
            return games;
        });
    }};

    private static List<String> GENRES = new ArrayList<>() {{
        add("Action");
        add("Adventure");
        add("Racing");
        add("Sports");
        add("Strategy");
    }};

    private static List<String> FILTERS = new ArrayList<>() {{
        add("Rated E");
        add("Rated T");
        add("Rated M");
        add("Single Player");
        add("Multiplayer");
    }};

    private GameCollection masterCollection;
    private GameCollection activeCollection;
    private List<String> activeFilters = null;
    private List<String> activeGenres = null;
    private String sortingBy = null;
    private RouteState routeState;
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
    private DropDownButton sortByDropButton;
    private DropDownButton genreDropButton;
    private DropDownButton filterDropButton;
    private HBox filterBar;
    private VBox horzGameBox;
    private VBox vertGameBox;
    private VBox listBody;
    private BorderPane listPane;
    private VBox noMatchesBox;

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
            //TODO make it so that the title isn't results for X and dont populate the search bar.
            this.routeState.pushState(new ArrayList<>() {{
                add(new Pair<>("page", AppRoot.SEARCH_PAGE_NAME));
                add(new Pair<>("search", title));
                add(new Pair<>("gameCollection", collection));
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

    private void updateLists(GameCollection collection, String sortingBy) {
        this.sortingBy = sortingBy;
        if(collection.games.isEmpty()) {
            if (!activeCollection.games.isEmpty()) {
                showNoMatches();
            }
            activeCollection = collection;
        } else {
            activeCollection = collection;
            if (sortingBy != null) {
                List<GameData> games = SORT_BYS.get(sortingBy).apply(collection);
                sortedGameList.setGames(games);
                showSortList();
            } else {
                popularList.setGames(collection.sortDescendingPopular());
                recentList.setGames(collection.sortRecent());
                showHorzLists();
            }
        }
    }

    private void pushListState() {
        routeState.pushState(new ArrayList<>() {{
            add (new Pair<>("storeActiveGenres", (activeGenres != null)? new ArrayList<>(activeGenres): null));
            add (new Pair<>("storeActiveFilters", (activeFilters != null)? new ArrayList<>(activeFilters): null));
            add (new Pair<>("storeSortBy", sortingBy));
        }});
    }

    private void showNoMatches() {
        listBody.getChildren().clear();
        listBody.getChildren().add(noMatchesBox);
        showingSortList = false;
        showingHorzList = false;
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

    private void initFilterBar() {
        filterBar = new HBox();
        filterBar.getStyleClass().add("filter-bar");
        sortByDropButton = new DropDownButton("Sort By", new ArrayList<>(SORT_BYS.keySet()), SelectionMode.SINGLE);
        sortByDropButton.getStyleClass().addAll("filter-button",  "filter-button-divide");
        sortByDropButton.setExtra("Clear");
        sortByDropButton.setMinWidth(150.0);
        genreDropButton = new DropDownButton("Genres", GENRES, SelectionMode.MULTIPLE);
        genreDropButton.getStyleClass().addAll("filter-button", "filter-button-divide");
        genreDropButton.setMinWidth(150.0);
        filterDropButton = new DropDownButton("Filters", FILTERS, SelectionMode.MULTIPLE);
        filterDropButton.getStyleClass().add("filter-button");
        Runnable onHide = () -> {
            pushListState();
        };

        sortByDropButton.setSelectListener((List<String> selectedItems) -> {
            if (selectedItems != null && selectedItems.size() > 0) {
                String item = selectedItems.get(0);
                if (SORT_BYS.containsKey(item)) {
                    if (!item.equals(sortingBy)) {
                        this.sortingBy = item;
                        pushListState();
                    }
                } else if (sortingBy != null) {
                    this.sortingBy = null;
                    pushListState();
                }
            }
        });

        genreDropButton.setSelectListener((List<String> selectedItems) -> {
            if (selectedItems != null && selectedItems.size() > 0) {
                activeGenres = selectedItems;
            } else {
                activeGenres = null;
            }
            updateLists(masterCollection.getMatchingGames(activeGenres, activeFilters), sortingBy);
        });
        genreDropButton.setHideListener(onHide);

        filterDropButton.setSelectListener((List<String> selectedItems) -> {
            if (selectedItems != null && selectedItems.size() > 0) {
                activeFilters = selectedItems;
            } else {
                activeFilters = null;
            }
            updateLists(masterCollection.getMatchingGames(activeGenres, activeFilters), sortingBy);
        });
        filterDropButton.setHideListener(onHide);

        List<Node> dropButtonList = Arrays.asList(sortByDropButton, genreDropButton, filterDropButton);
        for (int i = 0; i < dropButtonList.size(); i++) {
            HBox buttonWrapper = new HBox();
            buttonWrapper.getStyleClass().add("drop-button-wrapper");
            if (i < dropButtonList.size() - 1) {
                buttonWrapper.getStyleClass().add("drop-button-wrapper-divide");
            }
            Region region = new Region();
            buttonWrapper.getChildren().addAll(dropButtonList.get(i), region);
            buttonWrapper.setMaxWidth(220.0);
            buttonWrapper.setMinWidth(220.0);
            filterBar.getChildren().add(buttonWrapper);
        }
    }

    private void initHorzGames() {
        horzGameBox = new VBox();
        Pair<VBox, HorizontalGameList> popularViews = createGameList("Most Popular", masterCollection.sortDescendingPopular(), () -> {
            sortingBy = POPULAR_KEY;
            pushListState();
        });
        this.mostPopular = popularViews.getKey();
        this.popularList = popularViews.getValue();
        Pair<VBox, HorizontalGameList> recentViews = createGameList("Most Recent", masterCollection.sortRecent(), () -> {
            sortingBy = RECENT_KEY;
            pushListState();
        });
        this.mostRecent = recentViews.getKey();
        this.recentList = recentViews.getValue();
        horzGameBox.getChildren().addAll(mostPopular, mostRecent);
    }

    private void initVertGameBox() {
        vertGameBox = new VBox();
        this.sortedGameList = new VerticalGameList(routeState);
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
            activeGenres = null;
            activeFilters = null;
            pushListState();
        });
        noMatchesBox.getChildren().addAll(noMatchText, unfilterButton);
    }

    public Store(GameCollection masterCollection, RouteState routeState) {
        super();
        this.masterCollection = masterCollection;
        this.activeCollection = masterCollection;
        this.routeState = routeState;

        this.getStylesheets().addAll("/styles/store.css");
        searchBar = new Search(masterCollection, routeState);
        searchBar = new Search(masterCollection, this.routeState);
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
        initFilterBar();
        listBody.getChildren().add(horzGameBox);

        listPane = new BorderPane();
        listPane.setCenter(listBody);
        listPane.setTop(filterBar);

        body = new VBox();
        body.getChildren().addAll(sales, listPane);

        this.scroller.setContent(body);
        this.scroller.setFitToWidth(true);

        //BorderPane is used to set visibility order while maintaining positioning: make sure you set center first, then top
        this.setCenter(scroller);
        this.setTop(searchHBox);

        routeState.subscribe((state) -> {
            if (state.get("page") == AppRoot.STORE_PAGE_NAME) {
                if (state.containsKey("storeSortBy") && state.get("storeSortBy") != null) {
                    sortingBy = (String)state.get("storeSortBy");
                } else {
                    sortingBy = null;
                }
                if (state.containsKey("storeActiveGenres") && state.get("storeActiveGenres") != null) {
                    activeGenres = (List<String>)state.get("storeActiveGenres");
                } else {
                    activeGenres = null;
                }
                if (state.containsKey("storeActiveFilters") && state.get("storeActiveFilters") != null) {
                    activeFilters = (List<String>)state.get("storeActiveFilters");
                } else {
                    activeFilters = null;
                }
                this.sortByDropButton.setSelectedItems((sortingBy != null)? new ArrayList<>(){{ add(sortingBy); }}: null);
                this.genreDropButton.setSelectedItems(activeGenres);
                this.filterDropButton.setSelectedItems(activeFilters);
                updateLists(masterCollection.getMatchingGames(activeGenres, activeFilters), sortingBy);
            }
        });
    }
}
