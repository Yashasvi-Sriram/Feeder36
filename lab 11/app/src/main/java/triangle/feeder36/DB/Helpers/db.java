package triangle.feeder36.DB.Helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Vector;

import triangle.feeder36.Calender.Date;
import triangle.feeder36.Calender.DateTime;
import triangle.feeder36.Calender.Time;
import triangle.feeder36.DB.Def.CourseDef;
import triangle.feeder36.DB.Def.FeedbackFormDef;
import triangle.feeder36.DB.Def.FeedbackResponseDef;
import triangle.feeder36.DB.Def.TaskDef;
import triangle.feeder36.DB.Def.UserInfo;
import triangle.feeder36.Log.TLog;

public class db extends Helper {
    /**
     * DB name
     */
    public static final String DB_NAME = "feeder.db";
    /**
     * DB version
     */
    public static final int DB_VERSION = 1;


    public static final class TABLES {

        /**
         * does not close db connection
         */
        /* create table function*/
        public static void createTable(SQLiteDatabase db, String create_table_query) {
            db.execSQL(create_table_query);
            Log.i(TLog.TAG, "created table with query <<< " + create_table_query);
        }

        /**
         * does not close db connection
         */
        /* drop table function*/
        public static void dropTable(SQLiteDatabase db, String table_name) {
            db.execSQL("DROP TABLE IF EXISTS " + table_name + ";");
            Log.i(TLog.TAG, "dropped table with query <<< " + "DROP TABLE IF EXISTS " + table_name + ";");
        }

        /**
         * user info table
         */
        public static final class USER_INFO {
            /* table - name */
            public static final String TABLE_NAME = "user_info";

            /* table - columns */
            // PRIMARY KEY
            public static final String USER_NAME = "user_name";
            public static final String PASSWORD = "password";

            /* create table query */
            private static final String CREATE_TABLE_QUERY =
                    "CREATE TABLE "
                            + TABLE_NAME
                            + "("
                            + PRIMARY_KEY + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                            + USER_NAME + " VARCHAR(255) NOT NULL,"
                            + PASSWORD + " VARCHAR(255) NOT NULL"
                            + ")";
        }

        /**
         * Courses table
         */
        public static final class COURSES {
            /* table - name */
            public static final String TABLE_NAME = "courses";

            /* table - columns */
            // PRIMARY KEY
            public static final String CODE = "code";
            public static final String NAME = "name";
            public static final String DJANGO_PK = "django_pk";

            /* create table query */
            private static final String CREATE_TABLE_QUERY =
                    "CREATE TABLE "
                            + TABLE_NAME
                            + "("
                            + PRIMARY_KEY + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                            + DJANGO_PK + " INTEGER ,"
                            + CODE + " VARCHAR(255) NOT NULL,"
                            + NAME + " VARCHAR(255) NOT NULL"
                            + ");";
        }

        /**
         * Tasks table
         */
        public static final class TASKS {
            /* table - name */
            public static final String TABLE_NAME = "tasks";

            /* table - columns */
            // PRIMARY KEY
            public static final String DJANGO_PK = "django_pk";
            public static final String TAG = "tag";
            public static final String DEADLINE = "deadline";
            public static final String DETAIL = "detail";
            public static final String COURSE_PK = "course_pk";

            /* table query */
            private static final String CREATE_TABLE_QUERY =
                    "CREATE TABLE "
                            + TABLE_NAME
                            + "("
                            + PRIMARY_KEY + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                            + DJANGO_PK + " INTEGER ,"
                            + TAG + " VARCHAR(50) NOT NULL,"
                            + DEADLINE + " VARCHAR(20) NOT NULL,"
                            + DETAIL + " VARCHAR(400) NOT NULL,"
                            + COURSE_PK + " INTEGER " /* course's django pk */
                            + ");";
        }

        /**
         * Feedback Forms table
         */
        public static final class FEEDBACK_FORMS {
            /* table - name */
            public static final String TABLE_NAME = "feedback_forms";

            /* table - columns */
            // PRIMARY KEY
            public static final String DJANGO_PK = "django_pk";
            public static final String NAME = "name";
            public static final String DEADLINE = "deadline";
            public static final String QUESTION_SET = "question_set";
            public static final String COURSE_PK = "course_pk";

