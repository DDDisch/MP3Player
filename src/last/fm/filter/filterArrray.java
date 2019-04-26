package last.fm.filter;

import javafx.application.Platform;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class filterArrray {
    //Manipulate to JSONObject to fit the corresponding output
    public static String filter(String filter, String mode, JSONObject api) {
        boolean noImg = false;
        if(api.get("error") == null) {
            JSONObject jObj = (JSONObject) api.get(filter);
            JSONArray img = null;
            JSONObject bio = null;

            if (filter.equals("artist"))
                bio = (JSONObject) jObj.get("bio");
            if (filter.equals("track"))
                bio = (JSONObject) jObj.get("wiki");

            if (mode.equals("img")) {
                if (filter.equals("artist")) {
                    img = (JSONArray) jObj.get("image");
                } else if (filter.equals("track")) {
                    JSONObject tmp = (JSONObject) jObj.get("album");
                    try {
                        img = (JSONArray) tmp.get("image");
                    } catch (NullPointerException e) {
                        System.err.println("There is no image linked to your Song");
                        noImg = true;
                    }
                }

                if(!noImg) {
                    assert img != null;
                    JSONObject bigImg = (JSONObject) img.get(img.size() - 1);
                    return "" + bigImg.get("#text");
                } else {
                    return "True";
                }
            }

            if (mode.equals("title")) {
                return "" + jObj.get("name");
            }

            if (mode.equals("text")) {
                assert bio != null;
                return "" + bio.get("content");
            }
        } else {
            System.err.println("There was an Error in the Request" + api.get("error"));
            Platform.exit();
        }

        return "";
    }
}
