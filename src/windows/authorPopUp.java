package windows;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.ArrayList;
import java.util.Arrays;

public class authorPopUp {
    private static ArrayList<String> apiArr;

    public static void authorWindow(String api) {
        createArr(api);

        Stage stage = new Stage();
        stage.initStyle(StageStyle.UNDECORATED);
        GridPane root = new GridPane();
        ImageView iv = new ImageView();
        if(!filterArray(4,"img").isEmpty()) {
            iv.setImage(new Image(filterArray(4, "img")));
        }

        stage.setScene(new Scene(root, 400, 400));
        stage.show();
        stage.setTitle(filterArray(0, "title"));

        addItem(root, new Label(filterArray(0, "title")));
        addItem(root, iv);

        stage.addEventFilter(KeyEvent.KEY_PRESSED, e-> {
            if(e.getCode() == KeyCode.ESCAPE) {
                stage.close();
            }
        });
    }

    private static void addItem(GridPane root, Node node) {
        root.getChildren().add(node);
        GridPane.setConstraints(root.getChildren().get(root.getChildren().size()-1), 0,root.getChildren().size()-1);
    }

    private static void createArr(String api) {
        apiArr = new ArrayList<>(Arrays.asList(api.split(",")));

        //Delete MBID entry, because not every Artist has an MBID
        //Reason: To prevent different indexes after the MBID
        if(apiArr.get(1).contains("mbid"))
            apiArr.remove(1);
    }

    //Call Filter Array with every Index except Null, because the spliting of the API return String does not split the first with the last items
    private static String filterArray(int index, String mode) {
        if(mode.equals("normal"))
            return new ArrayList<>(Arrays.asList(apiArr.get(index).split(":"))).get(1).replace("\"", "");
        if(mode.equals("img"))
            return new ArrayList<>(Arrays.asList(apiArr.get(index).split(":",2))).get(1).replace("\"", "");
        if(mode.equals("title"))
            return new ArrayList<>(Arrays.asList(apiArr.get(0).split(":"))).get(2).replace("\"", "");
        return "";
    }
}
