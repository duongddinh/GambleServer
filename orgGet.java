

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.net.HttpURLConnection;

public class orgGet {
    private ArrayList<String> arraylistcontain = new ArrayList<String>();
    private String email;
    private int statusCode;
    private String output = "";
    public orgGet(String email)  {
        this.email = email;
    }

    private String htmlParse(String url2, boolean checkQuota) throws Exception, IOException {
        BufferedReader br = null;
        StringBuilder getran = new StringBuilder();
        URL url = new URL(url2);
        URLConnection uc = url.openConnection();
        uc.addRequestProperty("User-Agent", email);
        if (!checkQuota) {
            statusCode = ((HttpURLConnection) uc).getResponseCode();
            System.out.println(statusCode);
            if (statusCode !=200) {
                output = "Too many requests";
                throw new Exception("Too many requests, wait for 10 mins to a day");
            }
        }
        long tStart = System.currentTimeMillis();
        br = new BufferedReader(new InputStreamReader(uc.getInputStream()));
        String line;
        while (((line = br.readLine()) != null)) {
            long tEnd = System.currentTimeMillis();
            if ((tEnd - tStart)/1000.0 >= (60)) {
                output = "Operation timed out";
                throw new Exception("Operation timed out! Check your internet connection");
            }
            getran.append(line+" ");
        }
        if (br != null) {
            br.close();
        }

        if (!checkQuota)
            arraylistcontain = parseString(getran.toString());

        return getran.toString();
    }

    private ArrayList<String> parseString(String a) {
        arraylistcontain.clear();
        char c;
        String d ="";
        ArrayList<String> arrli = new ArrayList<String>();
        for (int i =0; i< a.length(); i++){
            c = a.charAt(i);
            if (c != ' '){
                d +=c;

            } else {
                arrli.add(d);
                d = "";
            }
        }
        return arrli;
    }

    public String getRandomNumber(int totalnumber, int min, int max, int base) throws Exception, IOException {
        String a = "https://www.random.org/integers/?num="+totalnumber+ "&min="+min +"&max="+max +"&col=1&base="+base+"&format=plain&rnd=new";
        if (Integer.valueOf(QuotaCheck()) >= 0) {
            return htmlParse(a, false);
        } else {
            output = "Too many requests";
            try {
                throw new Exception("Too many requests, wait for 10 mins to a day");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "-1";
        }
    }
    public String QuotaCheck(String ipadrr) throws Exception, IOException {
        String d = "https://www.random.org/quota/?ip="+ipadrr+"&format=plain";
        return htmlParse(d, true).trim();
    }
    public String QuotaCheck() throws Exception, IOException {
        String e = "https://www.random.org/quota/?format=plain";
        return htmlParse(e, true).trim();
    }

}
