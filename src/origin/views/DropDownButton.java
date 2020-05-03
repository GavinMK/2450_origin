package origin.views;

import javafx.animation.RotateTransition;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;
import origin.utils.GuiHelper;

import java.io.File;
import java.util.List;
import java.util.function.Consumer;

/*
    A button , when pressed, shows a list where one or multiple options can be selected
    Creating a custom dropdown gives us more control over the styling
 */
public class DropDownButton extends VBox {
    private Consumer<List<String>> selectListener = null;
    private Runnable hideListener = null;
    private boolean showingList = false;
    private String name;
    private DropDownList dropDownList;
    private HBox dropButton;
    private Text label;
    private Label selectedText;
    private ImageView dropArrow;
    private HBox dropHBox;
    private RotateTransition dropArrowUpTransition;
    private RotateTransition dropArrowDownTransition;
    private SelectionMode selectionMode;

    private Text createLabel() {
        Text text = new Text(name);
        text.getStyleClass().add("label-text");
        return text;
    }

    private Label createSelectedText() {
        Label text = new Label();
        text.getStyleClass().add("selected-text");
        return text;
    }

    private ImageView createDropArrow() {
        File file = new File("src/assets/drop_arrow.png");
        ImageView imgView = new ImageView(file.toURI().toString());
        imgView.getStyleClass().add("drop-arrow");
        dropArrowUpTransition = new RotateTransition(Duration.millis(80), imgView);
        dropArrowUpTransition.setToAngle(180.0);
        dropArrowDownTransition = new RotateTransition(Duration.millis(80), imgView);
        dropArrowDownTransition.setToAngle(0.0);
        return imgView;
    }

    private void hideDropDown() {
        if (showingList) {
            dropArrowDownTransition.play();
            GuiHelper.SwapClasses(dropButton, "toggle-on", "toggle-off");
            dropDownList.setVisible(false);

            showingList = false;
            if (hideListener != null) {
                hideListener.run();
            }
        }
    }

    private void showDropDown() {
        if (!showingList) {
            dropArrowUpTransition.play();
            GuiHelper.SwapClasses(dropButton, "toggle-off", "toggle-on");
            dropDownList.setVisible(true);
            showingList = true;
        }
    }

    private void toggleDropDown() {
        if (showingList) {
            hideDropDown();
        } else {
            showDropDown();
        }
    }

    private void initDropButton() {
        label = createLabel();
        selectedText = createSelectedText();
        dropArrow = createDropArrow();
        dropButton = new HBox();
        dropButton.getStyleClass().add("drop-button");
        dropButton.setOnMouseClicked((evt) -> {
            toggleDropDown();
        });
        dropHBox = new HBox();
        dropHBox.getChildren().add(dropArrow);
        dropHBox.setAlignment(Pos.CENTER_RIGHT);
        dropButton.getChildren().addAll(label, selectedText, dropHBox);
        dropButton.setHgrow(dropHBox, Priority.ALWAYS);
        dropButton.setAlignment(Pos.CENTER_LEFT);
    }

    private DropDownList createDropDownList(List<String> items) {
        DropDownList dropDownList = new DropDownList(selectionMode, true);
        dropDownList.setItems(items);
        dropDownList.setManaged(false);
        dropDownList.setVisible(false);
        dropDownList.setChangeListener((List<String> selectedItems) ->  {
            if (selectedItems != null && (selectedItems.size() > 1 || (selectedItems.size() > 0 && items.contains(selectedItems.get(0))))) {
                label.setText(name + ": ");
                String selectedItemsStr = "";
                for (int i = 0; i < selectedItems.size(); i++) {
                    selectedItemsStr += selectedItems.get(i);
                    if (i < selectedItems.size() - 1) {
                        selectedItemsStr += ", ";
                    }
                }
                selectedText.setText(selectedItemsStr);
                if (selectionMode == SelectionMode.SINGLE) {
                    hideDropDown();
                }
            } else {
                label.setText(name);
                selectedText.setText("");
            }
            if (selectListener != null) {
                selectListener.accept(selectedItems);
            }
        });
        return dropDownList;
    }

    public DropDownButton(String name, List<String> items, SelectionMode selectionMode) {
        this.name = name;
        this.selectionMode = selectionMode;
        this.getStylesheets().add("/styles/dropDownButton.css");

        dropDownList = createDropDownList(items);
        initDropButton();

        dropDownList.setTranslateY(dropButton.getHeight());
        //Set dropDown to below searchBox (necessary since its position is not managed)
        dropButton.heightProperty().addListener((obs, prevH, height) -> {
            dropDownList.setTranslateY(height.doubleValue());
        });

        dropDownList.setMinWidth(dropButton.getWidth());
        //Set dropDown to width of the search box
        dropButton.widthProperty().addListener((obs, prevW, width) -> {
            dropDownList.setMinWidth(width.doubleValue());
        });

        this.getChildren().addAll(dropButton, dropDownList);

        //If clicked off of search box or drop down, remove focus from the field
        this.sceneProperty().addListener((obs, prevScene, scene) -> {
            if (scene != null) {
                scene.addEventFilter(MouseEvent.MOUSE_CLICKED, evt -> {
                    if (showingList) {
                        if (!GuiHelper.IsChild(this, evt.getPickResult().getIntersectedNode())) {
                            hideDropDown();
                        }
                    }
                });
            }
        });
    }

    public void setSelectedItems(List<String> selectedItems) {
        dropDownList.setSelectedItems(selectedItems);
    }

    public void setSelectListener(Consumer<List<String>> listener) {
        this.selectListener = listener;
        List<String> selectedItems = dropDownList.getSelectedItems();
        if (selectedItems != null) {
            listener.accept(selectedItems);
        }
    }

    public void setHideListener(Runnable listener) {
        this.hideListener = listener;
    }

    public void setExtra(String extra) {
        dropDownList.setExtra(extra);
    }
}
