package triangle.feeder36.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;

import triangle.feeder36.Log.TLog;
import triangle.feeder36.LoginRequest;
import triangle.feeder36.R;

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
                Log.i(TLog.TAG, "submit button clicked");
                Runnable login_request_code = new LoginRequest();
                Thread login_request = new Thread(login_request_code);
                login_request.start();
            }
        });
    }
}
