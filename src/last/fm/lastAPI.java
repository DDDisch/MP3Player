package last.fm;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class lastAPI {
    private Object out;
    private JSONObject jout;
    private JSONParser jsonParser = new JSONParser();
    private final String api_key = "f5e523cdf9f852a409985ca6d22c4f1d";
    //private final String api_sig = "839f284b8672339a30048e6817acadfe";
    private final String baseURL = "http://ws.audioscrobbler.com/2.0/";
    private final int WAIT = 60;

    /*
        The Constructor is used to Generate to the Token and get the Session of the User which is need for further requests
    */
    public JSONObject authorAPI(String author) throws TimeoutException, InterruptedException {
        try {

            if(author.contains("&")) {
                int tmp = author.indexOf("&");
                author = author.substring(0,tmp-1);
            }

            out = jsonParser.parse(curl.curl.sendCurl(baseURL + "?method=artist.getInfo&artist=" + author + "&api_key=" + api_key + "&lang=de&autocorrect=1&format=json", "GET"));
            jout = (JSONObject) out;
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        int i = 0;
        while (i < WAIT) {
            if (jout.get("artist") != null) {
                break;
            } else {
                TimeUnit.SECONDS.sleep(1);
                ++i;
                if (i == 120) {
                    throw new TimeoutException("Timed out after waiting for " + i + " seconds");
                }
            }
        }

        return jout;
    }

    public JSONObject titleAPI(String artist, String title) throws TimeoutException, InterruptedException {
        try {
            if(artist.contains("&")) {
                int tmp = artist.indexOf("&");
                artist = artist.substring(0,tmp-1);
            }

            out = jsonParser.parse(curl.curl.sendCurl(baseURL + "?method=track.getInfo&api_key=" + api_key + "&artist=" + artist +"&track=" + title + "&format=json", "GET"));
            jout = (JSONObject) out;
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        int i = 0;
        while (i < WAIT) {
            if (jout.get("track") != null) {
                break;
            } else {
                TimeUnit.SECONDS.sleep(1);
                ++i;
                if (i == 120) {
                    throw new TimeoutException("Timed out after waiting for " + i + " seconds");
                }
            }
        }

        return jout;
    }
}