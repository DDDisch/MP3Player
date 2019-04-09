package sample;

import FileCommunicator.FileCommunicator;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.MapChangeListener;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import last.fm.lastAPI;

import java.awt.*;
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
    private GridPane root;
    private Button stopAndPlay, forward, backward, load;
    private FileChooser fileChooser = new FileChooser();
    private Stage primaryStage = new Stage();
    private File file;
    private boolean play = false;

    private MediaPlayer mediaPlayer;

    @Override
    public void start(Stage primaryStage2) {
        try {
            apiSettings = new FileCommunicator(new File("./res/settings/api.txt"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        primaryStage = primaryStage2;
        fileChooser.setTitle("Open Resource File");

        root = new GridPane();
        root.setAlignment(Pos.CENTER);

        primaryStage.initStyle(StageStyle.UNDECORATED);

        primaryStage.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            if(e.getCode() == KeyCode.ESCAPE) {
                Platform.exit();
            }
        });

        stopAndPlay = new Button("\u25B6");
        forward = new Button(">");
        backward = new Button("<");
        load = new Button("Load");

        primaryStage.setTitle("Last.fm API");
        primaryStage.setScene(new Scene(root, Toolkit.getDefaultToolkit().getScreenSize().getWidth(), Toolkit.getDefaultToolkit().getScreenSize().getHeight()/2));
        primaryStage.setX(0);
        primaryStage.setY(0);


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

        HBox control = new HBox();
        control.getChildren().addAll(backward,stopAndPlay,forward);
        control.setAlignment(Pos.CENTER);

        addToRoot(author,0);
        addToRoot(moreInfo,1);
        addToRoot(control,3);
        addToRoot(load,4);



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

    private void addToRoot(Node Element, int row) {
        GridPane.setConstraints(Element, 0, row);
        GridPane.setHalignment(Element, HPos.CENTER);
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

        load.setOnAction(e -> {
            file = fileChooser.showOpenDialog(primaryStage);
            if (file != null)
            {
                loadMP3(file);
                play = false;
            }
        });

        stopAndPlay.setOnAction(e ->{
            if (!play)
            {
                startPlayer();
                stopAndPlay.setText("\u25A0");
                play = true;
            }
            else
            {
                stopPlayer();
                stopAndPlay.setText("\u25B6");
                play = false;
            }

        });

    }


    private void loadMP3(File sound) {
        Media m = new Media(sound.toURI().toString());
        
        m.getMetadata().addListener((MapChangeListener<String, Object>) change -> {
            if(change.wasAdded()) {
                handleMeta(change.getKey(), change.getValueAdded());
            }
        });
        
        mediaPlayer = new MediaPlayer(m);
        mediaPlayer.setAutoPlay(false);
    }

    private void startPlayer()
    {
        if(mediaPlayer != null)
            mediaPlayer.play();
    }

    private void stopPlayer()
    {
        if(mediaPlayer != null)
            mediaPlayer.pause();
    }

    private void handleMeta(String key, Object value) {
        if (key.equals("artist")) {
            author.setText(value.toString());
        } else if (key.equals("title")) {
            //title.setText(value.toString());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
