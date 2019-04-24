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
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.transform.Scale;
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
    private Label author, title;
    private FileCommunicator apiSettings;
    private String tmpS="";
    private GridPane root;
    private Button stopAndPlay, forward, backward, load;
    private FileChooser fileChooser = new FileChooser();
    private Stage primaryStage = new Stage();
    private File file;
    private ImageView bgImg = new ImageView();
    private boolean play = false;
    private String baseURL = "http://ws.audioscrobbler.com/2.0/";
    private String api_key = "f5e523cdf9f852a409985ca6d22c4f1d";

    private MediaPlayer mediaPlayer;

    @Override
    public void start(Stage primaryStage2) throws IOException {

        try {
            apiSettings = new FileCommunicator(new File("./res/settings/api.txt"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        primaryStage = primaryStage2;
        fileChooser.setTitle("Open Resource File");

        root = new GridPane();
        root.setAlignment(Pos.CENTER);
        root.getStylesheets().add(getClass().getResource("style.css").toExternalForm());

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
        primaryStage.setScene(new Scene(root, 300, Toolkit.getDefaultToolkit().getScreenSize().getHeight()/2));
        primaryStage.setX(0);
        primaryStage.setY(0);

        moreInfo = new Button("\u24d8");
        moreInfo.setId("moreInfo");

        if(apiSettings.readFile().contains("Author:")) {
            String tmp = null;
            try {
                tmp = apiSettings.readLine(1);
            } catch (IOException e) {
                e.printStackTrace();
            }

            ArrayList<String> tmpA= new ArrayList<>(Arrays.asList(tmp.split("(?<= )")));
            tmpA.remove(0);

            for (String s : tmpA) tmpS += s;

            author = new Label("" + tmpS);
        } else {
            author = new Label("Author");
        }

        if(apiSettings.readFile().contains("Title:")) {
            tmpS = "";
            String tmp = null;
            try {
                tmp = apiSettings.readLine(2);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ArrayList<String> tmpA= new ArrayList<>(Arrays.asList(tmp.split("(?<= )")));

            tmpA.remove("Title: ");

            for (String s : tmpA) tmpS += s;

            title = new Label("" + tmpS);
        }
        else {
            title = new Label("Title");
        }

        String api = curl.curl.sendCurl(baseURL + "?method=artist.getInfo&artist=" + author.getText() + "&api_key=" + api_key + "&lang=de&autocorrec=1&format=json", "GET");
        ArrayList<String> apiArr = last.fm.filter.filterArrray.createArr(api);
        System.out.println(last.fm.filter.filterArrray.filter(8, "img", apiArr));
        root.setBackground(new Background(new BackgroundImage(new Image(last.fm.filter.filterArrray.filter(8, "img", apiArr), root.getWidth()+85, root.getHeight(), true, true), BackgroundRepeat.NO_REPEAT,BackgroundRepeat.NO_REPEAT,BackgroundPosition.CENTER, BackgroundSize.DEFAULT)));

        HBox control = new HBox();
        control.getChildren().addAll(backward,stopAndPlay,forward);
        control.setAlignment(Pos.CENTER);

        load.setId("load");

        HBox info = new HBox();
        info.getChildren().addAll(author,moreInfo);
        info.setAlignment(Pos.TOP_CENTER);

        title.setId("title");
        author.setId("author");

        addToRoot(title,0);
        addToRoot(info,1);
        addToRoot(control,4);
        addToRoot(load,5);

        primaryStage.show();
        primaryStage.titleProperty().bind(author.textProperty());

        addListener();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                if(!author.getText().equals("")) {
                    apiSettings.writeLine("Author: " + author.getText(), false);
                } else {
                    apiSettings.writeLine("Author: None", false);
                }

                if(!title.getText().equals("")) {
                    apiSettings.writeLine("Title: " + title.getText(), true);
                } else {
                    apiSettings.writeLine("Title: None", true);
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

        author.textProperty().addListener((obsv, newv, old) -> {
            try {
                String api = curl.curl.sendCurl(baseURL + "?method=artist.getInfo&artist=" + author.getText() + "&api_key=" + api_key + "&lang=de&autocorrec=1&format=json", "GET");
                ArrayList<String> apiArr = last.fm.filter.filterArrray.createArr(api);
                root.setBackground(new Background(new BackgroundImage(new Image(last.fm.filter.filterArrray.filter(8, "img", apiArr), primaryStage.getWidth(), primaryStage.getHeight(), true, true), BackgroundRepeat.NO_REPEAT,BackgroundRepeat.NO_REPEAT,BackgroundPosition.CENTER, BackgroundSize.DEFAULT)));
            } catch (IOException e) {
                e.printStackTrace();
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
            title.setText(value.toString());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
