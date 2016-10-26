package triangle.feeder36.Activities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import triangle.feeder36.Log.TLog;
import triangle.feeder36.R;
import triangle.feeder36.ServerTalk.IPSource;

public class Login extends AppCompatActivity {

    ScrollView login;
    EditText user_name,password;
    Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        /* Getting references */
        login = (ScrollView) findViewById(R.id.login);
        user_name = (EditText) login.findViewById(R.id.user_name);
        password = (EditText) login.findViewById(R.id.password);
        submit = (Button) login.findViewById(R.id.submit);

        /* Submit listener */
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!connectedToNetwork()){
                    Toast.makeText(Login.this, "Network Needed", Toast.LENGTH_SHORT).show();
                    return;
                }
                HTTPLoginRequest loginRequest = new HTTPLoginRequest();
                loginRequest.execute(user_name.getText().toString(),password.getText().toString(), IPSource.getLoginURL());
            }
        });
    }

    private boolean connectedToNetwork() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    private class HTTPLoginRequest extends AsyncTask<String,String,String>{

        @Override
        protected String doInBackground(String[] params) {

            try {
                // Prepare POST variables containing credentials
                String credentials =
                        URLEncoder.encode("user_name", "UTF-8")
                        + "="
                        + URLEncoder.encode(params[0], "UTF-8")
                        + "&"
                        + URLEncoder.encode("password", "UTF-8")
                        + "="
                        + URLEncoder.encode(params[1], "UTF-8");

                // Prepare URL
                URL login_request_page = new URL(params[2]);
                // Open Connection
                HttpURLConnection conn = (HttpURLConnection) login_request_page.openConnection();
                // This connection can send data to server
                conn.setDoInput(true);
                // The request is of POST type
                conn.setRequestMethod("POST");

                // POST credentials to server
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write( credentials );
                wr.flush();

                // get the response from the server
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                // using a StringBuilder
                StringBuilder string_response = new StringBuilder();
                String buffer_line;
                while ((buffer_line = reader.readLine()) != null) {
                    string_response.append(buffer_line);
                }
                reader.close();

                // return the response to onPostExecute
                return string_response.toString();

            }
            catch (IOException e) {
                e.printStackTrace();
            }
            return "-1";
        }

        @Override
        protected void onPostExecute(String result) {
            // The result is
            // 1 if user exists
            // 0 if not
            // -1 if some other error occurred
            // TODO : implement code here
            switch (result) {
                case "1":
                    Toast.makeText(Login.this, "Login Successful", Toast.LENGTH_SHORT).show();
                    break;
                case "0":
                    Toast.makeText(Login.this, "Credentials Invalid", Toast.LENGTH_SHORT).show();
                    break;
                case "-1":
                    Toast.makeText(Login.this, "Login Failed", Toast.LENGTH_SHORT).show();
                    break;
            }
        }

        @Override
        protected void onCancelled() {
            Log.i(TLog.TAG, "login cancelled ");
        }

        @Override
        protected void onProgressUpdate(String[] values) {
            Log.i(TLog.TAG, values[0]);
            Toast.makeText(Login.this, values[0], Toast.LENGTH_SHORT).show();
        }
    }


}
