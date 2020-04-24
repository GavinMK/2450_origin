package origin;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import origin.views.*;

/*
    Entry point of application, handles high-level routing and toolbars
 */
public class AppRoot extends Application {
    private DarkDecoration darkDecoration;
    private VBox body;
    private BorderPane borderPane;

    @Override
    public void start(Stage primaryStage) throws Exception{
        //Set application to initial state
        darkDecoration = new DarkDecoration();
        body = new VBox();

        //Top of borderpane is the decoration, center is the rest of the app
        borderPane = new BorderPane();
        borderPane.setTop(darkDecoration);
        borderPane.setCenter(body);
        borderPane.getStyleClass().add("border-pane");
        borderPane.getStylesheets().add("/styles/rootLayout.css");

        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setScene(new Scene(borderPane, 1000, 950));

        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
