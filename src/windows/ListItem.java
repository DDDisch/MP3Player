package windows;

import javafx.scene.control.Button;

import java.io.File;

public class ListItem extends Button {


    File file;

    public ListItem(String name)
    {
        this.setWidth(150);
        this.setHeight(50);
        this.setText(name);
        addListeners();
    }

    public void setFile(File file) {
        this.file = file;
    }

    private void addListeners()
    {
        this.setOnMouseClicked(event -> {
            sample.Main.stopPlayer();
            sample.Main.loadMP3(file);
        });
    }
}