            /* table query */
            private static final String CREATE_TABLE_QUERY =
                    "CREATE TABLE "
                            + TABLE_NAME
                            + "("
                            + PRIMARY_KEY + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                            + DJANGO_PK + " INTEGER ,"
                            + NAME + " VARCHAR(50) NOT NULL,"
                            + DEADLINE + " VARCHAR(20) NOT NULL,"
                            + QUESTION_SET + " TEXT NOT NULL,"
                            + COURSE_PK + " INTEGER " /* course's django pk */
                            + ");";
        }

        /**
         * Feedback Responses table
         */
        public static final class FEEDBACK_RESPONSES {
            /* table - name */
            public static final String TABLE_NAME = "feedback_responses";

            /* table - columns */
            // PRIMARY KEY
            public static final String FEEDBACK_FORM_PK = "feedback_form_pk"; /* feedback_form's django pk */
            public static final String ANSWER_SET = "answer_set";
            public static final String COMMENT = "comment";
            public static final String SUBMIT_STATUS = "submit_status";

            /* table query */
            private static final String CREATE_TABLE_QUERY =
                    "CREATE TABLE "
                            + TABLE_NAME
                            + "("
                            + PRIMARY_KEY + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                            + FEEDBACK_FORM_PK + " INTEGER ,"
                            + COMMENT + " TEXT NOT NULL,"
                            + ANSWER_SET + " TEXT NOT NULL,"
                            + SUBMIT_STATUS + " INTEGER "
                            + ");";
        }

    }

    public db(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        super.onCreate(db);
        // create tables
        TABLES.createTable(db, TABLES.USER_INFO.CREATE_TABLE_QUERY);
        TABLES.createTable(db, TABLES.COURSES.CREATE_TABLE_QUERY);
        TABLES.createTable(db, TABLES.TASKS.CREATE_TABLE_QUERY);
        TABLES.createTable(db, TABLES.FEEDBACK_FORMS.CREATE_TABLE_QUERY);
        TABLES.createTable(db, TABLES.FEEDBACK_RESPONSES.CREATE_TABLE_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onUpgrade(db, oldVersion, newVersion);
        // drop tables
        TABLES.dropTable(db, TABLES.USER_INFO.TABLE_NAME);
        TABLES.dropTable(db, TABLES.COURSES.TABLE_NAME);
        TABLES.dropTable(db, TABLES.TASKS.TABLE_NAME);
        TABLES.dropTable(db, TABLES.FEEDBACK_FORMS.TABLE_NAME);
        TABLES.dropTable(db, TABLES.FEEDBACK_RESPONSES.TABLE_NAME);
        // create new ones
        onCreate(db);
    }

    /* User Info  */
    public void insert(UserInfo userInfo) {
        ContentValues values = new ContentValues();

        values.put(TABLES.USER_INFO.USER_NAME, userInfo.getUserName());
        values.put(TABLES.USER_INFO.PASSWORD, userInfo.getPASSWORD());

        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLES.USER_INFO.TABLE_NAME, null, values);
        db.close();
    }

    public void updateEntryWithKeyValue(UserInfo userInfo, String key, String value) {
        ContentValues values = new ContentValues();

        values.put(TABLES.USER_INFO.USER_NAME, userInfo.getUserName());
        values.put(TABLES.USER_INFO.PASSWORD, userInfo.getPASSWORD());

        SQLiteDatabase db = getWritableDatabase();
        String where = key + " = '" + value + "' ";
        db.update(TABLES.USER_INFO.TABLE_NAME, values, where, null);
        db.close();
    }

    public Vector<UserInfo> getAllUsers() {
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLES.USER_INFO.TABLE_NAME + ";";

        Vector<UserInfo> retVector = new Vector<>();

        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();

        while (!c.isAfterLast()) {
            UserInfo row = new UserInfo(c);
            retVector.add(row);
            c.moveToNext();
        }

        c.close();
        db.close();
        return retVector;
    }
    /* User Info  */

