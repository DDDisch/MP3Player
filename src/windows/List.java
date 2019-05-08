package windows;

import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import sample.Main;

import java.io.File;
import java.util.Vector;


public class List {

    Stage secondStage;
    VBox root;
    Scene scene;
    Vector<File> songList = new Vector<>();

    public List()
    {
        secondStage = new Stage();
        root = new VBox();
        scene = new Scene(root, 150,300);
        secondStage.setScene(scene);
        secondStage.setX(300);
        secondStage.setY(40);
        secondStage.initStyle(StageStyle.UNDECORATED);
        secondStage.show();

        final List.Delta dragDelta = new List.Delta();
        scene.setOnMousePressed(mouseEvent -> {
            dragDelta.x = secondStage.getX() - mouseEvent.getScreenX();
            dragDelta.y = secondStage.getY() - mouseEvent.getScreenY();
        });
        scene.setOnMouseDragged(mouseEvent -> {
            secondStage.setX(mouseEvent.getScreenX() + dragDelta.x);
            secondStage.setY(mouseEvent.getScreenY() + dragDelta.y);
        });

        secondStage.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            if(e.getCode() == KeyCode.ESCAPE) {
                Platform.exit();
            }
        });

    }

    public void addListItem(File file)
    {
        ListItem l = new ListItem(file.getName());
        l.setFile(file);
        songList.add(file);
        root.getChildren().add(l);
    }

    public Vector getListItemVector()
    {
        return songList;
    }

    class Delta { double x, y; }
}
