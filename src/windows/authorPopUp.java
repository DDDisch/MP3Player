package windows;

import javafx.geometry.HPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.awt.*;
import java.util.ArrayList;

public class authorPopUp {
    private static ArrayList<String> apiArr;

    public static void authorWindow(String api) {
        ArrayList<String> apiArr = last.fm.filter.filterArrray.createArr(api);

        Stage stage = new Stage();
        stage.initStyle(StageStyle.UNDECORATED);
        GridPane root = new GridPane();
        ImageView iv = new ImageView();
        Label artist = new Label(last.fm.filter.filterArrray.filter(0, "title", apiArr));

        stage.setScene(new Scene(root, 300, Toolkit.getDefaultToolkit().getScreenSize().getHeight()/2));
        stage.show();
        stage.setTitle(last.fm.filter.filterArrray.filter(0, "title", apiArr));
        stage.setAlwaysOnTop(true);
        stage.setX(0);
        stage.setY(Toolkit.getDefaultToolkit().getScreenSize().getHeight()/2);

        if(!last.fm.filter.filterArrray.filter(4,"img", apiArr).isEmpty()) {
            iv.setImage(new Image(last.fm.filter.filterArrray.filter(8, "img", apiArr), 300, 300, true, true));
        }

        artist.setFont(new Font("Helvetica", 40));

        addItem(root, iv);
        addItem(root, artist);

        stage.addEventFilter(KeyEvent.KEY_PRESSED, e-> {
            if(e.getCode() == KeyCode.ESCAPE) {
                stage.close();
            }
        });
    }

    private static void addItem(GridPane root, Node node) {
        GridPane.setConstraints(node, 0,root.getChildren().size());
        GridPane.setHalignment(node, HPos.CENTER);
        root.getChildren().add(node);
    }
}