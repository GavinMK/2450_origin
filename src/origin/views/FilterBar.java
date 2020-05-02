package origin.views;

import javafx.scene.Node;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.util.Pair;
import origin.AppRoot;
import origin.model.GameCollection;
import origin.model.GameData;
import origin.utils.RouteState;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Filter;

public class FilterBar extends HBox {
    public static ArrayList<Pair<String, Object>> GetClearState(String routePrefix) {
        return new ArrayList<>() {{
            add(new Pair<>(routePrefix + "SortBy", null));
            add(new Pair<>(routePrefix + "ActiveGenres", null));
            add(new Pair<>(routePrefix + "ActiveFilters", null));
        }};
    }

    public static final String MOST_POPULAR = "Most Popular";
    public static final String MOST_RECENT = "Most Recent";
    public static final String LOWEST_PRICE = "Lowest Price";
    public static final String HIGHEST_PRICE = "Highest Price";
    public static final String SORT_BY_FIELD = "Sort By";
    public static final String GENRE_FIELD = "Genre";
    public static final String FILTER_FIELD = "Filter";
    public static final double DROP_BUTTON_W = 150.0;
    public static final double DROP_BUTTON_SPACING = 315.0;

    private GameCollection gameCollection;

    private String sortingBy = null;
    private List<String> activeGenres = null;
    private List<String> activeFilters = null;

    private DropDownButton sortByDropButton;
    private DropDownButton genreDropButton;
    private DropDownButton filterDropButton;

    private BiConsumer<GameCollection, String> collectionListener;
    private BiConsumer<List<GameData>, String> listListener;
    private Runnable hideListener;

    private String routePrefix;
    private RouteState routeState;
    private boolean disablePush = false;

