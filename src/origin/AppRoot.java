package origin;

import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Pair;
import origin.model.GameCollection;
import origin.utils.RouteState;
import origin.views.*;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;

/*
    Entry point of application, handles high-level routing and toolbars
 */
public class AppRoot extends Application {
    private RouteState routeState;
    private HashMap<String, Node> pages;
    private String currentPageName = null;
    private DarkDecoration darkDecoration;
    private NavBar navBar;
    private VBox body;
    private BorderPane borderPane;
    private GameCollection masterCollection;
    private KeyManager keyManager;
    public static final String STORE_PAGE_NAME = "Store";
    public static final String LIBRARY_PAGE_NAME = "Library";
    public static final String ACCESS_PAGE_NAME = "Access";
    public static final String PROFILE_PAGE_NAME = "Profile";
    public static final String GAME_PAGE_NAME = "Game";
    public static final String SEARCH_PAGE_NAME = "Search";
    public static final int UI_WIDTH = 1125;
    public static final int UI_HEIGHT = 950;

    private HashMap<String, Node> createPages() {
        return new HashMap<String, Node>() {{
            put(STORE_PAGE_NAME, new Store(masterCollection, routeState, keyManager));
            put(LIBRARY_PAGE_NAME, new Library());
            put(ACCESS_PAGE_NAME, new Access());
            put(PROFILE_PAGE_NAME, new Profile());
            put(GAME_PAGE_NAME, new GamePage(routeState));
            put(SEARCH_PAGE_NAME, new SearchPage(masterCollection, routeState, keyManager));
        }};
    }

    private NavBar createNavBar() {
        return new NavBar(new ArrayList<Pair<String, Runnable>>() {{
            add(new Pair<>(STORE_PAGE_NAME, () -> {
                routeState.pushState(new ArrayList<Pair<String, Object>>() {{
                    add(new Pair<>("page", STORE_PAGE_NAME));
                    addAll(FilterBar.GetClearState(AppRoot.STORE_PAGE_NAME));
                }});
            }));
            add(new Pair<>(LIBRARY_PAGE_NAME, () -> {
                routeState.pushState(new ArrayList<Pair<String, Object>>() {{
                    add(new Pair<>("page", LIBRARY_PAGE_NAME));
                }});
            }));
            add(new Pair<>(ACCESS_PAGE_NAME, () -> {
                routeState.pushState(new ArrayList<Pair<String, Object>>() {{
                    add(new Pair<>("page", ACCESS_PAGE_NAME));
                }});
            }));
        }}, routeState, keyManager);
    }

    @Override
    public void start(Stage primaryStage) {
        keyManager = new KeyManager();
        try {
            File gamesFile = new File("src/assets/games.csv");
            masterCollection = new GameCollection(gamesFile);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //Set application to initial state
        routeState = new RouteState(new ArrayList<Pair<String, Object>>() {{
            add(new Pair<>("page", "Store"));
        }});

        pages = createPages();
        darkDecoration = new DarkDecoration();
        navBar = createNavBar();
        body = new VBox();
        body.getChildren().add(this.navBar);

        //Top of borderpane is the decoration, center is the rest of the app
        borderPane = new BorderPane();
        borderPane.setTop(darkDecoration);
        borderPane.setCenter(body);
        borderPane.getStyleClass().add("border-pane");
        borderPane.getStylesheets().add("/styles/rootLayout.css");

        routeState.subscribe((HashMap<String, Object> state) -> {
            System.out.println("SubInvoked: " + (String)state.get("page"));
            String pageName = (String)state.get("page");
            if (pageName != currentPageName) {
                if (!pages.containsKey(pageName)) {
                    System.err.println("Attempted to change to nonexistant page: " + pageName);
                    return;
                }
                if (body.getChildren().size() > 1) {
                    body.getChildren().remove(1);
                }
                body.getChildren().add(pages.get(pageName));
            }
        });

        primaryStage.initStyle(StageStyle.UNDECORATED);
        Scene scene = new Scene(borderPane, UI_WIDTH, UI_HEIGHT);
        primaryStage.setScene(scene);
        scene.setOnKeyPressed((evt) -> {
            keyManager.trigger(evt.getCode());
        });

        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
