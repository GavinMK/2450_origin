package origin.views;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import origin.model.GameData;
import origin.utils.RouteState;

import java.util.ArrayList;
import java.util.List;

/**
 * Side scrolling list of games.
 */
public class HorizontalGameList extends HBox {

    private List<GameData> games;
    private ArrayList<TallGame> gameBoxes;
    private Button leftButton;
    private Button rightButton;
    private RouteState routeState;

    //TODO some way to calculate this based on width of the window??
    private int boxCount;

    private HBox visibleGames;
    private int leftIterator;

    private TallGame createTallGame(GameData game) {
        TallGame gameBox = new TallGame(game, this.routeState);
        //TODO link to game page
        return gameBox;
    }

    private Button createLeftButton() {
        Button button = new Button();
        button.getStyleClass().addAll("left-button", "scroll-button");
        button.setOnAction((evt) -> {
            if(this.leftIterator > 0) {
                this.negativeShiftVisible();
                this.leftIterator -= 1;
                if (this.leftIterator == 0) {
                    this.leftButton.setDisable(true);
                }
            }
        });
        button.setDisable(true);
        return button;
    }

    private Button createRightButton() {
        Button button = new Button();
        button.getStyleClass().addAll("right-button", "scroll-button");
        button.setOnAction((evt) -> {
            if(this.getRightIterator() < this.gameBoxes.size() - 1) {
                this.positiveShiftVisible();
                this.leftIterator += 1;
                if (this.getRightIterator() == this.gameBoxes.size() - 1) {
                    this.rightButton.setDisable(true);
                }
            }
        });
        return button;
    }

    private HBox createVisibleGames() {
        HBox box = new HBox();
        for(int i = this.leftIterator; i < this.boxCount; i++){
            if(this.gameBoxes.get(i) != null) {
                box.getChildren().add(this.gameBoxes.get(i));
            }
            else break;
        }
        box.setSpacing(20);
        return box;
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

    public HorizontalGameList(List<GameData> games, RouteState routeState) {
        super();
        this.games = games;
        this.routeState = routeState;
        this.leftIterator = 0;
        this.boxCount = 4;
        this.leftButton = this.createLeftButton();
        this.rightButton = this.createRightButton();
        this.visibleGames = new HBox();
        this.gameBoxes = new ArrayList<>();
        for(GameData game: games) {
            this.gameBoxes.add(this.createTallGame(game));
        }
        this.visibleGames = this.createVisibleGames();

        this.getStylesheets().add("/styles/gameList.css");

        this.getChildren().addAll(this.leftButton, this.visibleGames, this.rightButton);
        this.setAlignment(Pos.CENTER);
    }

}
