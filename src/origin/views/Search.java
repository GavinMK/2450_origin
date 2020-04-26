package origin.views;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Button;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
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

/*
    An autocompleted search-bar for games
 */
public class Search extends VBox {
    private static final int ITEM_LIMIT = 6;

    private GameCollection masterCollection;
    private GameCollection searchCollection = null;
    private RouteState routeState;
    private HashMap<String, GameData> titlesToGameData = new HashMap<>();

    private boolean showingList = false;
    private Button searchIcon;
    private Button clearIcon;
    private TextField searchField;
    private HBox searchBox;
    private DropDownList dropDownList;

    private void gotoGamePage(GameData gameData) {
        routeState.pushState(new ArrayList<>() {{
            add(new Pair<>("page", AppRoot.GAME_PAGE_NAME));
            add(new Pair<>("gameData", gameData));
        }});
    }

    private void gotoSearchPage() {
        if (searchField.getText().length() > 0) {
            routeState.pushState(new ArrayList<>() {{
                add(new Pair<>("page", AppRoot.SEARCH_PAGE_NAME));
                add(new Pair<>("search", searchField.getText()));
                add(new Pair<>("gameCollection", searchCollection));
            }});
        }
    }

    private DropDownList createDropDownList() {
        DropDownList dropDownList = new DropDownList(SelectionMode.SINGLE, false);
        dropDownList.showDropTriangle();
        dropDownList.setManaged(false);
        dropDownList.setChangeListener((List<String> selectedItems) ->  {
            if (selectedItems.size() > 0) {
                String gameTitle = selectedItems.get(0);
                if (titlesToGameData.containsKey(gameTitle)) {
                    GameData gameData = titlesToGameData.get(gameTitle);
                    gotoGamePage(gameData);
                } else {
                    gotoSearchPage();
                }
            }
        });
        return dropDownList;
    }

    private Button createSearchIcon() {
        Button searchIcon = new Button();
        searchIcon.getStyleClass().add("search-icon");
        searchIcon.setDisable(true);
        //When search icon is pressed, search
        searchIcon.setOnAction((evt) -> {
            gotoSearchPage();
        });
        return searchIcon;
    }

    private Button createClearIcon() {
        Button clearIcon = new Button();
        clearIcon.getStyleClass().add("clear-icon");
        clearIcon.setVisible(false);
        //Clear text on press
        clearIcon.setOnAction((evt) -> {
            searchField.clear();
        });
        return clearIcon;
    }

    private TextField createSearchField() {
        TextField textField = new TextField();
        textField.getStyleClass().add("search-field");
        textField.setPromptText("Search Games");
        //On enter, search
        textField.setOnKeyPressed((evt) -> {
            if (evt.getCode() == KeyCode.ENTER) {
                gotoSearchPage();
            }
        });
        return textField;
    }

    //Create region containing the search field and icons
    private void initSearchBox() {
        searchIcon = createSearchIcon();
        searchField = createSearchField();
        clearIcon = createClearIcon();
        searchBox = new HBox();
        searchBox.getStyleClass().add("search-box");
        searchBox.getChildren().addAll(searchIcon, searchField, clearIcon);
        searchField.focusedProperty().addListener((observable, prevFocused, focused) -> {
            //When searchField in focus, add white outline to box
            if (focused) {
                GuiHelper.SwapClasses(searchBox, "unfocused-box", "focused-box");
            } else {
                GuiHelper.SwapClasses(searchBox, "focused-box", "unfocused-box");
            }
            //When searchField in focus & has text, show dropdown
            if (focused && searchField.getText().length() > 0 && !showingList) {
                this.getChildren().add(dropDownList);
                showingList = true;
            } else if (showingList) {
                //Remove dropdown list if no longer in focus
                this.getChildren().remove(this.getChildren().size() - 1);
                showingList = false;
            }
        });

        searchField.textProperty().addListener((observable, prevText, text) -> {
            searchIcon.setDisable(text.length() == 0);
            clearIcon.setVisible(text.length() > 0);
            if (text.length() > 0) {
                //Update search results
                searchCollection = masterCollection.getTitlesContainingString(text);
                List<GameData> searchGames = searchCollection.sortAlphabetical();
                //# of games titles to display
                int numItems = (searchGames.size() < ITEM_LIMIT)? searchGames.size(): ITEM_LIMIT;
                ArrayList<String> titles = new ArrayList<>();  //Array of displayed game titles
                titlesToGameData.clear();
                for (int i = 0; i < numItems; i++) {
                    GameData game = searchGames.get(i);
                    titles.add(game.title);
                    //game titles mapped to data for access in dropdown handler
                    titlesToGameData.put(game.title, game);
                }
                //If not showing all the games, add a See More option to the dropdown
                if (numItems < searchGames.size()) {
                    dropDownList.setExtra("See More");
                } else {
                    dropDownList.setExtra(null);
                }
                //If no searchItems, add a item that will act as a search button
                if (numItems > 0) {
                    dropDownList.setItems(titles);
                } else {
                    dropDownList.setItems(new ArrayList<>() {{
                        add("Search \"" + text + "\"");
                    }});
                }

                //Show the dropdown list
                if (!showingList && searchField.isFocused()) {
                    this.getChildren().add(dropDownList);
                    showingList = true;
                }
            } else if (showingList) {
                //Remove the dropdown list if no text
                this.getChildren().remove(this.getChildren().size() - 1);
                showingList = false;
            }
        });

        //If clicked off of search box or drop down, remove focus from the field
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
    }

    public Search(GameCollection gameCollection, RouteState routeState) {
        super();
        this.getStylesheets().add("/styles/search.css");

        this.masterCollection = gameCollection;
        this.routeState = routeState;

        dropDownList = createDropDownList();
        initSearchBox();

        dropDownList.setMinWidth(searchBox.getWidth());
        dropDownList.setTranslateY(searchBox.getHeight());
        //Set dropDown to width of the search box
        searchBox.widthProperty().addListener((obs, prevW, width) -> {
            dropDownList.setMinWidth(width.doubleValue());
        });
        //Set dropDown to below searchBox (necessary since its position is not managed)
        searchBox.heightProperty().addListener((obs, prevH, height) -> {
            dropDownList.setTranslateY(height.doubleValue());
        });

        this.getChildren().addAll(searchBox);
    }

    public void setSearch(String str) {
        searchField.setText(str);
    }
}