    /* Courses */
    public void insert(CourseDef courseDef) {
        ContentValues values = new ContentValues();

        values.put(TABLES.COURSES.DJANGO_PK , courseDef.DJANGO_PK);
        values.put(TABLES.COURSES.CODE , courseDef.CODE);
        values.put(TABLES.COURSES.NAME , courseDef.NAME);

        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLES.COURSES.TABLE_NAME, null, values);
        db.close();
    }

    public void updateEntryWithKeyValue(CourseDef courseDef, String key, String value) {
        ContentValues values = new ContentValues();

        values.put(TABLES.COURSES.DJANGO_PK, courseDef.DJANGO_PK);
        values.put(TABLES.COURSES.CODE, courseDef.CODE);
        values.put(TABLES.COURSES.NAME, courseDef.NAME);

        SQLiteDatabase db = getWritableDatabase();
        String where = key + " = '" + value + "' ";
        db.update(TABLES.COURSES.TABLE_NAME, values, where, null);
        db.close();
    }

    public Vector<CourseDef> getAllCourses() {
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLES.COURSES.TABLE_NAME + ";";

        Vector<CourseDef> retVector = new Vector<>();

        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();

        while (!c.isAfterLast()) {
            CourseDef row = new CourseDef(c);
            retVector.add(row);
            c.moveToNext();
        }

        c.close();
        db.close();
        return retVector;
    }

    public HashMap<Integer, CourseDef> prepareCoursesHashMap() {
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLES.COURSES.TABLE_NAME + ";";

        HashMap<Integer, CourseDef> local = new HashMap<>();

        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();

        while (!c.isAfterLast()) {
            CourseDef row = new CourseDef(c);
            local.put(row.DJANGO_PK, row);
            c.moveToNext();
        }

        c.close();
        db.close();
        return local;
    }
    /* Courses */

    /* Tasks */
    public void insert(TaskDef taskDef) {
        ContentValues values = new ContentValues();

        values.put(TABLES.TASKS.COURSE_PK , taskDef.COURSE_PK);
        values.put(TABLES.TASKS.DJANGO_PK , taskDef.DJANGO_PK);
        values.put(TABLES.TASKS.TAG , taskDef.TAG);
        values.put(TABLES.TASKS.DEADLINE , taskDef.DEADLINE);
        values.put(TABLES.TASKS.DETAIL , taskDef.DETAIL);

        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLES.TASKS.TABLE_NAME, null, values);
        db.close();
    }

    public void updateEntryWithKeyValue(TaskDef taskDef, String key, String value) {
        ContentValues values = new ContentValues();

        values.put(TABLES.TASKS.COURSE_PK , taskDef.COURSE_PK);
        values.put(TABLES.TASKS.DJANGO_PK , taskDef.DJANGO_PK);
        values.put(TABLES.TASKS.TAG , taskDef.TAG);
        values.put(TABLES.TASKS.DEADLINE , taskDef.DEADLINE);
        values.put(TABLES.TASKS.DETAIL , taskDef.DETAIL);

        SQLiteDatabase db = getWritableDatabase();
        String where = key + " = '" + value + "' ";
        db.update(TABLES.TASKS.TABLE_NAME, values, where, null);
        db.close();
    }

    public Vector<TaskDef> getAllTasks() {
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLES.TASKS.TABLE_NAME + ";";

        Vector<TaskDef> retVector = new Vector<>();

        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();

        while (!c.isAfterLast()) {
            TaskDef row = new TaskDef(c);
            retVector.add(row);
            c.moveToNext();
        }

        c.close();
        db.close();
        return retVector;
    }

    public HashMap<Integer, TaskDef> prepareTasksHashMap() {
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLES.TASKS.TABLE_NAME + ";";

        HashMap<Integer, TaskDef> local = new HashMap<>();

        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();

        while (!c.isAfterLast()) {
            TaskDef row = new TaskDef(c);
            local.put(row.DJANGO_PK, row);
            c.moveToNext();
        }

        c.close();
        db.close();
        return local;
    }

    public Vector<TaskDef> getDayTasks(Date date) {
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLES.TASKS.TABLE_NAME + ";";

        Vector<TaskDef> retVector = new Vector<>();

        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();

        while (!c.isAfterLast()) {
            TaskDef row = new TaskDef(c);
            DateTime row_deadline = new DateTime(row.DEADLINE,Date.SIMPLE_REPR_SEPARATOR, Time.SIMPLE_REPR_SEPARATOR,DateTime.SIMPLE_REPR_SEPARATOR);
            if(date.isEqualTo(row_deadline.$DATE)){
                retVector.add(row);
            }
            c.moveToNext();
        }

        c.close();
        db.close();
        return retVector;
    }

    public CourseDef getCourseOf(TaskDef taskDef){
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLES.COURSES.TABLE_NAME
                + " WHERE "
                + TABLES.COURSES.DJANGO_PK + " = " + taskDef.COURSE_PK + ";";

        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();

        if (!c.isAfterLast()) {
            return new CourseDef(c);
        }

        c.close();
        db.close();
        return null;
    }
    /* Tasks */

    /* Feedback forms */
    public void insert(FeedbackFormDef feedbackFormDef) {
        ContentValues values = new ContentValues();

        values.put(TABLES.FEEDBACK_FORMS.COURSE_PK , feedbackFormDef.COURSE_PK);
        values.put(TABLES.FEEDBACK_FORMS.DJANGO_PK , feedbackFormDef.DJANGO_PK);
        values.put(TABLES.FEEDBACK_FORMS.NAME , feedbackFormDef.NAME);
        values.put(TABLES.FEEDBACK_FORMS.DEADLINE , feedbackFormDef.DEADLINE);
        values.put(TABLES.FEEDBACK_FORMS.QUESTION_SET , feedbackFormDef.QUESTION_SET);

        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLES.FEEDBACK_FORMS.TABLE_NAME, null, values);
        db.close();
    }

    public void updateEntryWithKeyValue(FeedbackFormDef feedbackFormDef, String key, String value) {
        ContentValues values = new ContentValues();

        values.put(TABLES.FEEDBACK_FORMS.COURSE_PK , feedbackFormDef.COURSE_PK);
        values.put(TABLES.FEEDBACK_FORMS.DJANGO_PK , feedbackFormDef.DJANGO_PK);
        values.put(TABLES.FEEDBACK_FORMS.NAME , feedbackFormDef.NAME);
        values.put(TABLES.FEEDBACK_FORMS.DEADLINE , feedbackFormDef.DEADLINE);
        values.put(TABLES.FEEDBACK_FORMS.QUESTION_SET , feedbackFormDef.QUESTION_SET);

        SQLiteDatabase db = getWritableDatabase();
        String where = key + " = '" + value + "' ";
        db.update(TABLES.FEEDBACK_FORMS.TABLE_NAME, values, where, null);
        db.close();
    }

    public Vector<FeedbackFormDef> getAllFeedbackForms() {
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLES.FEEDBACK_FORMS.TABLE_NAME + ";";

        Vector<FeedbackFormDef> retVector = new Vector<>();

        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();

        while (!c.isAfterLast()) {
            FeedbackFormDef row = new FeedbackFormDef(c);
            retVector.add(row);
            c.moveToNext();
        }

        c.close();
        db.close();
        return retVector;
    }

    public HashMap<Integer, FeedbackFormDef> prepareFeedbackFormsHashMap() {
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLES.FEEDBACK_FORMS.TABLE_NAME + ";";

        HashMap<Integer, FeedbackFormDef> local = new HashMap<>();

        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();

        while (!c.isAfterLast()) {
            FeedbackFormDef row = new FeedbackFormDef(c);
            local.put(row.DJANGO_PK, row);
            c.moveToNext();
        }

        c.close();
        db.close();
        return local;
    }

    public Vector<FeedbackFormDef> getDayFeedbackForms(Date date) {
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLES.FEEDBACK_FORMS.TABLE_NAME + ";";

        Vector<FeedbackFormDef> retVector = new Vector<>();

        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();

        while (!c.isAfterLast()) {
            FeedbackFormDef row = new FeedbackFormDef(c);
            DateTime row_deadline = new DateTime(row.DEADLINE,Date.SIMPLE_REPR_SEPARATOR, Time.SIMPLE_REPR_SEPARATOR,DateTime.SIMPLE_REPR_SEPARATOR);
            if(date.isEqualTo(row_deadline.$DATE)){
                retVector.add(row);
            }
            c.moveToNext();
        }

        c.close();
        db.close();
        return retVector;
    }

    public CourseDef getCourseOf(FeedbackFormDef feedbackFormDef){
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLES.COURSES.TABLE_NAME
                + " WHERE "
                + TABLES.COURSES.DJANGO_PK + " = " + feedbackFormDef.COURSE_PK + ";";

        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();

        if (!c.isAfterLast()) {
            return new CourseDef(c);
        }

        c.close();
        db.close();
        return null;
    }

    public FeedbackResponseDef getResponseOf(FeedbackFormDef feedbackFormDef){
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLES.FEEDBACK_RESPONSES.TABLE_NAME
                + " WHERE "
                + TABLES.FEEDBACK_RESPONSES.FEEDBACK_FORM_PK + " = " + feedbackFormDef.DJANGO_PK + ";";

        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();

        if (!c.isAfterLast()) {
            return new FeedbackResponseDef(c);
        }

        c.close();
        db.close();
        return null;
    }
    /* Feedback forms */

    /* Feedback Responses */
    public void insert(FeedbackResponseDef feedbackResponseDef, int submit_status) {
        ContentValues values = new ContentValues();

        values.put(TABLES.FEEDBACK_RESPONSES.FEEDBACK_FORM_PK , feedbackResponseDef.FEEDBACK_FORM_PK);
        values.put(TABLES.FEEDBACK_RESPONSES.ANSWER_SET , feedbackResponseDef.ANSWER_SET);
        values.put(TABLES.FEEDBACK_RESPONSES.COMMENT , feedbackResponseDef.COMMENT);
        values.put(TABLES.FEEDBACK_RESPONSES.SUBMIT_STATUS , submit_status);

        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLES.FEEDBACK_RESPONSES.TABLE_NAME, null, values);
        db.close();
    }

    public Vector<FeedbackResponseDef> getAllFeedbackResponses() {
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLES.FEEDBACK_RESPONSES.TABLE_NAME + ";";

        Vector<FeedbackResponseDef> retVector = new Vector<>();

        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();

        while (!c.isAfterLast()) {
            FeedbackResponseDef row = new FeedbackResponseDef(c);
            retVector.add(row);
            c.moveToNext();
        }

        c.close();
        db.close();
        return retVector;
    }

    public JSONArray getFeedbackResponsesToBeSubmitted() {
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLES.FEEDBACK_RESPONSES.TABLE_NAME
                + " WHERE "
                + TABLES.FEEDBACK_RESPONSES.SUBMIT_STATUS + " = " + " 0 "
                + ";";

        JSONArray response_set_list = new JSONArray();

        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();

        while (!c.isAfterLast()) {
            FeedbackResponseDef row = new FeedbackResponseDef(c);
            JSONObject response = new JSONObject();

            try {
                response.put(FeedbackResponseDef.JSONResponseKeys.ANSWER_SET,row.ANSWER_SET);
                response.put(FeedbackResponseDef.JSONResponseKeys.COMMENT, row.COMMENT);
                response.put(FeedbackResponseDef.JSONResponseKeys.FEEDBACK_FORM_PK, row.FEEDBACK_FORM_PK);

                response_set_list.put(response);
            }
            catch (JSONException e) {
                e.printStackTrace();
            }

            c.moveToNext();
        }

        c.close();
        db.close();
        return response_set_list;
    }

    public void resetFeedbackResponsesTable(){
        SQLiteDatabase db = getWritableDatabase();
        TABLES.dropTable(db, TABLES.FEEDBACK_RESPONSES.TABLE_NAME);
        TABLES.createTable(db, TABLES.FEEDBACK_RESPONSES.CREATE_TABLE_QUERY);
        db.close();
    }

    public void markFeedbackResponseAsSubmitted(String fb_form_django_pk) {
        ContentValues values = new ContentValues();

        values.put(TABLES.FEEDBACK_RESPONSES.SUBMIT_STATUS, "1");

        SQLiteDatabase db = getWritableDatabase();
        String where = TABLES.FEEDBACK_RESPONSES.FEEDBACK_FORM_PK + " = " + fb_form_django_pk + " ";
        db.update(TABLES.FEEDBACK_RESPONSES.TABLE_NAME, values, where, null);

        db.close();
    }

    public FeedbackFormDef getFormOf(FeedbackResponseDef feedbackResponseDef){
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLES.FEEDBACK_FORMS.TABLE_NAME
                + " WHERE "
                + TABLES.FEEDBACK_FORMS.DJANGO_PK + " = " + feedbackResponseDef.FEEDBACK_FORM_PK + ";";

        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();

        if (!c.isAfterLast()) {
            return new FeedbackFormDef(c);
        }

        c.close();
        db.close();
        return null;
    }

    public HashMap<Integer, FeedbackResponseDef> prepareFeedbackResponsesHashMap() {
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLES.FEEDBACK_RESPONSES.TABLE_NAME + ";";

        HashMap<Integer, FeedbackResponseDef> local = new HashMap<>();

        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();

        while (!c.isAfterLast()) {
            FeedbackResponseDef row = new FeedbackResponseDef(c);
            local.put(row.FEEDBACK_FORM_PK, row);
            c.moveToNext();
        }

        c.close();
        db.close();
        return local;
    }
    /* Feedback Responses */

}