package last.fm.filter;

import java.util.ArrayList;
import java.util.Arrays;

public class filterArrray {
    public static ArrayList<String> createArr(String api) {
        ArrayList<String> apiArr = new ArrayList<>(Arrays.asList(api.split(",")));

        //Delete MBID entry, because not every Artist has an MBID
        //Reason: To prevent different indexes after the MBID
        if(apiArr.get(1).contains("mbid"))
            apiArr.remove(1);

        return apiArr;
    }

    //Call Filter Array with every Index except Null, because the splitting of the API return String does not split the first with the last items
    public static String filter(int index, String mode, ArrayList<String> apiArr) {
        if(mode.equals("normal"))
            return new ArrayList<>(Arrays.asList(apiArr.get(index).split(":"))).get(1).replace("\"", "");
        if(mode.equals("img"))
            return new ArrayList<>(Arrays.asList(apiArr.get(index).split(":",2))).get(1).replace("\"", "");
        if(mode.equals("title"))
            return new ArrayList<>(Arrays.asList(apiArr.get(0).split(":"))).get(2).replace("\"", "");
        return "";
    }
}
