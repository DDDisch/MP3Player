package windows;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.HPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.json.simple.JSONObject;
import sample.Main;

import java.awt.*;

public class authorPopUp {
    public static void authorWindow(JSONObject api) {
        Stage stage = new Stage();
        stage.initStyle(StageStyle.UNDECORATED);
        GridPane root = new GridPane();
        ImageView iv = new ImageView();
        Label artist = new Label(last.fm.filter.filterArrray.filter("artist", "title", api));
        Label content = new Label(last.fm.filter.filterArrray.filter("artist", "text", api));
        content.setWrapText(true);

        stage.setScene(new Scene(root));
        stage.show();
        stage.setTitle(last.fm.filter.filterArrray.filter("artist", "title", api));
        stage.setAlwaysOnTop(true);
        stage.setX(Screen.getPrimary().getBounds().getWidth()/2);
        stage.setY(0);

        if(!last.fm.filter.filterArrray.filter("artist","img", api).isEmpty()) {
            iv.setImage(new Image(last.fm.filter.filterArrray.filter("artist", "img", api), 300, 300, true, true));
            stage.setWidth(iv.getImage().getWidth());
        }

        artist.setFont(new Font("Helvetica", 40));

        addItem(root, iv);
        addItem(root, artist);
        addItem(root, content);

        stage.addEventFilter(KeyEvent.KEY_PRESSED, e-> {
            if(e.getCode() == KeyCode.ESCAPE) {
                stage.close();
            }
        });

        final Delta dragDelta = new Delta();
        root.setOnMousePressed(mouseEvent -> {
            dragDelta.x = stage.getX() - mouseEvent.getScreenX();
            dragDelta.y = stage.getY() - mouseEvent.getScreenY();
        });
        root.setOnMouseDragged(mouseEvent -> {
            stage.setX(mouseEvent.getScreenX() + dragDelta.x);
            stage.setY(mouseEvent.getScreenY() + dragDelta.y);
        });
    }

    private static void addItem(GridPane root, Node node) {
        GridPane.setConstraints(node, 0,root.getChildren().size());
        GridPane.setHalignment(node, HPos.CENTER);
        root.getChildren().add(node);
    }

    static class Delta { double x, y; }
}