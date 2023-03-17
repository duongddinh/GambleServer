
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.Scanner;

public class TimeConnect {

    public String getTine() {
        // TODO Auto-generated method stub
        String time = null;
        try {
            Socket soooooo = new Socket("time-A.timefreq.bldrdoc.gov", 13);
            try {
                InputStream inStream = soooooo.getInputStream();
                Scanner in = new Scanner(inStream);
                while (in.hasNextLine()) {
                    String line = in.nextLine();
                    System.out.println(line);
                    time = line;
                }
            } finally {
                soooooo.close();
            }
        } catch (IOException ioexc) {
            ioexc.printStackTrace();
        }
        return time;

    }

}
