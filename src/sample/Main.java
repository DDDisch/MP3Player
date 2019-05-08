package sample;

import FileCommunicator.FileCommunicator;
import FileCommunicator.reloadSongs;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.MapChangeListener;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import last.fm.lastAPI;
import org.json.simple.JSONObject;
import windows.List;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;
import java.util.concurrent.TimeoutException;

public class Main extends Application {

    private lastAPI api = new lastAPI();
    private Button moreInfo;
    private static Label author = new Label(""), title = new Label("");
    private FileCommunicator apiSettings;
    private String tmpS="";
    private GridPane root;
    private static Button stopAndPlay, forward, backward, load;
    private FileChooser fileChooser = new FileChooser();
    private static Slider volume = new Slider();
    private static Slider playProgress = new Slider();
    private Stage primaryStage = new Stage();
    private static Duration duration;
    private File file;
    private boolean play = false;
    private static List getSongs;
    private static Media m;

    private static MediaPlayer mediaPlayer;

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
        root.getStylesheets().add(getClass().getResource("style.css").toExternalForm());

        primaryStage.initStyle(StageStyle.UNDECORATED);

        primaryStage.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            if(e.getCode() == KeyCode.ESCAPE) {
                Platform.exit();
            }
        });

        getSongs = new List();

        stopAndPlay = new Button("\u25B6");
        forward = new Button(">");
        backward = new Button("<");
        load = new Button("Load");

        primaryStage.setTitle("Last.fm API");
        primaryStage.setScene(new Scene(root, 300,300));
        primaryStage.setX(0);
        primaryStage.setY(40);

        volume.setValue(100);
        moreInfo = new Button("\u24d8");
        moreInfo.setId("moreInfo");

        addListener();
        if(apiSettings.readFile().contains("Author:")) {
            String tmp = null;
            try {
                tmp = apiSettings.readLine(1);
            } catch (IOException e) {
                e.printStackTrace();
            }

            assert tmp != null;
            ArrayList<String> tmpA= new ArrayList<>(Arrays.asList(tmp.split("(?<= )")));
            tmpA.remove(0);

            for (String s : tmpA) tmpS += s;

            author.setText(tmpS);
        } else {
            author.setText("Unknown");
        }

        if(apiSettings.readFile().contains("Title:")) {
            tmpS = "";
            String tmp = null;
            try {
                tmp = apiSettings.readLine(2);
            } catch (IOException e) {
                e.printStackTrace();
            }
            assert tmp != null;
            ArrayList<String> tmpA= new ArrayList<>(Arrays.asList(tmp.split("(?<= )")));
            tmpA.remove("Title: ");
            for (String s : tmpA) tmpS += s;
            title.setText(tmpS);
        }
        else {
            title.setText("Unknown");
        }


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
        addToRoot(playProgress, 2);
        addToRoot(control,3);
        addToRoot(volume, 4);
        addToRoot(load,5);
        playProgress.setMinWidth(primaryStage.getWidth() - primaryStage.getWidth()/4);

        primaryStage.show();
        primaryStage.titleProperty().bind(author.textProperty());

        try {
            reloadSongs.reloadSongs();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Vector<File> songs = reloadSongs.getReloadedSongs();

        for (File song : songs) {
            getSongs.addListItem(song);
        }

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
            JSONObject tmp = null;
            try {
                tmp = api.authorAPI(author.getText());
            } catch (TimeoutException | InterruptedException e1) {
                e1.printStackTrace();
            }

            windows.authorPopUp.authorWindow(tmp);
        });

        load.setOnAction(e -> {
            root.setCursor(Cursor.WAIT);

            file = fileChooser.showOpenDialog(primaryStage);
            getSongs.addListItem(file);

            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter("./res/settings/loadedFiles.txt", true));
                writer.append(file.toURI().getPath()+"\n");
                writer.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if(mediaPlayer != null)
                mediaPlayer.dispose();

            if (file != null) {
                stopAndPlay.setText("\u25B6");
                loadMP3(file);
                play = false;
            }
            root.setCursor(Cursor.DEFAULT);
        });

        stopAndPlay.setOnAction(e ->{
            if (!play) {
                startPlayer();
                play = true;
            } else {
                stopPlayer();
                play = false;
            }

        });

        title.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                JSONObject apiJ = api.titleAPI(author.getText(), title.getText());
                if(!last.fm.filter.filterArrray.filter("track", "img", apiJ).equals("True")) {
                    Image img = new Image(last.fm.filter.filterArrray.filter("track", "img", apiJ));
                    primaryStage.setHeight(300);
                    primaryStage.setWidth(300);
                    System.out.println(last.fm.filter.filterArrray.filter("track", "img", apiJ));
                    root.setBackground(new Background(new BackgroundImage(img, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT)));
                } else {
                    apiJ = api.authorAPI(author.getText());
                    Image img = new Image(last.fm.filter.filterArrray.filter("artist", "img", apiJ));
                    primaryStage.setHeight(300);
                    primaryStage.setWidth(300);
                    root.setBackground(new Background(new BackgroundImage(img, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT)));
                }
            } catch (InterruptedException | TimeoutException e) {
                root.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
                e.printStackTrace();
            }
        });

        final Delta dragDelta = new Delta();
        root.setOnMousePressed(mouseEvent -> {
            dragDelta.x = primaryStage.getX() - mouseEvent.getScreenX();
            dragDelta.y = primaryStage.getY() - mouseEvent.getScreenY();
        });
        root.setOnMouseDragged(mouseEvent -> {
            primaryStage.setX(mouseEvent.getScreenX() + dragDelta.x);
            primaryStage.setY(mouseEvent.getScreenY() + dragDelta.y);
        });
    }

    public static void loadMP3(File sound) {
        m = new Media(sound.toURI().toString());
        mediaPlayer = new MediaPlayer(m);
        mediaPlayer.setAutoPlay(false);
        mediaPlayer.setStartTime(new Duration(0));

        mediaPlayer.setOnReady(() -> {
            duration = mediaPlayer.getMedia().getDuration();
        });

        volume.valueProperty().addListener(ov -> {
            if (volume.isValueChanging()) {
                mediaPlayer.setVolume(volume.getValue()/100.0);
            }
        });

        playProgress.valueProperty().addListener(ov -> {
            if (playProgress.isValueChanging()) {
                // multiply duration by percentage calculated by slider position
                mediaPlayer.seek(duration.multiply(playProgress.getValue() / 100.0));
            }
        });

        mediaPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
            playProgress.setValue(mediaPlayer.getCurrentTime().divide(duration).toMillis() * 100);
        });

        mediaPlayer.volumeProperty().addListener((observable, oldValue, newValue) -> {
            volume.setValue((int)Math.round(mediaPlayer.getVolume() * 100));
        });

        m.getMetadata().addListener((MapChangeListener<String, Object>) change -> {
            if(change.wasAdded()) {
                handleMeta(change.getKey(), change.getValueAdded());
            }
        });
    }

    private void startPlayer()
    {
        stopAndPlay.setText("\u25A0");
        if(mediaPlayer != null)
            mediaPlayer.play();
    }

    public static void stopPlayer()
    {
        stopAndPlay.setText("\u25B6");
        if(mediaPlayer != null)
            mediaPlayer.pause();
    }

    private static void handleMeta(String key, Object value) {
        if (key.equals("artist")) {
            author.setText(value.toString());
        } else if (key.equals("title")) {
            title.setText(value.toString());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    class Delta { double x, y; }
}
