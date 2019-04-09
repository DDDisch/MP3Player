package sample;

import FileCommunicator.FileCommunicator;
import javafx.application.Application;
import javafx.collections.MapChangeListener;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import last.fm.lastAPI;
import FileCommunicator.*;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeoutException;

public class Main extends Application {

    private lastAPI api = new lastAPI();
    private Button moreInfo;
    private TextField author;
    private FileCommunicator apiSettings;
    private String tmpS="";
    private Media m;
    GridPane root;
    Button stopAndPlay, forward, backward, load;
    FileChooser fileChooser = new FileChooser();
    Stage primaryStage = new Stage();
    File file;
    boolean play = false;

    private String URL = "";
    private MediaPlayer mediaPlayer;

    public Main() throws IOException {
    }

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

        stopAndPlay = new Button("||");
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
                play = true;
            }
            else
            {
                stopPlayer();
                play = false;
            }

        });

    }


    public void loadMP3(File sound) {
        m = new Media(sound.toURI().toString());
        
        m.getMetadata().addListener(new MapChangeListener<String, Object>() {

            @Override
            public void onChanged(Change<? extends String, ?> change) {
                if(change.wasAdded()) {
                    handleMeta(change.getKey(), change.getValueAdded());
                }
            }
        });
        
        mediaPlayer = new MediaPlayer(m);
        mediaPlayer.setAutoPlay(false);
    }

    public void startPlayer()
    {
        if(mediaPlayer != null)
            mediaPlayer.play();
    }

    public void stopPlayer()
    {
        if(mediaPlayer != null)
            mediaPlayer.pause();
    }

    public void handleMeta(String key, Object value) {
        if (key.equals("album")) {
            //album.setText(value.toString());
        } else if (key.equals("artist")) {
            author.setText(value.toString());
        } if (key.equals("title")) {
            //title.setText(value.toString());
        } if (key.equals("year")) {
            //year.setText(value.toString());
        } if (key.equals("image")) {
            //albumCover.setImage((Image)value);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }



}
