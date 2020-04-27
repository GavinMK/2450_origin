package origin.views;

import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Polygon;
import origin.AppRoot;
import origin.utils.GuiHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/*
    DropDown list of checkboxes (if multi-select) or buttons (if single-select)
 */
public class DropDownList extends VBox {
    private SelectionMode selectionMode;
    private boolean persistentSelection;
    private Polygon dropTriangle;
    private Button extraButton;
    private VBox itemBox;
    private Consumer<List<String>> changeListener = null;
    private static final double DROP_TRIANGLE_W = 22.0;
    private static final double DROP_TRIANGLE_X_OFF = 40.0;
    private static final double DROP_TRIANGLE_Y_OFF = 5.0;
    private String selectedItem = null;
    private HashMap<String, CheckBox> checkBoxes;

    //Triangle between field and the dropDown box
    private Polygon createDropTriangle() {
        Polygon polygon = new Polygon();
        polygon.getPoints().addAll(DROP_TRIANGLE_W / 2.0, 0.0);
        polygon.getPoints().addAll(0.0, DROP_TRIANGLE_W / 2.0);
        polygon.getPoints().addAll(DROP_TRIANGLE_W, DROP_TRIANGLE_W / 2.0);
        polygon.setFill(Paint.valueOf("#2B2B2B"));
        polygon.setTranslateX(DROP_TRIANGLE_X_OFF);
        polygon.setTranslateY(DROP_TRIANGLE_Y_OFF);
        return polygon;
    }

    //Only for single selection mode
    private void selectItem(String item) {
        if (persistentSelection) {
            selectedItem = item;
        }
        if (changeListener != null) {
            changeListener.accept(new ArrayList<>() {{
                add(item);
            }});
        }
    }

    //Button for each item in the dropDown
     public Button createItemButton(String item) {
        Button button = new Button(item);
        button.getStyleClass().add("item-button");
        button.setMinWidth(this.getMinWidth());
        button.setOnAction((evt) -> {
            selectItem(item);
        });
        return button;
     }

     public CheckBox createItemCheckBox(String item) {
         CheckBox checkBox = new CheckBox(item);
         checkBox.getStyleClass().add("item-check-box");
         checkBox.setMinWidth(this.getMinWidth());
         checkBox.selectedProperty().addListener((obs, prevCheck, checked) -> {
            if (changeListener != null) {
                changeListener.accept(getSelectedItems());
            }
         });
         return checkBox;
     }

     //A button representing extra functionality in the list (clearly divided from items)
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

    public DropDownList(SelectionMode selectionMode, boolean persistentSelection) {
        super();
        this.selectionMode = selectionMode;
        this.persistentSelection = persistentSelection;
        this.getStylesheets().add("/styles/dropDownList.css");
        itemBox = new VBox();
        itemBox.getStyleClass().add("drop-down-list");
        this.getChildren().addAll(itemBox);
        this.minWidthProperty().addListener((observer, prevW, width) -> {
            itemBox.setMinWidth(width.doubleValue());
            for (Node node: itemBox.getChildren()) {
                ((Region)node).setMinWidth(width.doubleValue());
            }
        });
    }

    public void showDropTriangle() {
        dropTriangle = createDropTriangle();
        dropTriangle.setManaged(false);
        itemBox.setTranslateY(DROP_TRIANGLE_Y_OFF + DROP_TRIANGLE_W / 2.0 - 1);
        this.getChildren().add(dropTriangle);
    }

    //Listens to changes in selected items
    public void setChangeListener(Consumer<List<String>> changeListener) {
        this.changeListener = changeListener;
        List<String> selectedItems = getSelectedItems();
        if (changeListener != null && selectedItems != null) {
            changeListener.accept(selectedItems);
        }
    }

    //Set the items shown in the dropbox
    public void setItems(List<String> items) {
        itemBox.getChildren().clear();
        checkBoxes = new HashMap<>();
        for (String item: items) {
            if (selectionMode == SelectionMode.SINGLE) {
                Button btn = createItemButton(item);
                itemBox.getChildren().add(btn);
            } else if (selectionMode == SelectionMode.MULTIPLE) {
                CheckBox checkBox = createItemCheckBox(item);
                checkBoxes.put(item, checkBox);
                itemBox.getChildren().add(checkBox);
            }
        }
        if (extraButton != null) {
            itemBox.getChildren().add(extraButton);
        }
    }

    public void setSelectedItems(List<String> selectedItems) {
        if (selectionMode == SelectionMode.SINGLE) {
            if (selectedItems == null || selectedItems.size() == 0) {
                selectItem(null);
            } else if (selectedItems.size() == 1) {
                selectItem(selectedItems.get(0));
            } else {
                System.err.println("Passed multiple selections to DropDownList on single selection mode");
            }
        } else {
            for (Map.Entry<String, CheckBox> entry : checkBoxes.entrySet()) {
                entry.getValue().setSelected((selectedItems != null)? selectedItems.contains(entry.getKey()): false);
            }
        }
    }

    public List<String> getSelectedItems() {
        if (selectionMode == SelectionMode.SINGLE && selectedItem != null) {
            return new ArrayList<>(){{
                add(selectedItem);
            }};
        } else if (selectionMode == SelectionMode.MULTIPLE) {
            ArrayList<String> selectedItems = new ArrayList<>();
            for (Map.Entry<String, CheckBox> entry: checkBoxes.entrySet()) {
                if (entry.getValue().isSelected()) {
                    selectedItems.add(entry.getKey());
                }
            }
            if (selectedItems.isEmpty()) {
                return null;
            }
            return selectedItems;
        }
        return null;
    }
}
