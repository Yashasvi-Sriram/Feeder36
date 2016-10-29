package triangle.feeder36.DB.Helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.HashMap;
import java.util.Vector;

import triangle.feeder36.Calender.Date;
import triangle.feeder36.Calender.DateTime;
import triangle.feeder36.Calender.Time;
import triangle.feeder36.DB.Def.CourseDef;
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
                            + COURSE_PK + " INTEGER "
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
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onUpgrade(db, oldVersion, newVersion);
        // drop tables
        TABLES.dropTable(db, TABLES.USER_INFO.TABLE_NAME);
        TABLES.dropTable(db, TABLES.COURSES.TABLE_NAME);
        TABLES.dropTable(db, TABLES.TASKS.TABLE_NAME);
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
}