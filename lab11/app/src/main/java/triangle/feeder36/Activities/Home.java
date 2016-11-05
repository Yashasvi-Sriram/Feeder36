package triangle.feeder36.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import triangle.feeder36.CustomAdapters.FeedbackItem;
import triangle.feeder36.CustomAdapters.TaskItem;
import triangle.feeder36.DB.Def.CourseDef;
import triangle.feeder36.DB.Def.FeedbackFormDef;
import triangle.feeder36.DB.Def.FeedbackResponseDef;
import triangle.feeder36.DB.Def.TaskDef;
import triangle.feeder36.DB.Def.UserInfo;
import triangle.feeder36.DB.Helpers.Helper;
import triangle.feeder36.DB.Helpers.db;
import triangle.feeder36.Log.TLog;
import triangle.feeder36.R;
import triangle.feeder36.ServerTalk.IPSource;

public class Home extends AppCompatActivity {

    AccountManager account;
    CaldroidAndLists caldroidAndLists;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        /* Initializes Logout Manager */
        account = new AccountManager();

        /* Initializes Caldroid View */
        caldroidAndLists = new CaldroidAndLists();
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
                new SyncDataBases().execute(IPSource.syncURL(), IPSource.responseSubmitURL());
                break;
            case R.id.home_menu_filters:
                Intent filters = new Intent(Home.this, Filters.class);
                startActivity(filters);
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
                    Vector<FeedbackResponseDef> feedbackResponsesVec = dbManager.getAllFeedbackResponses();
                    int count = 0;

                    for (int i = 0; i < feedbackResponsesVec.size(); i++) {
                        FeedbackResponseDef feedbackResponsesVec_i = feedbackResponsesVec.get(i);
                        if (feedbackResponsesVec_i.SUBMIT_STATUS == 0) count++;
                    }

