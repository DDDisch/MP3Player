package sample;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import last.fm.mainAPI;

import java.io.IOException;
import java.net.URISyntaxException;

public class Main extends Application {

    private mainAPI api;
    private Button connApi;

    @Override
    public void start(Stage primaryStage) throws Exception{
        Group root = new Group();
        primaryStage.setTitle("Last.fm API");
        primaryStage.setScene(new Scene(root, 300, 275));

        connApi = new Button("Connect Api");
        connApi.setOnAction(e -> {
            try {
                api = new mainAPI();
            } catch (IOException | URISyntaxException ex) {
                ex.printStackTrace();
            }
        });
        root.getChildren().add(connApi);

        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
