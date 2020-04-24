package origin;

import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Pair;
import origin.utils.RouteState;
import origin.views.*;

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
    public static final String STORE_PAGE_NAME = "Store";
    public static final String LIBRARY_PAGE_NAME = "Library";
    public static final String ACCESS_PAGE_NAME = "Access";
    public static final String PROFILE_PAGE_NAME = "Profile";
    public static final String GAME_PAGE_NAME = "Game";

    private HashMap<String, Node> createPages() {
        return new HashMap<>() {{
            put(STORE_PAGE_NAME, new Store());
            put(LIBRARY_PAGE_NAME, new Library());
            put(ACCESS_PAGE_NAME, new Access());
            put(PROFILE_PAGE_NAME, new Profile());
            put(GAME_PAGE_NAME, new GamePage());
        }};
    }

    private NavBar createNavBar() {
        return new NavBar(new ArrayList<>() {{
            add(STORE_PAGE_NAME);
            add(LIBRARY_PAGE_NAME);
            add(ACCESS_PAGE_NAME);
        }}, routeState);
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        //Set application to initial state
        routeState = new RouteState(new ArrayList<>() {{
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
        primaryStage.setScene(new Scene(borderPane, 1000, 950));

        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
