package triangle.feeder36.Activities;

import android.content.Context;
import android.content.Intent;
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
import java.util.Vector;

import triangle.feeder36.DB.Def.UserInfo;
import triangle.feeder36.DB.Helpers.db;
import triangle.feeder36.Log.TLog;
import triangle.feeder36.R;
import triangle.feeder36.ServerTalk.IPSource;

public class Login extends AppCompatActivity {

    ScrollView login;
    EditText user_name, password, specific_ip;
    Button submit;

    String post_user_name;
    String post_password;

    db dbManager;
    Vector<UserInfo> users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* Checking for stored users */
        dbManager = new db(this, db.DB_NAME, null, db.DB_VERSION);
        users = dbManager.getAllUsers();

        /* At least One User stored */
        if (users.size() > 0) {
            // We are assuming one user per device TODO: Maybe implement multiple users
            /* HTTPLoginRequest if a PROPER user_name exists */
            if (!users.get(0).USER_NAME.equals("")) {
                /* NOTE : We are assuming that the stored user is a valid account at this point of time
                 TODO : Practically it may not be the case, So while sync-ing and sending response to feed back .
                 TODO : Tally the credentials of the stored user */

                /* Take to home screen */
                Intent homeScreen = new Intent(Login.this, Home.class);
                startActivity(homeScreen);
            }
        }

        /* If no Users stored */
        setContentView(R.layout.login);
        /* Getting references */
        login = (ScrollView) findViewById(R.id.login);
        user_name = (EditText) login.findViewById(R.id.user_name);
        password = (EditText) login.findViewById(R.id.password);
        specific_ip = (EditText) login.findViewById(R.id.specific_ip);
        submit = (Button) login.findViewById(R.id.submit);

        /* Submit listener */
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!connectedToNetwork()) {
                    Toast.makeText(Login.this, "Network Needed", Toast.LENGTH_SHORT).show();
                    return;
                }
                post_user_name = user_name.getText().toString();
                post_password = password.getText().toString();
                String ip = IPSource.loginURL();
                if (!specific_ip.getText().toString().matches("")){
                    IPSource.resetBase(specific_ip.getText().toString(),null);
                    ip = IPSource.loginURL();
                }
                new HTTPLoginRequest().execute(post_user_name, post_password, ip);
            }
        });
    }

    @Override
    public void onBackPressed() {}

    private boolean connectedToNetwork() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    private class HTTPLoginRequest extends AsyncTask<String, String, String> {

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
                wr.write(credentials);
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

            } catch (IOException e) {
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
            switch (result) {
                case "1":
                    Toast.makeText(Login.this, "Login Successful", Toast.LENGTH_SHORT).show();

                    if(users.size() == 0){
                        UserInfo newUser = new UserInfo(post_user_name, post_password);
                        dbManager.insert(newUser);
                    }
                    else if(!users.get(0).USER_NAME.matches(post_user_name) || !users.get(0).PASSWORD.matches(post_password)) {
                        UserInfo newUser = new UserInfo(post_user_name, post_password);
                        dbManager.updateEntryWithKeyValue(newUser, db.TABLES.USER_INFO.USER_NAME, users.get(0).USER_NAME);
                    }
                    /* Take to home screen */
                    Intent homeScreen = new Intent(Login.this, Home.class);
                    startActivity(homeScreen);
                    break;
                case "0":
                    Toast.makeText(Login.this, "Invalid user or password", Toast.LENGTH_SHORT).show();
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

    }

}