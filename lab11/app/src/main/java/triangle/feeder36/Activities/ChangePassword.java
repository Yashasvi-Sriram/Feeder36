package triangle.feeder36.Activities;

import android.content.Intent;
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

public class ChangePassword extends AppCompatActivity {

    db dbManager;
    Vector<UserInfo> users;
    ScrollView change_password;
    EditText user_name, old_password, new_password, confirm_new_password;
    String post_user_name, post_old_password, post_new_password;
    Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password);

        /* Getting references */
        change_password = (ScrollView) findViewById(R.id.change_password);
        user_name = (EditText) change_password.findViewById(R.id.user_name);
        old_password = (EditText) change_password.findViewById(R.id.old_password);
        new_password = (EditText) change_password.findViewById(R.id.new_password);
        confirm_new_password = (EditText) change_password.findViewById(R.id.confirm_new_password);
        submit = (Button) change_password.findViewById(R.id.submit);
        /* submit listener */
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!new_password.getText().toString().equals(confirm_new_password.getText().toString())) {
                    Toast.makeText(ChangePassword.this, "Passwords don't match", Toast.LENGTH_SHORT).show();
                    return;
                }
                post_user_name = user_name.getText().toString();
                post_old_password = old_password.getText().toString();
                post_new_password = new_password.getText().toString();
                new HTTPChangePasswordRequest().execute(post_user_name, post_old_password, post_new_password, IPSource.changePasswordURL());
            }
        });
    }


    private class HTTPChangePasswordRequest extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String[] params) {
            try {
                // Prepare POST variables containing credentials
                String credentials =
                        URLEncoder.encode("user_name", "UTF-8")
                                + "="
                                + URLEncoder.encode(params[0], "UTF-8")
                                + "&"
                                + URLEncoder.encode("old_password", "UTF-8")
                                + "="
                                + URLEncoder.encode(params[1], "UTF-8")
                                + "&"
                                + URLEncoder.encode("new_password", "UTF-8")
                                + "="
                                + URLEncoder.encode(params[2], "UTF-8");

                // Prepare URL
                URL login_request_page = new URL(params[3]);
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
                    Toast.makeText(ChangePassword.this, "Change Successful", Toast.LENGTH_SHORT).show();

                    dbManager = new db(ChangePassword.this, db.DB_NAME, null, db.DB_VERSION);
                    users = dbManager.getAllUsers();

                    if (users.size() == 0) {
                        UserInfo newUser = new UserInfo(post_user_name, post_new_password);
                        dbManager.insert(newUser);
                    }
                    else if (!users.get(0).USER_NAME.matches(post_user_name) || !users.get(0).PASSWORD.matches(post_new_password)) {
                        UserInfo newUser = new UserInfo(post_user_name, post_new_password);
                        dbManager.updateEntryWithKeyValue(newUser, db.TABLES.USER_INFO.USER_NAME, users.get(0).USER_NAME);
                    }

                    /* Take to home screen */
                    Intent homeScreen = new Intent(ChangePassword.this, Home.class);
                    startActivity(homeScreen);
                    break;
                case "0":
                    Toast.makeText(ChangePassword.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                    break;
                case "-1":
                    Toast.makeText(ChangePassword.this, "Password Change Failed", Toast.LENGTH_SHORT).show();
                    break;
            }
        }

        @Override
        protected void onCancelled() {
            Log.i(TLog.TAG, "change_password cancelled ");
        }

    }


}
