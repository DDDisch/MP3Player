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
import org.json.simple.JSONObject;

import java.awt.*;
import java.util.ArrayList;

public class authorPopUp {
    public static void authorWindow(JSONObject api) {
        Stage stage = new Stage();
        stage.initStyle(StageStyle.UNDECORATED);
        GridPane root = new GridPane();
        ImageView iv = new ImageView();
        Label artist = new Label(last.fm.filter.filterArrray.filter("artist", "title", api));

        stage.setScene(new Scene(root));
        stage.show();
        stage.setTitle(last.fm.filter.filterArrray.filter("artist", "title", api));
        stage.setAlwaysOnTop(true);
        stage.setX(0);
        stage.setY(Toolkit.getDefaultToolkit().getScreenSize().getHeight()/2);

        if(!last.fm.filter.filterArrray.filter("artist","img", api).isEmpty()) {
            iv.setImage(new Image(last.fm.filter.filterArrray.filter("artist", "img", api), 300, 300, true, true));
            stage.setWidth(iv.getImage().getWidth());
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