package last.fm;

import md5.MD5;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

public class mainAPI {
    private String linR="";
    private String api_key = "f5e523cdf9f852a409985ca6d22c4f1d";
    private String api_sig = "839f284b8672339a30048e6817acadfe";
    private String out;
    private ArrayList<String> outA;
    private String token, tokenB;

    /*
        The Constructor is used to Generate to the Token and get the Session of the User which is need for further requests
    */
    public mainAPI() throws IOException, URISyntaxException {
        //curl command will be written in command String

        out = curlCall("http://ws.audioscrobbler.com/2.0/?method=auth.gettoken&api_key=" + api_key + "&api_sig=" + api_sig + "&format=json");

        outA = new ArrayList<>(Arrays.asList(out.replace("\"", "").split(":")));
        tokenB = outA.get(1);
        tokenB = tokenB.replace("\n", "");

        token = tokenB;
        token = token.replace("}", "%7D");

        if(token != null) {
            String os = System.getProperty("os.name").toLowerCase();
            String url = "http://www.last.fm/api/auth/?api_key=" + api_key + "&token=" + token;

            if (os.contains("win")) {
                Runtime rt = Runtime.getRuntime();
                rt.exec("rundll32 url.dll,FileProtocolHandler " + url);
            } else if (os.contains("mac")) {
                Runtime rt = Runtime.getRuntime();
                rt.exec("open " + url);
            } else if (os.contains("nix") || os.contains("nux")) {
                Runtime rt = Runtime.getRuntime();
                String[] browsers = {"epiphany", "firefox", "mozilla", "konqueror",
                        "netscape", "opera", "links", "lynx"};

                StringBuffer cmd = new StringBuffer();
                for (int i = 0; i < browsers.length; i++) {
                    if (i == 0) cmd.append(String.format("%s \"%s\"", browsers[i], url));
                    else cmd.append(String.format(" || %s \"%s\"", browsers[i], url));
                    // If the first didn't work, try the next browser and so on
                }

                rt.exec(new String[]{"sh", "-c", cmd.toString()});
            }


        }

        out = curlCall("http://ws.audioscrobbler.com/2.0/?method=auth.getSession&token=" + token + "&api_key=" + api_key + "&api_sig=" + createSecret("auth.getSession") + "&format=json");
        System.out.println(out);

    }

    private String createSecret(String method) {
        String sec = "api_key" + api_key + "method" + method + "token" + tokenB + api_sig;
        System.out.println("Non md5: " + sec);
        sec = MD5.getMd5(sec);
        System.out.println("MD5: " + sec);
        return sec;
    }

    private String curlCall(String link) throws IOException {
        //Build and Connect to given URL
        HttpURLConnection con = (HttpURLConnection) new URL(link).openConnection();
        con.setRequestMethod("GET");
        con.setDoOutput(true);

        //Read Output form API
        InputStream instr = con.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(instr));
        String lin;
        while((lin = br.readLine())!=null) linR = String.format("%s%s", linR, String.format("%s\n", lin));

        //Disconnect Connection and Reset all Variables
        con.disconnect();
        con = null;
        link = null;
        System.gc();

        //Return Read lines
        return linR;
    }

}
