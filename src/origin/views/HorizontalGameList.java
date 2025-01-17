package origin.views;

import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import origin.model.GameData;
import origin.utils.RouteState;

import java.util.ArrayList;
import java.util.List;

/**
 * Side scrolling list of games.
 */
public class HorizontalGameList extends HBox {
    private static int MAX_BOX_COUNT = 4;
    private List<GameData> games;
    private ArrayList<TallGame> gameBoxes;
    private Button leftButton;
    private Button rightButton;
    private RouteState routeState;
    private ImageView specialFrame;
    private String specialFrameUri;
    private boolean isSpecialFrame;

    private int boxCount;

    private HBox visibleGames;
    private int leftIterator;

    private TallGame createTallGame(GameData game) {
        TallGame gameBox = new TallGame(game, this.routeState);
        return gameBox;
    }

    private Button createLeftButton() {
        Button button = new Button();
        button.getStyleClass().addAll("left-button", "scroll-button");
        button.setFocusTraversable(false);
        button.setOnAction((evt) -> {
            if(this.leftIterator > 0) {
                this.negativeShiftVisible();
                this.leftIterator -= 1;
                if (this.leftIterator == 0 && this.specialFrame == null) {
                    this.leftButton.setDisable(true);
                }
            }
            else if (this.specialFrame != null){
                this.enableSpecialFrame();
                if(this.rightButton.isDisabled()) this.rightButton.setDisable(false);
            }
        });
        button.setDisable(true);
        return button;
    }

    private Button createRightButton() {
        Button button = new Button();
        button.getStyleClass().addAll("right-button", "scroll-button");
        button.setFocusTraversable(false);
        button.setOnAction((evt) -> {
            if (this.leftIterator == 0 && this.specialFrame != null && this.isSpecialFrame) {
                this.disableSpecialFrame();
            }
            else if(this.getRightIterator() < this.gameBoxes.size() - 1) {
                this.positiveShiftVisible();
                this.leftIterator += 1;
            }
            if (this.getRightIterator() == this.gameBoxes.size() - 1) {
                this.rightButton.setDisable(true);
            }
        });
        return button;
    }

    private void enableSpecialFrame() {
        this.getChildren().remove(this.leftButton);
        this.getChildren().add(0, this.specialFrame);
        this.isSpecialFrame = true;
        this.boxCount = getMaxBoxCount();
        if (this.visibleGames.getChildren().size() >= 4) {
            this.visibleGames.getChildren().remove(3);
        }
        if (this.visibleGames.getChildren().size() >= 3) {
            this.visibleGames.getChildren().remove(2);
        }
        //this.visibleGames.getChildren().remove(1);
    }

    private void disableSpecialFrame() {
        this.getChildren().remove(0);
        this.getChildren().add(0, this.leftButton);
        this.isSpecialFrame = false;
        this.boxCount = getMaxBoxCount();
        this.leftButton.setDisable(false);
        //this.visibleGames.getChildren().add(1, this.gameBoxes.get(1));
        if (this.gameBoxes.size() >= 3) {
            this.visibleGames.getChildren().add(2, this.gameBoxes.get(2));
        }
        if (this.gameBoxes.size() >= 4) {
            this.visibleGames.getChildren().add(3, this.gameBoxes.get(3));
        }
    }

    private void populateVisibleGames() {
        for(int i = this.leftIterator; i < this.boxCount; i++){
            if(this.gameBoxes.get(i) != null) {
                visibleGames.getChildren().add(this.gameBoxes.get(i));
            }
            else break;
        }
        visibleGames.setSpacing(20);
    }

    private int getRightIterator() {
        return this.leftIterator + this.boxCount - 1;
    }

    private void positiveShiftVisible() {
        this.visibleGames.getChildren().remove(0);
        this.visibleGames.getChildren().add(this.gameBoxes.get(this.getRightIterator() + 1));
        if (this.leftButton.isDisabled()) this.leftButton.setDisable(false);
    }

    private void negativeShiftVisible() {
        this.visibleGames.getChildren().remove(this.visibleGames.getChildren().size() - 1);
        this.visibleGames.getChildren().add(0, this.gameBoxes.get(this.leftIterator - 1));
        if(this.rightButton.isDisabled()) this.rightButton.setDisable(false);
    }

    public HorizontalGameList(List<GameData> games, RouteState routeState, String specialFrameUri) {
        super();
        this.routeState = routeState;
        this.specialFrameUri = specialFrameUri;
        this.leftIterator = 0;
        this.boxCount = MAX_BOX_COUNT;
        this.leftButton = this.createLeftButton();
        this.rightButton = this.createRightButton();
        this.visibleGames = new HBox();
        this.gameBoxes = new ArrayList<>();

        if (this.specialFrameUri != null) {
            this.specialFrame = new ImageView(new Image(this.specialFrameUri));
            this.specialFrame.getStyleClass().add("special-frame");
        }

        if (this.specialFrameUri != null) {
            this.isSpecialFrame = true;
        }
        setGames(games);

        this.getStylesheets().add("/styles/horiz-gameList.css");
        this.getChildren().addAll(this.leftButton, this.visibleGames, this.rightButton);
        if (this.specialFrame != null) {
            this.enableSpecialFrame();
        }
    }

    private int getMaxBoxCount() {
        return (isSpecialFrame)? (MAX_BOX_COUNT / 2): MAX_BOX_COUNT;
    }

    public void setGames(List<GameData> games) {
        if (this.games == null || !this.games.equals(games)) {
            this.games = games;
            this.gameBoxes.clear();
            this.visibleGames.getChildren().clear();
            this.leftIterator = 0;
            if (games.size() <= getMaxBoxCount()) {
                this.leftButton.setVisible(false);
                this.rightButton.setVisible(false);
            } else {
                this.leftButton.setVisible(true);
                this.rightButton.setVisible(true);
            }
            boxCount = (games.size() < getMaxBoxCount()) ? games.size() : getMaxBoxCount();
            for (GameData game : games) {
                this.gameBoxes.add(this.createTallGame(game));
            }
            this.populateVisibleGames();
        }
    }
}
