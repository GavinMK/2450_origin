package origin.views;

import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.Pair;
import origin.utils.GuiHelper;
import origin.utils.RouteState;

import java.util.ArrayList;
import java.util.HashMap;

/*
    UI to traverse routeState, including arrows and pages
 */
public class NavBar extends HBox {
    private String activePageName = null;
    //In case we ever need to unsubscribe from routeState
    private String routeSubToken = null;
    private RouteState routeState;
    private HashMap<String, Button> pageButtons;
    private Button backButton;
    private Button forwardButton;
    private Button profileButton;
    private HBox leftBox;
    private HBox rightBox;

    private Button createBackButton() {
        Button button = new Button();
        button.getStyleClass().addAll("back-button", "nav-button");
        button.setOnAction((evt) -> {
            try {
                routeState.toPrevState();
            } catch (Exception e) {
                System.out.println("Invalid PrevState: " + e);
            }
        });
        return button;
    }

    private Button createForwardButton() {
        Button button = new Button();
        button.getStyleClass().addAll("forward-button", "nav-button");
        button.setOnAction((evt) -> {
            try {
                routeState.toNextState();
            } catch (Exception e) {
                System.out.println("Invalid PrevState: " + e);
            }
        });
        return button;
    }

    private HashMap<String, Button> createPageButtons(ArrayList<String> pageNames) {
        HashMap<String, Button> pageButtons = new HashMap<>();
        for (String pageName: pageNames) {
            Button pageButton = new Button(pageName);
            pageButton.getStyleClass().addAll("page-button", "nav-button");
            //Be super careful, lambda context may be saving pageName reference, not pageName value
            pageButton.setOnAction((evt) -> {
                System.out.println("Button clicked: " + pageName);
                routeState.pushState(new ArrayList<>() {{
                    add(new Pair<>("page", pageName));
                }});
            });
            pageButtons.put(pageName, pageButton);
        }
        return pageButtons;
    }

    private Button createProfileButton() {
        Button button = new Button();
        button.getStyleClass().addAll("profile-button", "nav-button");
        button.setOnAction((evt) -> {
            try {
                routeState.pushState(new ArrayList<>() {{
                    add(new Pair<>("page", "profile"));
                }});
            } catch (Exception e) {
                System.out.println("Invalid PrevState: " + e);
            }
        });
        return button;
    }

    public NavBar(ArrayList<String> pageNames, RouteState routeState) {
        super();
        this.routeState = routeState;

        this.getStylesheets().add("/styles/navBar.css");
        this.getStyleClass().add("nav-bar");

        backButton = createBackButton();
        forwardButton = createForwardButton();
        pageButtons = createPageButtons(pageNames);
        profileButton = createProfileButton();

        leftBox = new HBox();
        leftBox.getChildren().addAll(backButton, forwardButton);
        //Add page buttons in order of array
        for (String pageName: pageNames) {
            leftBox.getChildren().add(pageButtons.get(pageName));
        }

        rightBox = new HBox();
        rightBox.getChildren().addAll(profileButton);
        rightBox.setAlignment(Pos.CENTER_RIGHT);
        this.setHgrow(this.rightBox, Priority.ALWAYS);

        this.getChildren().addAll(leftBox, rightBox);
        routeSubToken = routeState.subscribe((HashMap<String, Object> state) -> {
            backButton.setDisable(!routeState.hasPrevState());
            forwardButton.setDisable(!routeState.hasNextState());

            if (state.containsKey("page")) {
                String pageName = (String)state.get("page");
                if (pageName != activePageName) {
                    //TODO: we can modify this to not change if pageButtons does not containKey (eg. go to game page from store page)
                    if (activePageName != null && pageButtons.containsKey(activePageName)) {
                        Button pageButton = pageButtons.get(activePageName);
                        GuiHelper.SwapClasses(pageButton, "active-page-button", "page-button");
                    }
                    if (pageButtons.containsKey(pageName)) {
                        Button pageButton = pageButtons.get(pageName);
                        GuiHelper.SwapClasses(pageButton, "page-button", "active-page-button");
                    }
                    activePageName = pageName;
                }
            }
        });
    }
}
