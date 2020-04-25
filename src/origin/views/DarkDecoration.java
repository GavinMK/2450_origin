package origin.views;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.util.ArrayList;

/*
    JavaFX doesn't support changing the color of the decoration (the frame of a window),
    so we just implement the whole frame ourselves...
 */
public class DarkDecoration extends HBox {
    private Button logoButton;
    private MenuBar menuBar;
    private Button exitButton;
    private Button maximizeButton;
    private Button minimizeButton;
    private HBox leftBox;
    private HBox rightBox;
    private double x;
    private double y;
    private double windowW;
    private double windowH;
    private boolean fullscreen = false;

    //Specifies menu names and their items
    private static final ArrayList<Pair<String, ArrayList<String>>> MENU_BAR_CONTENT = new ArrayList<>() {{
        add(new Pair<>("Origin", new ArrayList<>() {{
            add("Settings");
            add("Account & Billing");
            add("Order History");
            add("Redeem Product Code");
            add("Reload Page");
            add("Go Offline");
            add("Sign Out");
            add("Exit");
        }}));
        add(new Pair<>("View", new ArrayList<>() {{
            add("Store");
            add("Library");
            add("Friends List");
        }}));
        add(new Pair<>("Friends", new ArrayList<>() {{
            add("View Friends List");
            add("Add a Friend");
        }}));
        add(new Pair<>("Games", new ArrayList<>() {{
            add("View Games");
            add("Add Non-Origin Game");
            add("Redeem A Code");
        }}));
    }};

    private Button createLogoButton() {
        Button button = new Button();
        button.getStyleClass().add("logo-button");
        return button;
    }

    //Create Menu UI elements from MENU_BAR_CONTENT
    private MenuBar createMenuBar() {
        MenuBar menuBar = new MenuBar();
        menuBar.getStyleClass().add("menu-bar");
        for (Pair<String, ArrayList<String>> menuContent: MENU_BAR_CONTENT) {
            Menu menu = new Menu(menuContent.getKey());
            menu.getStyleClass().add("menu");
            for (String menuItemContent: menuContent.getValue()) {
                MenuItem menuItem = new MenuItem(menuItemContent);
                menuItem.getStyleClass().add("menu-item");
                menu.getItems().add(menuItem);
            }
            menuBar.getMenus().add(menu);
        }
        return menuBar;
    }

    private Button createMinimizeButton() {
        Button button = new Button();
        button.getStyleClass().addAll("minimize-button", "toolbar-button");
        button.setOnAction((evt) -> {
            Stage stage = (Stage)((Node)evt.getSource()).getScene().getWindow();
            stage.setIconified(true);
        });
        return button;
    }

    private Button createMaximizeButton() {
        Button button = new Button();
        button.getStyleClass().addAll("maximize-button", "toolbar-button");
        button.setOnAction((evt) -> {
            Stage stage = (Stage)((Node)evt.getSource()).getScene().getWindow();
            if (!fullscreen) {
                goFullscreen(stage);
            } else {
                goWindowed(stage);
            }
        });
        return button;
    }

    private void goFullscreen(Stage stage) {
        windowW = stage.getWidth();
        windowH = stage.getHeight();
        fullscreen = true;
        maximizeButton.getStyleClass().removeIf((className) -> (className == "maximize-button"));
        maximizeButton.getStyleClass().add("window-button");
        stage.setFullScreenExitHint("");
        stage.setFullScreen(true);
    }

    private void goWindowed(Stage stage) {
        stage.setWidth(windowW);
        stage.setHeight(windowH);
        fullscreen = false;
        maximizeButton.getStyleClass().removeIf((className) -> (className == "window-button"));
        maximizeButton.getStyleClass().add("maximize-button");
        stage.setFullScreen(false);
    }

    private Button createExitButton() {
        Button button = new Button();
        button.getStyleClass().addAll("exit-button", "toolbar-button");
        button.setOnAction((evt) -> {
            Stage stage = (Stage)((Node)evt.getSource()).getScene().getWindow();
            stage.close();
        });
        return button;
    }

    public DarkDecoration() {
        super();
        this.getStylesheets().add("/styles/darkToolbar.css");
        this.getStyleClass().add("toolbar");
        logoButton = createLogoButton();
        menuBar = createMenuBar();
        minimizeButton = createMinimizeButton();
        maximizeButton = createMaximizeButton();
        exitButton = createExitButton();
        leftBox = new HBox();
        leftBox.getChildren().addAll(logoButton, menuBar);
        rightBox = new HBox();
        rightBox.getChildren().addAll(minimizeButton, maximizeButton, exitButton);
        rightBox.setAlignment(Pos.CENTER_RIGHT);
        //The rightBox will always fill the remaining horizontal space, this enforces our right-align
        this.setHgrow(this.rightBox, Priority.ALWAYS);
        this.getChildren().addAll(leftBox, rightBox);
        this.setOnMouseDragged((MouseEvent evt) -> {
            Stage stage = (Stage)((Node)evt.getSource()).getScene().getWindow();
            stage.setX(evt.getScreenX() - x);
            stage.setY(evt.getScreenY() - y);
            //On a fullscreen drag, we expect the app to go windowed
            // so go windowed and transform for different dimensions
            if (this.fullscreen) {
                double widthPercent = (x / stage.getWidth());
                x = this.windowW * widthPercent;
                this.goWindowed(stage);
            }
        });
        this.setOnMousePressed((MouseEvent evt) -> {
            x = evt.getSceneX();
            y = evt.getSceneY();
        });
    }
}
