package triangle.feeder36.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import triangle.feeder36.DB.Def.UserInfo;
import triangle.feeder36.DB.Helpers.Helper;
import triangle.feeder36.DB.Helpers.db;
import triangle.feeder36.R;

import static triangle.feeder36.R.id.user_name;

public class home_screen extends AppCompatActivity {

    Button log_out;
    db myDBHelper;
    String USER_INFO_TABLE = "user_info";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen);

        log_out = (Button) findViewById(R.id.log_out);
        myDBHelper = new db(this,db.DB_NAME,null,db.DB_VERSION);

        log_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserInfo myUser = new UserInfo("","");
                myDBHelper.updateEntryWithKeyValue(myUser,USER_INFO_TABLE, Helper.PRIMARY_KEY,"1");

                /* Take back to login screen */
                Intent changeActivity = new Intent(home_screen.this,Login.class);
                startActivity(changeActivity);
                Toast.makeText(home_screen.this,"Logged out successfully",Toast.LENGTH_SHORT).show();
            }
        });
    }

    /* Preventing going to login screen on pressing back button */
    @Override
    public void onBackPressed() {

        return;
    }
}