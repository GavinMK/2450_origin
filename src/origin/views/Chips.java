package origin.views;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

import java.util.List;

/*
    Displays a grid of non-interactable chips
 */
public class Chips extends GridPane {
    private int MAX_CHIP_ROWS = 3;
    private int MAX_CHIP_COLS = 3;
    private List<String> items;
    private Label createChip(String content) {
        Label label = new Label(content);
        label.getStyleClass().add("chip");
        return label;
    }

    public Chips(List<String> items) {
        super();
        this.getStylesheets().add("/styles/chips.css");
        this.items = items;
        this.setAlignment(Pos.CENTER_LEFT);
        populateGrid();
    }

    private void populateGrid() {
        this.getChildren().clear();
        int numCols = (int)Math.round(Math.sqrt(items.size()));
        numCols = (numCols > MAX_CHIP_COLS)? numCols: MAX_CHIP_COLS;

        this.setHgap(10);
        this.setVgap(10);

        int colI = 0;
        int rowI = 0;
        for (int i = 0; i < items.size(); i++) {
            this.add(createChip(items.get(i)), colI, rowI);
            colI++;
            if (colI >= numCols - 1) {
                rowI++;
                colI = 0;
            }
            if (rowI >= MAX_CHIP_ROWS) {
                break;
            }
        }
    }
}