                    if (count != 0) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(Home.this);
                        builder.setTitle("Warning !!");
                        builder.setInverseBackgroundForced(true);
                        String warningMsg = "You have some unsubmitted feedbacks which will be lost after logging out..." + "\n"
                                + "Do you wish to proceed ?";
                        builder.setMessage(warningMsg);

                        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                UserInfo user = new UserInfo("", "");
                                dbManager.updateEntryWithKeyValue(user, Helper.PRIMARY_KEY, "1");
                                dbManager.resetFeedbackResponsesTable();
                            /* Take back to Login screen */
                                Toast.makeText(Home.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
                                Intent login = new Intent(Home.this, Login.class);
                                startActivity(login);
                            }
                        });

                        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                        AlertDialog alert = builder.create();
                        alert.show();
                    } else {
                        UserInfo user = new UserInfo("", "");
                        dbManager.updateEntryWithKeyValue(user, Helper.PRIMARY_KEY, "1");
                        dbManager.resetFeedbackResponsesTable();
                            /* Take back to Login screen */
                        Toast.makeText(Home.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
                        Intent login = new Intent(Home.this, Login.class);
                        startActivity(login);
                    }
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
                Log.i(TLog.TAG, "HTTPUrlConnection connecting to " + params[0] + " to sync db ");
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
                // Closing writer
                wr.close();
                // Closing reader
                reader.close();
                // Closing Connection
                conn.disconnect();

                // json response string
                String json_string = json_string_builder.toString();

                this.startSync(json_string);


                /*------------------------------------------------------------------------------------*/
                if (!json_string.equals("0") && !json_string.equals("-1")) {
                    JSONArray responsesToBeSubmitted = dbManager.getFeedbackResponsesToBeSubmitted();
                    Log.i(TLog.TAG, responsesToBeSubmitted.toString());
                    String responses_post =
                            URLEncoder.encode("user_name", "UTF-8")
                                    + "="
                                    + URLEncoder.encode(post_user_name, "UTF-8")
                                    + "&"
                                    + URLEncoder.encode("password", "UTF-8")
                                    + "="
                                    + URLEncoder.encode(post_password, "UTF-8")
                                    + "&"
                                    + URLEncoder.encode(FeedbackResponseDef.JSONResponseKeys.FEEDBACK_RESPONSE_DICT, "UTF-8")
                                    + "="
                                    + URLEncoder.encode(responsesToBeSubmitted.toString(), "UTF-8");

                    // Prepare URL
                    URL response_submit_page = new URL(params[1]);
                    // Open Connection
                    Log.i(TLog.TAG, "HTTPUrlConnection connecting to " + params[1] + " to send responses ");
                    HttpURLConnection response_submit_conn = (HttpURLConnection) response_submit_page.openConnection();
                    // This connection can send data to server
                    response_submit_conn.setDoInput(true);
                    // The request is of POST type
                    response_submit_conn.setRequestMethod("POST");

                    // POST show_feedback responses to server
                    OutputStreamWriter response_writer = new OutputStreamWriter(response_submit_conn.getOutputStream());
                    response_writer.write(credentials);
                    response_writer.write(responses_post);
                    response_writer.flush();

                    // get the response from the server
                    BufferedReader response_reader = new BufferedReader(new InputStreamReader(response_submit_conn.getInputStream()));
                    // using a StringBuilder
                    StringBuilder response_string_builder = new StringBuilder();
                    while ((buffer_line = response_reader.readLine()) != null) {
                        response_string_builder.append(buffer_line);
                    }
                    String successful_submit_str = response_string_builder.toString();
                    JSONArray successful_submit_json = new JSONArray(successful_submit_str);
                    for (int i = 0; i < successful_submit_json.length(); i++) {
                        dbManager.markFeedbackResponseAsSubmitted(String.valueOf(successful_submit_json.get(i)));
                        Log.i(TLog.TAG, "submitFeedbackResponses: response submitted with show_feedback form pk " + String.valueOf(successful_submit_json.get(i)));
                    }

                    // Closing writer
                    response_writer.close();
                    // Closing reader
                    reader.close();
                    // Closing Connection
                    response_submit_conn.disconnect();
                }

                // return the response to onPostExecute
                return json_string;
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return "-1";
        }

        private void startSync(String json_string) {
            Log.i(TLog.TAG, json_string);
            try {

                JSONObject courses_tasks_fb_forms_fb_responses = new JSONObject(json_string);
                JSONArray courses = (JSONArray) courses_tasks_fb_forms_fb_responses.get(CourseDef.JSONResponseKeys.COURSES_DICT);
                JSONArray tasks = (JSONArray) courses_tasks_fb_forms_fb_responses.get(TaskDef.JSONResponseKeys.TASKS_DICT);
                JSONArray feedback_forms = (JSONArray) courses_tasks_fb_forms_fb_responses.get(FeedbackFormDef.JSONResponseKeys.FEEDBACK_FORM_DICT);
                JSONArray feedback_responses = (JSONArray) courses_tasks_fb_forms_fb_responses.get(FeedbackResponseDef.JSONResponseKeys.FEEDBACK_RESPONSE_DICT);
                this.syncCourses(this.prepareCoursesHashMap(courses), dbManager.prepareCoursesHashMap());
                this.syncTasks(this.prepareTasksHashMap(tasks), dbManager.prepareTasksHashMap());
                /* Forms should be synced before Responses */
                this.syncFeedbackForms(this.prepareFeedbackFormsHashMap(feedback_forms), dbManager.prepareFeedbackFormsHashMap());
                this.syncFeedbackResponses(this.prepareFeedbackResponsesHashMap(feedback_responses), dbManager.prepareFeedbackResponsesHashMap());

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
                    if (local_value.identical(remote_value)) {
                        // remove from remote hash
                        remote.remove(key);
                    }
                    // with different fields => updated at django
                    else {
                        // TODO: issue update notification
                        // update local db
                        Log.i(TLog.TAG, "syncCourses: update with name (previously) " + local_value.NAME);
                        dbManager.updateEntryWithKeyValue(remote_value, db.TABLES.COURSES.DJANGO_PK, String.valueOf(key));
                        // remove from remote hash
                        remote.remove(key);
                    }
                }
                // remote has no such entry => deleted at django
                else {
                    // TODO: issue delete notification
                    Log.i(TLog.TAG, "syncCourses: delete with name " + local_value.NAME);
                    dbManager.deleteEntryWithKeyValue(db.TABLES.COURSES.TABLE_NAME, db.TABLES.COURSES.DJANGO_PK, String.valueOf(key));
                }

            }
            // now going through remote table
            // now it has only new
            for (Map.Entry remote_entry : remote.entrySet()) {
                int key = (int) remote_entry.getKey();
                CourseDef new_value = (CourseDef) remote_entry.getValue();
                // TODO: issue create notification
                Log.i(TLog.TAG, "syncCourses: create with name " + new_value.NAME);
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
                    if (local_value.identical(remote_value)) {
                        // remove from remote hash
                        remote.remove(key);
                    }
                    // with different fields => updated at django
                    else {
                        // TODO: issue update notification
                        // update local db
                        Log.i(TLog.TAG, "syncTasks: update in with tag (previously) " + local_value.TAG);
                        dbManager.updateEntryWithKeyValue(remote_value, db.TABLES.TASKS.DJANGO_PK, String.valueOf(key));
                        // remove from remote hash
                        remote.remove(key);
                    }
                }
                // remote has no such entry => deleted at django
                else {
                    // TODO: issue delete notification
                    Log.i(TLog.TAG, "syncTasks: delete with tag " + local_value.TAG);
                    dbManager.deleteEntryWithKeyValue(db.TABLES.TASKS.TABLE_NAME, db.TABLES.TASKS.DJANGO_PK, String.valueOf(key));
                }

            }
            // now going through remote table
            // now it has only new
            for (Map.Entry remote_entry : remote.entrySet()) {
                int key = (int) remote_entry.getKey();
                TaskDef new_value = (TaskDef) remote_entry.getValue();
                // TODO: issue create notification
                Log.i(TLog.TAG, "syncTasks: create with tag " + new_value.TAG);
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
                    if (local_value.identical(remote_value)) {
                        // remove from remote hash
                        remote.remove(key);
                    }
                    // with different fields => updated at django
                    else {
                        // TODO: issue update notification
                        // update local db
                        Log.i(TLog.TAG, "syncFeedbackForms: update in with name (previously) " + local_value.NAME);
                        dbManager.updateEntryWithKeyValue(remote_value, db.TABLES.FEEDBACK_FORMS.DJANGO_PK, String.valueOf(key));
                        // remove from remote hash
                        remote.remove(key);
                    }
                }
                // remote has no such entry => deleted at django
                else {
                    // TODO: issue delete notification
                    Log.i(TLog.TAG, "syncFeedbackForms: delete with name " + local_value.NAME);
                    dbManager.deleteEntryWithKeyValue(db.TABLES.FEEDBACK_FORMS.TABLE_NAME, db.TABLES.FEEDBACK_FORMS.DJANGO_PK, String.valueOf(key));
                }

            }
            // now going through remote table
            // now it has only new
            for (Map.Entry remote_entry : remote.entrySet()) {
                int key = (int) remote_entry.getKey();
                FeedbackFormDef new_value = (FeedbackFormDef) remote_entry.getValue();
                // TODO: issue create notification
                Log.i(TLog.TAG, "syncFeedbackForms: create with name " + new_value.NAME);
                dbManager.insert(new_value);
            }
        }

        private void syncFeedbackResponses(HashMap<Integer, FeedbackResponseDef> remote, HashMap<Integer, FeedbackResponseDef> local) {
            // going through local table
            for (Map.Entry local_entry : local.entrySet()) {

                // current key
                int key = (int) local_entry.getKey();
                FeedbackResponseDef local_value = (FeedbackResponseDef) local_entry.getValue();

                // remote has that entry
                if (remote.get(key) != null) {

                    FeedbackResponseDef remote_value = remote.get(key);
                    // with same fields => no change
                    if (local_value.identical(remote_value)) {
                        // remove from remote hash
                        Log.i(TLog.TAG, "syncFeedbackResponses: found a response is in sync with show_feedback form pk " + local_value.FEEDBACK_FORM_PK + " submit status " + local_value.SUBMIT_STATUS);
                        remote.remove(key);
                    }
                    // with different fields => updated at django
                    else {
                        // TODO: issue update notification
                        // update local db
                        Log.i(TLog.TAG, "syncFeedbackResponses: (ERROR THIS SHOULD NOT HAPPEN!) update in with show_feedback form pk " + local_value.FEEDBACK_FORM_PK);
                        remote.remove(key);
                    }
                }
                // remote has no such entry => deleted at django
                else {
                    FeedbackFormDef remote_fb_form = dbManager.getFormOf(local_value);
                    if (remote_fb_form == null) {
                        // TODO: issue delete notification
                        Log.i(TLog.TAG, "syncFeedbackResponses: delete with show_feedback form pk " + local_value.FEEDBACK_FORM_PK);
                        dbManager.deleteEntryWithKeyValue(db.TABLES.FEEDBACK_RESPONSES.TABLE_NAME, db.TABLES.FEEDBACK_RESPONSES.FEEDBACK_FORM_PK, String.valueOf(key));
                    } else {
                        Log.i(TLog.TAG, "syncFeedbackResponses: found response to be submitted with show_feedback form pk " + local_value.FEEDBACK_FORM_PK + " submit status " + local_value.SUBMIT_STATUS);
                    }
                }

            }
            // now going through remote table
            // now it has only new
            for (Map.Entry remote_entry : remote.entrySet()) {
                int key = (int) remote_entry.getKey();
                FeedbackResponseDef new_value = (FeedbackResponseDef) remote_entry.getValue();
                // TODO: issue create notification
                Log.i(TLog.TAG, "syncFeedbackResponses: create with show_feedback form pk " + new_value.FEEDBACK_FORM_PK);
                dbManager.insert(new_value, 1);
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

        HashMap<Integer, FeedbackResponseDef> prepareFeedbackResponsesHashMap(JSONArray feedbackResponses) {
            HashMap<Integer, FeedbackResponseDef> remote = new HashMap<>();
            try {
                for (int i = 0; i < feedbackResponses.length(); i++) {
                    FeedbackResponseDef feedbackResponseDef = new FeedbackResponseDef((JSONObject) feedbackResponses.get(i));
                    remote.put(feedbackResponseDef.FEEDBACK_FORM_PK, feedbackResponseDef);
                }
            } catch (JSONException e) {
                Log.i(TLog.TAG, "not a valid json string to parse FeedbackResponses " + feedbackResponses.toString());
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
                    caldroidAndLists.reloadColoursForCalendar();
                    caldroidAndLists.onCaldroidSelectDate(new triangle.feeder36.Calender.Date(true));
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

    class CaldroidAndLists {
        ListView list_view_tasks, list_view_feedback;
        LinearLayout home, calendar_layout;
        TextView task_heading, feedback_heading;
        CaldroidFragment caldroidFragment;

        db dbManager;

        public CaldroidAndLists() {
            this.initCaldroid();
        }

        public void initCaldroid() {
            home = (LinearLayout) findViewById(R.id.home);
            list_view_tasks = (ListView) home.findViewById(R.id.list_view_tasks);
            list_view_feedback = (ListView) home.findViewById(R.id.list_view_feedback);
            calendar_layout = (LinearLayout) home.findViewById(R.id.calendar_layout);
            task_heading = (TextView) findViewById(R.id.task_heading);
            feedback_heading = (TextView) findViewById(R.id.feedback_heading);

            dbManager = new db(Home.this, db.DB_NAME, null, db.DB_VERSION);

            /* initialises CaldroidAndLists View */
            this.initialiseCaldroidView();
        }

        public void initialiseCaldroidView() {
            caldroidFragment = new CaldroidFragment();
            Bundle args = new Bundle();
            Calendar cal = Calendar.getInstance();
            args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
            args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
            args.putInt(CaldroidFragment.START_DAY_OF_WEEK, CaldroidFragment.MONDAY);
//            args.putInt(CaldroidFragment.THEME_RESOURCE, com.caldroid.R.style.CaldroidDefaultDark);
            caldroidFragment.setArguments(args);

            FragmentTransaction t = getSupportFragmentManager().beginTransaction();
            t.replace(R.id.calendar_layout, caldroidFragment);
            t.commit();

            caldroidFragment.setCaldroidListener(new CaldroidListener() {

                @Override
                public void onSelectDate(Date date, View view) {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(date);
                    int year = cal.get(Calendar.YEAR);
                    int month = cal.get(Calendar.MONTH) + 1;
                    int day = cal.get(Calendar.DAY_OF_MONTH);

                    onCaldroidSelectDate(new triangle.feeder36.Calender.Date(year, month, day));
                }

                @Override
                public void onChangeMonth(int month, int year) {

                }

                @Override
                public void onLongClickDate(Date date, View view) {

                }

                @Override
                public void onCaldroidViewCreated() {
                    /* Initialises the list view below the CalView with present day show_tasks */
                    onCaldroidSelectDate(new triangle.feeder36.Calender.Date(true));
                    reloadColoursForCalendar();
                }
            });
        }

        private void onCaldroidSelectDate(triangle.feeder36.Calender.Date date) {
            Vector<TaskDef> tasksOnDay = dbManager.getDayTasks(date, Home.this);
            Vector<CourseDef> coursesOfTasks = new Vector<>();
            for (int i = 0; i < tasksOnDay.size(); i++) {
                coursesOfTasks.add(dbManager.getCourseOf(tasksOnDay.get(i)));
            }
            Vector<FeedbackFormDef> feedbackFormsOnDay = dbManager.getDayFeedbackForms(date, Home.this);
            Vector<CourseDef> coursesOfFbForms = new Vector<>();
            for (int i = 0; i < feedbackFormsOnDay.size(); i++) {
                coursesOfFbForms.add(dbManager.getCourseOf(feedbackFormsOnDay.get(i)));
            }

            /* Set visibility of heading text views accordingly */
            if (tasksOnDay.size() != 0) {
                task_heading.setVisibility(View.VISIBLE);
            } else {
                task_heading.setVisibility(View.GONE);
            }

            if (feedbackFormsOnDay.size() != 0) {
                feedback_heading.setVisibility(View.VISIBLE);
            } else {
                feedback_heading.setVisibility(View.GONE);
            }

            list_view_tasks.setAdapter(new TaskItem(Home.this, tasksOnDay, coursesOfTasks));
            list_view_feedback.setAdapter(new FeedbackItem(Home.this, feedbackFormsOnDay, coursesOfFbForms));
            setListViewHeightBasedOnChildren(list_view_tasks);
            setListViewHeightBasedOnChildren(list_view_feedback);
        }

        private String convertToHexColorStr(int i) {
            if (i >= 0 && i <= 9) {
                return String.valueOf(i);
            } else {
            /* convert to number residues starting from zero */
                i -= 10;
            /* convert to corresponding ASCII */
                i += 65;
                return String.valueOf((char) i);
            }
        }

        private String myHashFn(String courseName) {
            int val = 0;

            for (int i = 0; i < courseName.length(); i++) {
                val += courseName.charAt(i);
            }

            String col = "#";

            for (int i = 0; i < 6; i++) {
                col += convertToHexColorStr(val % (16 - i));
            }
            return col;
        }

        private void reloadColoursForCalendar() {
            caldroidFragment.getBackgroundForDateTimeMap().clear();
            caldroidFragment.refreshView();
            HashMap<String, Vector<CourseDef>> courseDefHashMap = dbManager.getDateCourseDefHashMap(Home.this);
            String defaultMultiColoringCol = "000000";
            DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

            for (Map.Entry pair : courseDefHashMap.entrySet()) {
                String task_date_str_i = (String) pair.getKey();
                Vector<CourseDef> course_def_vec = (Vector<CourseDef>) pair.getValue();
                HashMap<String, Integer> courseCodeHashMap = new HashMap<>();

                for (int i = 0; i < course_def_vec.size(); i++) {
                    CourseDef course_i = course_def_vec.get(i);
                    courseCodeHashMap.put(course_i.CODE, 1);
                }

                if (courseCodeHashMap.size() == 1) {
                    CourseDef course_i = course_def_vec.get(0);

                    String color_hex_i = myHashFn(course_i.NAME);
                    int color_int_i = Color.parseColor(color_hex_i);

                    Date task_date_i = null;
                    try {
                        task_date_i = formatter.parse(task_date_str_i);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    ColorDrawable color_for_course_i = new ColorDrawable(color_int_i);
                    caldroidFragment.setBackgroundDrawableForDate(color_for_course_i, task_date_i);
                }
                else if (courseCodeHashMap.size() != 0) {
                    Date course_date_i = null;
                    try {
                        course_date_i = formatter.parse(task_date_str_i);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    String transparency = determineTransparencyLightTheme(course_def_vec.size());

                    String col = "#" + transparency + defaultMultiColoringCol;
                    Log.i(TLog.TAG, transparency + " " + task_date_str_i);
                    ColorDrawable defaultCol = new ColorDrawable(Color.parseColor(col));
                    caldroidFragment.setBackgroundDrawableForDate(defaultCol, course_date_i);
                }
            }
        }

        /* Assuming light theme :( */
        private String determineTransparencyLightTheme(int num) {
            /* Diff must be 45 for the code to work */
            int minOp = 10;
            int maxOp = 55;

            /* table for opacity varying from 0% to 100% */
            String[] transparencyTable = {"00", "0D", "1A", "26", "33", "40", "4D", "59", "66", "73", "80"
                    , "8C", "99", "A6", "B3", "BF", "CC", "D9", "E6", "F2", "FF"};
            int ind = minOp / 5 + num - 2;
            return transparencyTable[ind];
        }
    }

    /****
     * Method for Setting the Height of the ListView dynamically.
     * *** Hack to fix the issue of not showing all the items of the ListView
     * *** when placed inside a ScrollView
     ****/
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, LinearLayout.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

}