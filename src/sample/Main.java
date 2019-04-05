package sample;

import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import last.fm.lastAPI;
import FileCommunicator.*;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

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
    GridPane root;
    Button stopAndPlay, forward, backward, load;
    FileChooser fileChooser = new FileChooser();
    Stage primaryStage = new Stage();
    File file;
    boolean play = false;

    private String URL = "";
    public AudioStream audioStream;

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
                loadMP3(file.getAbsolutePath());
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


    public void loadMP3(String soundURL) {
        InputStream in = null;
        try {
            in = new FileInputStream(soundURL);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        audioStream = null;
        try {
            audioStream = new AudioStream(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startPlayer()
    {
        AudioPlayer.player.start(audioStream);
    }

    public void stopPlayer()
    {
        AudioPlayer.player.stop(audioStream);
    }

    public static void main(String[] args) {
        launch(args);
    }



}
