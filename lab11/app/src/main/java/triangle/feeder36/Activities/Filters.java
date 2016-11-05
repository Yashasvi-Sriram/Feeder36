package triangle.feeder36.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.Vector;

import triangle.feeder36.DB.Def.CourseDef;
import triangle.feeder36.DB.Helpers.db;
import triangle.feeder36.R;

public class Filters extends AppCompatActivity {

    db dbManager;

    CheckBox show_tasks, show_feedback;
    RelativeLayout filters;
    LinearLayout courses;
    Button submit;

    Vector<CourseDef> courseDefs;

    SharedPreferences shPreferences;

    public static String DELIMITER = "`";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filters);

        new DBOperations().execute();
    }

    private void reloadFiltersFromSharedPreferences() {

        int show_tasks_value = shPreferences.getInt(getString(R.string.show_tasks), 1);
        int show_feedback_value = shPreferences.getInt(getString(R.string.show_feedback), 1);

        if (show_tasks_value == 0) {
            this.show_tasks.setChecked(false);
        } else if (show_tasks_value == 1) {
            this.show_tasks.setChecked(true);
        }

        if (show_feedback_value == 0) {
            this.show_feedback.setChecked(false);
        } else if (show_feedback_value == 1) {
            this.show_feedback.setChecked(true);
        }

        String no_such_key = getString(R.string.no_such_key);
        String filtered_courses_str = shPreferences.getString(getString(R.string.filtered_courses), no_such_key);

        if (!filtered_courses_str.equals(no_such_key)) {
            /* if there is a stored information about filtering  */
            String[] filtered_courses_array = filtered_courses_str.split(DELIMITER);
            for (int i = 0; i < courses.getChildCount(); i++) {
                /* check the already selected courses */
                CheckBox checkBox = (CheckBox) filters.findViewById(i);
                for (String filtered_course : filtered_courses_array) {

                    if (filtered_course.equals(checkBox.getHint().toString())) {
                        checkBox.setChecked(true);
                        break;
                    }
                }
            }

        }
        else {
            /* show all courses */
            for (int i = 0; i < courses.getChildCount(); i++) {
                /* check the already selected courses */
                CheckBox checkBox = (CheckBox) filters.findViewById(i);
                checkBox.setChecked(true);
            }
        }


    }

    private void saveFiltersToSharedPreferences() {

        SharedPreferences.Editor shEditor = shPreferences.edit();

        if (show_tasks.isChecked()) {
            shEditor.putInt(getString(R.string.show_tasks), 1);
        } else {
            shEditor.putInt(getString(R.string.show_tasks), 0);
        }

        if (show_feedback.isChecked()) {
            shEditor.putInt(getString(R.string.show_feedback), 1);
        } else {
            shEditor.putInt(getString(R.string.show_feedback), 0);
        }

        /* store in shared preferences */
        String filtered_courses_str = "";
        int noChildren = courses.getChildCount();

        for (int i = 0; i < noChildren; i++) {
            CheckBox checkBox = (CheckBox) filters.findViewById(i);

            if (checkBox.isChecked()) {
                if (i == noChildren - 1)
                    filtered_courses_str += checkBox.getHint().toString();
                else
                    filtered_courses_str += checkBox.getHint().toString() + DELIMITER ;
            }
        }

        shEditor.putString(getString(R.string.filtered_courses), filtered_courses_str);
        shEditor.apply();

        Toast.makeText(this, "Filters saved", Toast.LENGTH_SHORT).show();

    }

    private class DBOperations extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String[] params) {
            dbManager = new db(Filters.this, db.DB_NAME, null, db.DB_VERSION);
            courseDefs = dbManager.getAllCourses();
            shPreferences = Filters.this.getSharedPreferences(getString(R.string.preferences_file), Context.MODE_PRIVATE);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            filters = (RelativeLayout) findViewById(R.id.filters);
            show_tasks = (CheckBox) filters.findViewById(R.id.show_tasks);
            show_feedback = (CheckBox) filters.findViewById(R.id.show_feedback);
            courses = (LinearLayout) filters.findViewById(R.id.courses);


            for (int i = 0; i < courseDefs.size(); i++) {
                LinearLayout row = new LinearLayout(Filters.this);
                row.setOrientation(LinearLayout.HORIZONTAL);

                CheckBox course = new CheckBox(Filters.this);
                course.setId(i);
                course.setHint(courseDefs.get(i).CODE);

                /* Assigning children */
                row.addView(course);
                courses.addView(row);
            }

            reloadFiltersFromSharedPreferences();

            submit = (Button) filters.findViewById(R.id.submit);
            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveFiltersToSharedPreferences();
                    Intent home = new Intent(Filters.this, Home.class);
                    startActivity(home);
                }
            });

        }

    }


}
