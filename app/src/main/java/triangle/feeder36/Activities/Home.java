package triangle.feeder36.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import triangle.feeder36.DB.Def.UserInfo;
import triangle.feeder36.DB.Helpers.Helper;
import triangle.feeder36.DB.Helpers.db;
import triangle.feeder36.R;


public class Home extends AppCompatActivity {

    Button log_out;
    db dbManager;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        log_out = (Button) findViewById(R.id.log_out);
        dbManager = new db(this,db.DB_NAME,null,db.DB_VERSION);

        log_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserInfo myUser = new UserInfo("","");
                dbManager.updateEntryWithKeyValue(myUser, Helper.PRIMARY_KEY,"1");

                /* Take back to Login screen */
                Toast.makeText(Home.this,"Logged out successfully",Toast.LENGTH_SHORT).show();
                Intent login = new Intent(Home.this,Login.class);
                startActivity(login);
            }
        });
    }

    /* Back button disabled */
    @Override
    public void onBackPressed() {}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.home_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home_menu_chg_pwd:
                Intent changePassword = new Intent(Home.this, ChangePassword.class);
                startActivity(changePassword);
                break;
            default:
                break;
        }
        return true;
    }


}