


import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class RadioActiveDecay {


    public static String parseJSON(String toParse) {

        String target = "data";
        int i = 0;
        int last = toParse.length() - target.length() + 1;
        while (i < last) {
            if (toParse.charAt(i) == target.charAt(0)) {
                boolean equal = true;
                for (int j = 0; j < target.length() && equal; ++j) {
                    if (toParse.charAt(i + j) != target.charAt(j)) {
                        equal = false;
                    }
                }
                if (equal) {

                    String crop = toParse.substring(i+8).trim();
                    String crop2 =  crop.substring(0,crop.indexOf("}"));
                    return crop2.substring(0,crop.indexOf("]")).trim().replace(",","");
                }
            }
            ++i;
        }
        return null;
    }

    public String getDataFromServer(String TotalNum, String APIkey) throws IOException {
        URL url = new URL("https://www.fourmilab.ch/cgi-bin/Hotbits.api?nbytes="+TotalNum+"&fmt=json&npass=1&lpass=8&pwtype=3&apikey="+APIkey);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        String parser = "";
        con.setRequestMethod("GET");
        con.connect();
        if (con.getResponseCode() != 200) {
            throw new RuntimeException("Error getting data from server");
        }else {
            long tStart = System.currentTimeMillis();
            Scanner scn = new Scanner(url.openStream());
            while(scn.hasNext()) {
                long tEnd = System.currentTimeMillis();
                if ((tEnd - tStart)/1000.0 >= (60)) {
                    scn.close();
                    throw new RuntimeException("Operation timed out! Check your internet connection");
                }
                parser+=scn.nextLine();
            }
            scn.close();
        }
        return parser;
    }
}
