package triangle.feeder36;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import triangle.feeder36.Log.TLog;

public class LoginRequest implements Runnable {

    String ip = "localhost";
    String portNum = "80";
    String path = "~sharat/current/cs251/Assign/Lab09/support/Prince.php";

    public LoginRequest() {

    }

    @Override
    public void run() {

        try {
            URL login_request_page = new URL("http://" + ip + ":" + portNum + "/" + path);
            HttpURLConnection conn = (HttpURLConnection) login_request_page.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                Log.i(TLog.TAG, inputLine);
            }
            in.close();

        } catch (MalformedURLException e) {
            Log.i(TLog.TAG, "url incorrect");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
