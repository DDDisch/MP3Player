package last.fm.filter;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

public class filterArrray {
    //Manipulate to JSONObject to fit the corresponding output
    public static String filter(String filter, String mode, JSONObject api) {
        JSONObject artist = (JSONObject) api.get(filter);
        JSONArray img = (JSONArray) artist.get("image");

        if(mode.equals("img")) {
            JSONObject bigImg = (JSONObject)img.get(img.size()-3);
            return "" + bigImg.get("#text");
        }
        if(mode.equals("title")) {
            return "" + artist.get("name");
        }
        return "";
    }
}
