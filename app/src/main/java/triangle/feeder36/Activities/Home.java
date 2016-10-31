package triangle.feeder36.Activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import triangle.feeder36.Calender.DateTime;
import triangle.feeder36.CustomAdapters.FeedbackItem;
import triangle.feeder36.CustomAdapters.TaskItem;
import triangle.feeder36.DB.Def.CourseDef;
import triangle.feeder36.DB.Def.FeedbackFormDef;
import triangle.feeder36.DB.Def.TaskDef;
import triangle.feeder36.DB.Def.UserInfo;
import triangle.feeder36.DB.Helpers.Helper;
import triangle.feeder36.DB.Helpers.db;
import triangle.feeder36.Log.TLog;
import triangle.feeder36.R;
import triangle.feeder36.ServerTalk.IPSource;

public class Home extends AppCompatActivity {

    AccountManager account;
    ListView list_view_tasks,list_view_feedback;
    LinearLayout home,calendar_layout;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        home = (LinearLayout) findViewById(R.id.home);
        list_view_tasks = (ListView) home.findViewById(R.id.list_view_tasks);
        list_view_feedback = (ListView) home.findViewById(R.id.list_view_feedback);
        calendar_layout = (LinearLayout) home.findViewById(R.id.calendar_layout);

        /* initialises CalView using Caldroid */
        initialiseCalView();

        /* Initializing Logout Manager */
        account = new AccountManager();

