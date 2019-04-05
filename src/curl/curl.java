package curl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class curl {
    public static String sendCurl(String link, String method) throws IOException {
        //Build and Connect to given URL
        link = link.replace(" ", "%20");
        HttpURLConnection con = (HttpURLConnection) new URL(link).openConnection();
        con.setRequestMethod(method);
        con.setDoOutput(true);

        //Read Output form API
        InputStream instr = con.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(instr));
        String lin,linR="";
        while((lin = br.readLine())!=null) linR = String.format("%s%s", linR, String.format("%s\n", lin));

        //Disconnect Connection
        con.disconnect();

        //Return Read lines
        return linR;
    }
}
