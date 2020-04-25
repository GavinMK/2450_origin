package origin.views;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Polygon;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class DropDownList extends VBox {
    private SelectionMode selectionMode;
    private Polygon dropTriangle;
    private Button extraButton;
    private VBox itemBox;
    private Consumer<List<String>> changeListener = null;
    private static final double DROP_TRIANGLE_W = 22.0;
    private static final double DROP_TRIANGLE_X_OFF = 40.0;
    private static final double DROP_TRIANGLE_Y_OFF = 5.0;
    //private HashSet<String> selectedItems = new HashSet<>();

    public Polygon createDropTriangle() {
        Polygon polygon = new Polygon();
        polygon.getPoints().addAll(DROP_TRIANGLE_W / 2.0, 0.0);
        polygon.getPoints().addAll(0.0, DROP_TRIANGLE_W / 2.0);
        polygon.getPoints().addAll(DROP_TRIANGLE_W, DROP_TRIANGLE_W / 2.0);
        polygon.setFill(Paint.valueOf("#2B2B2B"));
        polygon.setTranslateX(DROP_TRIANGLE_X_OFF);
        polygon.setTranslateY(DROP_TRIANGLE_Y_OFF);
        return polygon;
    }

     public Button createItemButton(String item) {
        Button button = new Button(item);
        button.getStyleClass().add("item-button");
        button.setMinWidth(this.getMinWidth());
        button.setOnAction((evt) -> {
            ArrayList<String> selectedItems = new ArrayList<>();
            selectedItems.add(item);
            changeListener.accept(selectedItems);
        });
        return button;
     }

    public void setExtra(String item) {
        if (item != null) {
            extraButton = createItemButton(item);
            extraButton.getStyleClass().add("extra-button");
            itemBox.getChildren().add(extraButton);
        } else if (extraButton != null) {
            itemBox.getChildren().remove(itemBox.getChildren().size() - 1);
            extraButton = null;
        }
    }

    public DropDownList(SelectionMode selectionMode) {
        super();
        this.getStylesheets().add("/styles/dropDownList.css");
        this.selectionMode = selectionMode;
        itemBox = new VBox();
        itemBox.getStyleClass().add("drop-down-list");
        dropTriangle = createDropTriangle();
        dropTriangle.setManaged(false);
        this.minWidthProperty().addListener((observer, prevW, width) -> {
            for (Node node: itemBox.getChildren()) {
                node.minWidth(width.doubleValue());
            }
        });
        itemBox.setTranslateY(DROP_TRIANGLE_Y_OFF + DROP_TRIANGLE_W / 2.0);
        this.getChildren().addAll(itemBox, dropTriangle);
    }

    public void setChangeListener(Consumer<List<String>> changeListener) {
        this.changeListener = changeListener;
    }

    public void setItems(List<String> items) {
        itemBox.getChildren().clear();
        for (String item: items) {
            if (selectionMode == SelectionMode.SINGLE) {
                Button btn = createItemButton(item);
                itemBox.getChildren().add(btn);
            }
        }
        if (extraButton != null) {
            itemBox.getChildren().add(extraButton);
        }
    }
}