        /* Initialises the list view below the CalView with present day tasks */
        initialiseTasksWithPresentDate();
    }

    /* Back button disabled */
    @Override
    public void onBackPressed() {
    }

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
                account.onOptionsItemSelected(item);
                break;
            case R.id.home_menu_logout:
                account.onOptionsItemSelected(item);
                break;
            case R.id.home_menu_synchronize:
                new SyncDataBases().execute(IPSource.syncURL());
                break;
            default:
                break;
        }
        return true;
    }

    class AccountManager {

        db dbManager;

        public AccountManager() {
            this.initLogoutManager();
        }

        public void initLogoutManager() {
            dbManager = new db(Home.this, db.DB_NAME, null, db.DB_VERSION);
        }

        public void onOptionsItemSelected(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.home_menu_chg_pwd:
                    Intent changePassword = new Intent(Home.this, ChangePassword.class);
                    startActivity(changePassword);
                    break;
                case R.id.home_menu_logout:
                    UserInfo user = new UserInfo("", "");
                    dbManager.updateEntryWithKeyValue(user, Helper.PRIMARY_KEY, "1");

                    /* Take back to Login screen */
                    Toast.makeText(Home.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
                    Intent login = new Intent(Home.this, Login.class);
                    startActivity(login);
                default:
                    break;
            }
        }


    }

    private class SyncDataBases extends AsyncTask<String, String, String> {

        db dbManager;

        public SyncDataBases() {
            dbManager = new db(Home.this, db.DB_NAME, null, db.DB_VERSION);
        }

        @Override
        protected String doInBackground(String[] params) {
            Vector<UserInfo> users = dbManager.getAllUsers();

            String post_user_name = users.get(0).USER_NAME;
            String post_password = users.get(0).PASSWORD;

            try {
                // Prepare POST variables containing credentials
                String credentials =
                        URLEncoder.encode("user_name", "UTF-8")
                                + "="
                                + URLEncoder.encode(post_user_name, "UTF-8")
                                + "&"
                                + URLEncoder.encode("password", "UTF-8")
                                + "="
                                + URLEncoder.encode(post_password, "UTF-8");

                // Prepare URL
                URL login_request_page = new URL(params[0]);
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
                StringBuilder json_string_builder = new StringBuilder();
                String buffer_line;
                while ((buffer_line = reader.readLine()) != null) {
                    json_string_builder.append(buffer_line);
                }
                reader.close();
                // json response string
                String json_string = json_string_builder.toString();

                this.startSync(json_string);

                // return the response to onPostExecute
                return json_string_builder.toString();

            } catch (IOException e) {
                e.printStackTrace();
            }
            return "-1";
        }

        private void startSync(String json_string) {
            Log.i(TLog.TAG, json_string);
            try {

                JSONObject courses_tasks_fb_forms = new JSONObject(json_string);
                JSONArray courses = (JSONArray) courses_tasks_fb_forms.get(CourseDef.JSONResponseKeys.COURSES_DICT);
                JSONArray tasks = (JSONArray) courses_tasks_fb_forms.get(TaskDef.JSONResponseKeys.TASKS_DICT);
                JSONArray feedback_forms = (JSONArray) courses_tasks_fb_forms.get(FeedbackFormDef.JSONResponseKeys.FEEDBACK_FORM_DICT);
                this.syncCourses(this.prepareCoursesHashMap(courses), dbManager.prepareCoursesHashMap());
                this.syncTasks(this.prepareTasksHashMap(tasks), dbManager.prepareTasksHashMap());
                this.syncFeedbackForms(this.prepareFeedbackFormsHashMap(feedback_forms), dbManager.prepareFeedbackFormsHashMap());

            } catch (JSONException e) {
                e.printStackTrace();
                Log.i(TLog.TAG, "not a valid json string to parse Courses, Tasks, Fb Forms " + json_string);
            }

        }

        private void syncCourses(HashMap<Integer, CourseDef> remote, HashMap<Integer, CourseDef> local) {
            // going through local table
            for (Map.Entry local_entry : local.entrySet()) {

                // current key
                int key = (int) local_entry.getKey();
                CourseDef local_value = (CourseDef) local_entry.getValue();

                // remote has that entry
                if (remote.get(key) != null) {

                    CourseDef remote_value = remote.get(key);
                    // with same fields => no change
                    if (local_value.identical(remote_value)){
                        // remove from remote hash
                        remote.remove(key);
                    }
                    // with different fields => updated at django
                    else {
                        // TODO: issue update notification
                        // update local db
                        Log.i(TLog.TAG, "syncCourses: update with name (previously) " + local_value.NAME );
                        dbManager.updateEntryWithKeyValue(remote_value, db.TABLES.COURSES.DJANGO_PK, String.valueOf(key));
                        // remove from remote hash
                        remote.remove(key);
                    }
                }
                // remote has no such entry => deleted at django
                else {
                    // TODO: issue delete notification
                    Log.i(TLog.TAG, "syncCourses: delete with name " + local_value.NAME );
                    dbManager.deleteEntryWithKeyValue(db.TABLES.COURSES.TABLE_NAME, db.TABLES.COURSES.DJANGO_PK, String.valueOf(key));
                }

            }
            // now going through remote table
            // now it has only new
            for (Map.Entry remote_entry : remote.entrySet()) {
                int key = (int) remote_entry.getKey();
                CourseDef new_value = (CourseDef) remote_entry.getValue();
                // TODO: issue create notification
                Log.i(TLog.TAG, "syncCourses: create with name " + new_value.NAME );
                dbManager.insert(new_value);
            }

        }

        private void syncTasks(HashMap<Integer, TaskDef> remote, HashMap<Integer, TaskDef> local) {
            // going through local table
            for (Map.Entry local_entry : local.entrySet()) {

                // current key
                int key = (int) local_entry.getKey();
                TaskDef local_value = (TaskDef) local_entry.getValue();

                // remote has that entry
                if (remote.get(key) != null) {

                    TaskDef remote_value = remote.get(key);
                    // with same fields => no change
                    if (local_value.identical(remote_value)){
                        // remove from remote hash
                        remote.remove(key);
                    }
                    // with different fields => updated at django
                    else {
                        // TODO: issue update notification
                        // update local db
                        Log.i(TLog.TAG, "syncTasks: update in with tag (previously) " + local_value.TAG );
                        dbManager.updateEntryWithKeyValue(remote_value, db.TABLES.TASKS.DJANGO_PK, String.valueOf(key));
                        // remove from remote hash
                        remote.remove(key);
                    }
                }
                // remote has no such entry => deleted at django
                else {
                    // TODO: issue delete notification
                    Log.i(TLog.TAG, "syncTasks: delete with tag " + local_value.TAG );
                    dbManager.deleteEntryWithKeyValue(db.TABLES.TASKS.TABLE_NAME, db.TABLES.TASKS.DJANGO_PK, String.valueOf(key));
                }

            }
            // now going through remote table
            // now it has only new
            for (Map.Entry remote_entry : remote.entrySet()) {
                int key = (int) remote_entry.getKey();
                TaskDef new_value = (TaskDef) remote_entry.getValue();
                // TODO: issue create notification
                Log.i(TLog.TAG, "syncTasks: create with tag " + new_value.TAG );
                dbManager.insert(new_value);
            }


        }

        private void syncFeedbackForms(HashMap<Integer, FeedbackFormDef> remote, HashMap<Integer, FeedbackFormDef> local) {
            // going through local table
            for (Map.Entry local_entry : local.entrySet()) {

                // current key
                int key = (int) local_entry.getKey();
                FeedbackFormDef local_value = (FeedbackFormDef) local_entry.getValue();

                // remote has that entry
                if (remote.get(key) != null) {

                    FeedbackFormDef remote_value = remote.get(key);
                    // with same fields => no change
                    if (local_value.identical(remote_value)){
                        // remove from remote hash
                        remote.remove(key);
                    }
                    // with different fields => updated at django
                    else {
                        // TODO: issue update notification
                        // update local db
                        Log.i(TLog.TAG, "syncFeedbackForms: update in with name (previously) " + local_value.NAME );
                        dbManager.updateEntryWithKeyValue(remote_value, db.TABLES.FEEDBACK_FORMS.DJANGO_PK, String.valueOf(key));
                        // remove from remote hash
                        remote.remove(key);
                    }
                }
                // remote has no such entry => deleted at django
                else {
                    // TODO: issue delete notification
                    Log.i(TLog.TAG, "syncFeedbackForms: delete with name " + local_value.NAME );
                    dbManager.deleteEntryWithKeyValue(db.TABLES.FEEDBACK_FORMS.TABLE_NAME, db.TABLES.FEEDBACK_FORMS.DJANGO_PK, String.valueOf(key));
                }

            }
            // now going through remote table
            // now it has only new
            for (Map.Entry remote_entry : remote.entrySet()) {
                int key = (int) remote_entry.getKey();
                FeedbackFormDef new_value = (FeedbackFormDef) remote_entry.getValue();
                // TODO: issue create notification
                Log.i(TLog.TAG, "syncFeedbackForms: create with name " + new_value.NAME );
                dbManager.insert(new_value);
            }


        }

        HashMap<Integer, CourseDef> prepareCoursesHashMap(JSONArray courses) {
            HashMap<Integer, CourseDef> remote = new HashMap<>();
            try {
                for (int i = 0; i < courses.length(); i++) {
                    CourseDef courseDef = new CourseDef((JSONObject) courses.get(i));
                    remote.put(courseDef.DJANGO_PK, courseDef);
                }
            } catch (JSONException e) {
                Log.i(TLog.TAG, "not a valid json string to parse Courses " + courses.toString());
                e.printStackTrace();
            }
            return remote;
        }

        HashMap<Integer, TaskDef> prepareTasksHashMap(JSONArray tasks) {
            HashMap<Integer, TaskDef> remote = new HashMap<>();
            try {
                for (int i = 0; i < tasks.length(); i++) {
                    TaskDef taskDef = new TaskDef((JSONObject) tasks.get(i));
                    remote.put(taskDef.DJANGO_PK, taskDef);
                }
            } catch (JSONException e) {
                Log.i(TLog.TAG, "not a valid json string to parse Tasks " + tasks.toString());
                e.printStackTrace();
            }
            return remote;
        }

        HashMap<Integer, FeedbackFormDef> prepareFeedbackFormsHashMap(JSONArray feedbackForms) {
            HashMap<Integer, FeedbackFormDef> remote = new HashMap<>();
            try {
                for (int i = 0; i < feedbackForms.length(); i++) {
                    FeedbackFormDef feedbackFormDef = new FeedbackFormDef((JSONObject) feedbackForms.get(i));
                    remote.put(feedbackFormDef.DJANGO_PK, feedbackFormDef);
                }
            } catch (JSONException e) {
                Log.i(TLog.TAG, "not a valid json string to parse FeedbackForms " + feedbackForms.toString());
                e.printStackTrace();
            }
            return remote;
        }

        @Override
        protected void onPostExecute(String result) {
            // The result is
            // json string if everything is OK
            // 0 if user does not exist
            // -1 if some other error occurred
            switch (result) {
                case "0":
                    Toast.makeText(Home.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                    break;
                case "-1":
                    Toast.makeText(Home.this, "Sync Failed", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    Toast.makeText(Home.this, "Sync Completed", Toast.LENGTH_SHORT).show();
                    break;
            }
        }

        @Override
        protected void onCancelled() {
            Log.i(TLog.TAG, "Sync cancelled ");
        }

        @Override
        protected void onProgressUpdate(String[] values) {
            Toast.makeText(Home.this, values[0], Toast.LENGTH_SHORT).show();
        }
    }

    public void initialiseCalView() {
        CaldroidFragment caldroidFragment = new CaldroidFragment();
        Bundle args = new Bundle();
        Calendar cal = Calendar.getInstance();
        args.putInt(CaldroidFragment.MONTH,cal.get(Calendar.MONTH) + 1);
        args.putInt(CaldroidFragment.YEAR,cal.get(Calendar.YEAR));
        args.putInt(CaldroidFragment.START_DAY_OF_WEEK,CaldroidFragment.MONDAY);
        args.putInt(CaldroidFragment.THEME_RESOURCE, com.caldroid.R.style.CaldroidDefaultDark);
        caldroidFragment.setArguments(args);

        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.replace(R.id.calendar_layout,caldroidFragment);
        t.commit();

        final db dbManager = new db(this,db.DB_NAME,null,db.DB_VERSION);

        final CaldroidListener listener = new CaldroidListener() {

            @Override
            public void onSelectDate(Date date, View view) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH) + 1;
                int day = cal.get(Calendar.DAY_OF_MONTH);

                String clickedDate = String.valueOf(day) + "/" + String.valueOf(month) + "/" + String.valueOf(year);

                Vector<TaskDef> tasksOnDay = dbManager.getDayTasks(new triangle.feeder36.Calender.Date(clickedDate,"/"));

                int n = tasksOnDay.size();

                String [] taskCourseCode = new String[n];
                String [] taskCourseName = new String[n];
                String [] taskTag = new String[n];
                String [] taskDetail = new String[n];
                String [] taskDeadline = new String[n];

                for(int i=0;i<n;i++) {
                    TaskDef task_i = tasksOnDay.elementAt(i);
                    CourseDef course_i = dbManager.getCourseOf(task_i);

                    taskCourseCode[i] = course_i.CODE;
                    taskCourseName[i] = course_i.NAME;
                    taskTag[i] = task_i.TAG;
                    taskDetail[i] = task_i.DETAIL;
                    taskDeadline[i] = task_i.DEADLINE;
                }

                Vector<FeedbackFormDef> feedbacksOnDay = dbManager.getDayFeedbackForms(new triangle.feeder36.Calender.Date(clickedDate,"/"));

                int m = feedbacksOnDay.size();

                String [] feedbackCourseCode = new String[m];
                String [] feedbackCourseName = new String[m];
                String [] feedbackName = new String[m];
                String [] feedbackQuestionSet = new String[m];
                String [] feedbackDeadline = new String[m];

                for(int i=0;i<m;i++) {
                    FeedbackFormDef feedback_i = feedbacksOnDay.elementAt(i);
                    CourseDef course_i = dbManager.getCourseOf(feedback_i);

                    feedbackCourseCode[i] = course_i.CODE;
                    feedbackCourseName[i] = course_i.NAME;

                    feedbackName[i] = feedback_i.NAME;
                    feedbackQuestionSet[i] = feedback_i.QUESTION_SET;
                    feedbackDeadline[i] = feedback_i.DEADLINE;
                }

                list_view_tasks.setAdapter(new TaskItem(Home.this,taskCourseCode,taskCourseName,taskTag,taskDetail,taskDeadline));
                list_view_feedback.setAdapter(new FeedbackItem(Home.this,feedbackCourseCode,feedbackCourseName,feedbackName,feedbackQuestionSet,feedbackDeadline));
            }

            @Override
            public void onChangeMonth(int month, int year) {

            }

            @Override
            public void onLongClickDate(Date date, View view) {

            }

            @Override
            public void onCaldroidViewCreated() {

            }
        };
        caldroidFragment.setCaldroidListener(listener);
    }

    public void initialiseTasksWithPresentDate() {
        db dbManager = new db(this,db.DB_NAME,null,db.DB_VERSION);

        Calendar cal = Calendar.getInstance();

        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);

        String presentDate = String.valueOf(day) + "/" + String.valueOf(month) + "/" + String.valueOf(year);
        Vector<TaskDef> tasksOnDay = dbManager.getDayTasks(new triangle.feeder36.Calender.Date(presentDate,"/"));

        int n = tasksOnDay.size();

        String [] taskCourseCode = new String[n];
        String [] taskCourseName = new String[n];
        String [] taskTag = new String[n];
        String [] taskDetail = new String[n];
        String [] taskDeadline = new String[n];

        for(int i=0;i<n;i++) {
            TaskDef task_i = tasksOnDay.elementAt(i);
            CourseDef course_i = dbManager.getCourseOf(task_i);

            taskCourseCode[i] = course_i.CODE;
            taskCourseName[i] = course_i.NAME;
            taskTag[i] = task_i.TAG;
            taskDetail[i] = task_i.DETAIL;
            taskDeadline[i] = task_i.DEADLINE;
        }

        Vector<FeedbackFormDef> feedbacksOnDay = dbManager.getDayFeedbackForms(new triangle.feeder36.Calender.Date(presentDate,"/"));

        int m = feedbacksOnDay.size();

        String [] feedbackCourseCode = new String[m];
        String [] feedbackCourseName = new String[m];
        String [] feedbackName = new String[m];
        String [] feedbackQuestionSet = new String[m];
        String [] feedbackDeadline = new String[m];

        for(int i=0;i<m;i++) {
            FeedbackFormDef feedback_i = feedbacksOnDay.elementAt(i);
            CourseDef course_i = dbManager.getCourseOf(feedback_i);

            feedbackCourseCode[i] = course_i.CODE;
            feedbackCourseName[i] = course_i.NAME;

            feedbackName[i] = feedback_i.NAME;
            feedbackQuestionSet[i] = feedback_i.QUESTION_SET;
            feedbackDeadline[i] = feedback_i.DEADLINE;
        }

        list_view_tasks.setAdapter(new TaskItem(Home.this,taskCourseCode,taskCourseName,taskTag,taskDetail,taskDeadline));
        list_view_feedback.setAdapter(new FeedbackItem(Home.this,feedbackCourseCode,feedbackCourseName,feedbackName,feedbackQuestionSet,feedbackDeadline));
    }
}