    private static HashMap<String, Function<GameCollection, List<GameData>>> SORT_BYS = new HashMap<>() {{
        put(MOST_POPULAR, (collection) -> collection.sortDescendingPopular());
        put(MOST_RECENT, (collection) -> collection.sortRecent());
        put(LOWEST_PRICE, (collection) -> collection.sortPrice());
        put(HIGHEST_PRICE, (collection) -> {
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

    private void sendGamesToListeners(String changedField) {
        GameCollection matchingGameCollection = gameCollection.getMatchingGames(activeGenres, activeFilters);
        if (sortingBy != null) {
            List<GameData> games = SORT_BYS.get(sortingBy).apply(matchingGameCollection);
            if (listListener != null) {
                listListener.accept(games, changedField);
            }
        } else if (collectionListener != null) {
            collectionListener.accept(matchingGameCollection, changedField);
        }
    }

    private void pushState(String changedField) {
        if (this.routeState != null && !this.disablePush) {
            routeState.pushState(new ArrayList<>() {{
                add(new Pair<>(routePrefix + "ActiveGenres", (activeGenres != null) ? new ArrayList<>(activeGenres) : null));
                add(new Pair<>(routePrefix + "ActiveFilters", (activeFilters != null) ? new ArrayList<>(activeFilters) : null));
                add(new Pair<>(routePrefix + "SortBy", sortingBy));
            }});
        } else {
            sendGamesToListeners(changedField);
        }
    }

    public FilterBar(GameCollection gameCollection, boolean listOnly) {
        super();
        if (gameCollection != null) {
            this.gameCollection = gameCollection;
        } else {
            this.gameCollection = new GameCollection(new ArrayList<>());
        }

        this.getStylesheets().add("/styles/filterBar.css");
        this.getStyleClass().add("filter-bar");
        sortByDropButton = new DropDownButton("Sort By", new ArrayList<>(SORT_BYS.keySet()), SelectionMode.SINGLE);
        sortByDropButton.getStyleClass().addAll("filter-button",  "filter-button-divide");
        if (!listOnly) {
            sortByDropButton.setExtra("Clear");
        }
        sortByDropButton.setMinWidth(DROP_BUTTON_W);
        genreDropButton = new DropDownButton("Genres", GENRES, SelectionMode.MULTIPLE);
        genreDropButton.getStyleClass().addAll("filter-button", "filter-button-divide");
        genreDropButton.setMinWidth(DROP_BUTTON_W);
        filterDropButton = new DropDownButton("Filters", FILTERS, SelectionMode.MULTIPLE);
        filterDropButton.getStyleClass().add("filter-button");
        filterDropButton.setMinWidth(DROP_BUTTON_W);
        Runnable onHide = () -> {
            this.pushState(SORT_BY_FIELD);
            if (hideListener != null) {
                hideListener.run();
            }
        };

        sortByDropButton.setSelectListener((List<String> selectedItems) -> {
            if (selectedItems != null && selectedItems.size() > 0) {
                String item = selectedItems.get(0);
                if (SORT_BYS.containsKey(item)) {
                    if (!item.equals(sortingBy)) {
                        this.sortingBy = item;
                        pushState(SORT_BY_FIELD);
                    }
                } else if (sortingBy != null) {
                    this.sortingBy = null;
                    pushState(SORT_BY_FIELD);
                }
            }
        });

        genreDropButton.setSelectListener((List<String> selectedItems) -> {
            if (selectedItems != null && selectedItems.size() > 0) {
                activeGenres = selectedItems;
            } else {
                activeGenres = null;
            }
            sendGamesToListeners(GENRE_FIELD);
        });
        genreDropButton.setHideListener(onHide);

        filterDropButton.setSelectListener((List<String> selectedItems) -> {
            if (selectedItems != null && selectedItems.size() > 0) {
                activeFilters = selectedItems;
            } else {
                activeFilters = null;
            }
            sendGamesToListeners(FILTER_FIELD);
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
            buttonWrapper.setMaxWidth(DROP_BUTTON_SPACING);
            buttonWrapper.setMinWidth(DROP_BUTTON_SPACING);
            this.getChildren().add(buttonWrapper);
        }
    }

    public void linkWithRouteState(String pageName, RouteState routeState) {
        this.routeState = routeState;
        this.routePrefix = pageName;
        routeState.subscribe((state) -> {
            if (state.get("page") == pageName) {
                disablePush = true;
                if (state.containsKey(routePrefix + "SortBy") && state.get(routePrefix + "SortBy") != null) {
                    sortingBy = (String)state.get(routePrefix + "SortBy");
                } else {
                    sortingBy = null;
                }
                if (state.containsKey(routePrefix + "ActiveGenres") && state.get(routePrefix + "ActiveGenres") != null) {
                    activeGenres = (List<String>)state.get(routePrefix + "ActiveGenres");
                } else {
                    activeGenres = null;
                }
                if (state.containsKey(routePrefix + "ActiveFilters") && state.get(routePrefix + "ActiveFilters") != null) {
                    activeFilters = (List<String>)state.get(routePrefix + "ActiveFilters");
                } else {
                    activeFilters = null;
                }
                this.sortByDropButton.setSelectedItems((sortingBy != null)? new ArrayList<>(){{ add(sortingBy); }}: null);
                this.genreDropButton.setSelectedItems(activeGenres);
                this.filterDropButton.setSelectedItems(activeFilters);
                sendGamesToListeners(null);
                disablePush = false;
                //updateLists(masterCollection.getMatchingGames(activeGenres, activeFilters), sortingBy);
            }
        });
    }

    public void setCollectionListener(BiConsumer<GameCollection, String> listener) {
        this.collectionListener = listener;
    }

    public void setListListener(BiConsumer<List<GameData>, String> listener) {
        this.listListener = listener;
    }

    public void setHideListener(Runnable listener) {
        this.hideListener = listener;
    }

    public void setSortBy(String sortBy) {
        this.sortingBy = sortBy;
        this.sortByDropButton.setSelectedItems((sortingBy != null)? new ArrayList<>(){{ add(sortingBy); }}: null);
        pushState(SORT_BY_FIELD);
    }

    public void setGenres(List<String> genres) {
        this.activeGenres = genres;
        this.genreDropButton.setSelectedItems(activeGenres);
        pushState(GENRE_FIELD);
    }

    public void setFilters(List<String> filters) {
        this.activeFilters = filters;
        this.genreDropButton.setSelectedItems(filters);
        pushState(FILTER_FIELD);
    }

    public void setGameCollection(GameCollection collection) {
        this.gameCollection = collection;
        sendGamesToListeners(null);
    }
}
