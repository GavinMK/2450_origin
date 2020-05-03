package origin.views;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.util.Pair;
import origin.utils.GuiHelper;
import origin.utils.RouteState;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

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
    private boolean altHeld = false;

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

    private HashMap<String, Button> createPageButtons(List<Pair<String, Runnable>> pages) {
        HashMap<String, Button> pageButtons = new HashMap<>();
        for (Pair<String, Runnable> entry: pages) {
            String pageName = entry.getKey();
            Button pageButton = new Button(pageName);
            pageButton.getStyleClass().addAll("page-button", "nav-button");
            //Be super careful, lambda context may be saving pageName reference, not pageName value
            pageButton.setOnAction((evt) -> {
                entry.getValue().run();
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
                routeState.pushState(new ArrayList<Pair<String, Object>>() {{
                    add(new Pair<>("page", "profile"));
                }});
            } catch (Exception e) {
                System.out.println("Invalid PrevState: " + e);
            }
        });
        return button;
    }

    public NavBar(List<Pair<String, Runnable>> pages, RouteState routeState, KeyManager keyManager) {
        super();
        this.routeState = routeState;

        this.getStylesheets().add("/styles/navBar.css");
        this.getStyleClass().add("nav-bar");

        backButton = createBackButton();
        forwardButton = createForwardButton();
        pageButtons = createPageButtons(pages);
        profileButton = createProfileButton();

        leftBox = new HBox();
        leftBox.getChildren().addAll(backButton, forwardButton);
        //Add page buttons in order of array
        for (Pair<String, Runnable> entry: pages) {
            leftBox.getChildren().add(pageButtons.get(entry.getKey()));
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
        keyManager.addListener(Arrays.asList(KeyCode.LEFT, KeyCode.RIGHT, KeyCode.ALT), keyCode -> {
            if (keyCode == KeyCode.ALT) {
                altHeld = true;
            } else if (keyCode == KeyCode.LEFT && altHeld) {
                try {
                    if (routeState.hasPrevState()) {
                        routeState.toPrevState();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (keyCode == KeyCode.RIGHT && altHeld) {
                try {
                    if (routeState.hasNextState()) {
                        routeState.toNextState();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        sceneProperty().addListener((obj, prevScene, scene) -> {
            if (scene != null) {
                scene.setOnKeyReleased(e -> {
                    if (e.getCode() == KeyCode.ALT) {
                        altHeld = false;
                    }
                });
            }
        });
    }
}
