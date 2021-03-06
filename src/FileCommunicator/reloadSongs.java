package FileCommunicator;

import java.io.*;
import java.util.Vector;

public class reloadSongs {

    static Vector<File> reloadedSongs = new Vector<File>();

    public static void reloadSongs() throws IOException {
        File file = new File("./res/settings/loadedFiles.txt");
        if(!file.exists())
            file.createNewFile();

        BufferedReader br = new BufferedReader(new FileReader(file));
        String st = null;
        while ((st = br.readLine()) != null) {
            File song = new File(st);
            reloadedSongs.add(song);
        }
    }

    public static Vector getReloadedSongs()
    {
        return reloadedSongs;
    }
}
