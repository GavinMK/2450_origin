package origin.views;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Button;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Pair;
import origin.AppRoot;
import origin.model.GameCollection;
import origin.model.GameData;
import origin.utils.GuiHelper;
import origin.utils.RouteState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Search extends VBox {
    private static final int ITEM_LIMIT = 6;

    private GameCollection gameCollection;
    private RouteState routeState;
    private HashMap<String, GameData> titlesToGameData = new HashMap<>();

    private boolean showingList = false;
    private Button searchIcon;
    private Button clearIcon;
    private TextField searchField;
    private HBox searchBox;
    private DropDownList dropDownList;

    private Button createSearchIcon() {
        Button searchIcon = new Button();
        searchIcon.getStyleClass().add("search-icon");
        searchIcon.setDisable(true);
        searchIcon.setOnAction((evt) -> {
            gotoSearchView();
        });
        return searchIcon;
    }

    private Button createClearIcon() {
        Button clearIcon = new Button();
        clearIcon.getStyleClass().add("clear-icon");
        clearIcon.setVisible(false);
        clearIcon.setOnAction((evt) -> {
            searchField.clear();
        });
        return clearIcon;
    }

    private TextField createSearchField() {
        TextField textField = new TextField();
        textField.getStyleClass().add("search-field");
        textField.setPromptText("Search Games");
        return textField;
    }

    private void gotoSearchView() {
        routeState.pushState(new ArrayList<>() {{
            add(new Pair<>("page", AppRoot.SEARCH_PAGE_NAME));
            add(new Pair<>("search", searchField.getText()));
        }});
    }

    public Search(GameCollection gameCollection, RouteState routeState) {
        super();
        this.getStylesheets().add("/styles/search.css");

        this.gameCollection = gameCollection;
        this.routeState = routeState;

        dropDownList = new DropDownList(SelectionMode.SINGLE);
        dropDownList.setChangeListener((List<String> selectedItems) ->  {
            if (selectedItems.size() > 0) {
                String gameTitle = selectedItems.get(0);
                if (titlesToGameData.containsKey(gameTitle)) {
                    GameData gameData = titlesToGameData.get(gameTitle);
                    routeState.pushState(new ArrayList<>() {{
                        add(new Pair<>("page", AppRoot.GAME_PAGE_NAME));
                        add(new Pair<>("gameData", gameData));
                    }});
                } else {
                    gotoSearchView();
                }
            }
        });

        searchIcon = createSearchIcon();
        searchField = createSearchField();
        clearIcon = createClearIcon();
        searchBox = new HBox();
        searchBox.getStyleClass().add("search-box");
        searchBox.getChildren().addAll(searchIcon, searchField, clearIcon);
        searchField.focusedProperty().addListener((observable, prevFocused, focused) -> {
            if (focused) {
                GuiHelper.SwapClasses(searchBox, "unfocused-box", "focused-box");
            } else {
                GuiHelper.SwapClasses(searchBox, "focused-box", "unfocused-box");
            }
            if (focused != prevFocused) {
                if (focused && searchField.getText().length() > 0 && !showingList) {
                    this.getChildren().add(dropDownList);
                    showingList = true;
                } else if (showingList) {
                    this.getChildren().remove(this.getChildren().size() - 1);
                    showingList = false;
                }
            }
        });
        searchField.textProperty().addListener((observable, prevText, text) -> {
            if (text != prevText) {
                searchIcon.setDisable(text.length() == 0);
                clearIcon.setVisible(text.length() > 0);
                if (text.length() > 0) {
                    GameCollection searchGameCollection = gameCollection.getTitlesContainingString(text);
                    List<GameData> searchGames = searchGameCollection.sortAlphabetical();
                    int numItems = (searchGames.size() < ITEM_LIMIT)? searchGames.size(): ITEM_LIMIT;
                    ArrayList<String> titles = new ArrayList<>();
                    titlesToGameData.clear();
                    for (int i = 0; i < numItems; i++) {
                        GameData game = searchGames.get(i);
                        titles.add(game.title);
                        titlesToGameData.put(game.title, game);
                    }
                    if (numItems < searchGames.size()) {
                        System.out.println("SEE MORE ADDED");
                        dropDownList.setExtra("See More");
                    } else {
                        dropDownList.setExtra(null);
                    }
                    if (numItems > 0) {
                        dropDownList.setItems(titles);
                    } else {
                        dropDownList.setItems(new ArrayList<>() {{
                            add("Search \"" + text + "\"");
                        }});
                    }

                    if (!showingList) {
                        this.getChildren().add(dropDownList);
                        showingList = true;
                    }
                } else if (showingList) {
                    this.getChildren().remove(this.getChildren().size() - 1);
                    showingList = false;
                }
            }
        });
        dropDownList.setManaged(false);
        searchBox.widthProperty().addListener((obs, prevW, width) -> {
            dropDownList.setMinWidth(width.doubleValue());
        });
        searchBox.heightProperty().addListener((obs, prevH, height) -> {
            dropDownList.setTranslateY(searchBox.getHeight());
        });
        dropDownList.setMaxHeight(100.0);
        this.sceneProperty().addListener((obs, prevScene, scene) -> {
            if (scene != null) {
                scene.addEventFilter(MouseEvent.MOUSE_CLICKED, evt -> {
                    if (searchField.isFocused()) {
                        if (!GuiHelper.IsChild(this, evt.getPickResult().getIntersectedNode())) {
                            searchBox.requestFocus();
                        }
                    }
                });
            }
        });

        this.getChildren().addAll(searchBox);
    }
}
