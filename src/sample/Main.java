package sample;

import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import last.fm.lastAPI;
import FileCommunicator.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeoutException;

public class Main extends Application {

    private lastAPI api = new lastAPI();
    private Button moreInfo;
    private TextField author;
    private FileCommunicator apiSettings;
    private String tmpS="";

    @Override
    public void start(Stage primaryStage) {
        try {
            apiSettings = new FileCommunicator(new File("./res/settings/api.txt"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        GridPane root = new GridPane();
        primaryStage.setTitle("Last.fm API");
        primaryStage.setScene(new Scene(root, 300, 275));

        moreInfo = new Button("More Information");
        if(apiSettings.readFile().contains("Author:")) {
            String tmp = apiSettings.readFile();
            ArrayList<String> tmpA= new ArrayList<>(Arrays.asList(tmp.split("(?<= )")));
            tmpA.remove(0);

            for (String s : tmpA) tmpS += s;

            author = new TextField("" + tmpS);
        }
        else {
            author = new TextField("U2");
        }

        addToRoot(root, author);
        addToRoot(root, moreInfo);

        //GridPane.setConstraints(moreInfo, 0, 0);
        //GridPane.setConstraints(author, 0, 1);
        //root.getChildren().add(moreInfo);
        //root.getChildren().add(author);

        primaryStage.show();

        primaryStage.titleProperty().bind(author.textProperty());

        addListener();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                if(!author.getText().equals("")) {
                    apiSettings.writeLine("Author: " + author.getText());
                } else {
                    apiSettings.writeLine("None");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));
    }

    private void addToRoot(GridPane root, Node Element) {
        GridPane.setConstraints(Element, 0, root.getChildren().size());
        root.getChildren().add(Element);
    }

    private void addListener() {
        moreInfo.setOnAction(e -> {
            String tmp = "";
            try {
                tmp = api.authorAPI(author.getText());
            } catch (TimeoutException | InterruptedException e1) {
                e1.printStackTrace();
            }

            windows.authorPopUp.authorWindow(tmp);
        });
    }


    public static void main(String[] args) {
        launch(args);
    }
}
