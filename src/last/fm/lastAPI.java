package last.fm;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class lastAPI {
    private Object out;
    private JSONObject jout;
    private JSONParser jsonParser = new JSONParser();
    private final String api_key = "f5e523cdf9f852a409985ca6d22c4f1d";
    //private final String api_sig = "839f284b8672339a30048e6817acadfe";
    private final String baseURL = "http://ws.audioscrobbler.com/2.0/";

    /*
        The Constructor is used to Generate to the Token and get the Session of the User which is need for further requests
    */
    public JSONObject authorAPI(String author) throws TimeoutException, InterruptedException {
        try {

            if(author.contains("&")) {
                int tmp = author.indexOf("&");
                author = author.substring(0,tmp-1);
            }

            out = jsonParser.parse(curl.curl.sendCurl(baseURL + "?method=artist.getInfo&artist=" + author + "&api_key=" + api_key + "&lang=de&autocorrec=1&format=json", "GET"));
            jout = (JSONObject) out;
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        System.out.println(jout);

        int i = 0;
        while (i < 120) {
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
}