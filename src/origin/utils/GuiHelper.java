package origin.utils;

import javafx.scene.Node;

/*
    All the functions that help with the GUI
 */
public class GuiHelper {
    //Remove then add class to GUI element
    public static void SwapClasses(Node node, String removeClass, String addClass) {
        node.getStyleClass().removeIf((className) -> (className == removeClass));
        if (node.getStyleClass().indexOf(addClass) < 0) {
            node.getStyleClass().add(addClass);
        }
    }
}
