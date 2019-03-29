package last.fm;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class lastAPI {
    private String out;

    /*
        The Constructor is used to Generate to the Token and get the Session of the User which is need for further requests
    */
    public String authorAPI(String author) throws TimeoutException, InterruptedException {
        try {
            String api_key = "f5e523cdf9f852a409985ca6d22c4f1d";
            //private String api_sig = "839f284b8672339a30048e6817acadfe";
            String baseURL = "http://ws.audioscrobbler.com/2.0/";
            out = curl.curl.sendCurl(baseURL + "?method=artist.getInfo&artist=" + author + "&api_key=" + api_key + "&lang=de&format=json", "GET");
        } catch (IOException e) {
            e.printStackTrace();
        }

        int i = 0;
        while (i < 120) {
            if (out.contains("artist")) {
                break;
            } else {
                TimeUnit.SECONDS.sleep(1);
                ++i;
                if (i == 120) {
                    throw new TimeoutException("Timed out after waiting for " + i + " seconds");
                }
            }
        }

        return out;
    }
}
