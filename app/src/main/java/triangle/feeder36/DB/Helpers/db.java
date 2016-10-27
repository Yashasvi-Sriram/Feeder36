package triangle.feeder36.DB.Helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.Vector;

import triangle.feeder36.DB.Def.UserInfo;
import triangle.feeder36.Log.TLog;

import static triangle.feeder36.DB.Helpers.db.TABLES.USER_INFO.USER_NAME;

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
            /* user info table - name*/
            public static final String TABLE_NAME = "user_info";

            /* user info table - columns*/
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
            /* courses table - name*/
            public static final String TABLE_NAME = "courses";

            /* courses table - columns*/
            // PRIMARY KEY
            public static final String CODE = "code";
            public static final String NAME = "name";

            /* create table query */
            private static final String CREATE_TABLE_QUERY =
                    "CREATE TABLE "
                            + TABLE_NAME
                            + "("
                            + PRIMARY_KEY + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                            + CODE + " VARCHAR(255) NOT NULL,"
                            + NAME + " VARCHAR(255) NOT NULL"
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
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onUpgrade(db, oldVersion, newVersion);
        // drop tables
        TABLES.dropTable(db, TABLES.USER_INFO.TABLE_NAME);
        TABLES.dropTable(db, TABLES.COURSES.TABLE_NAME);
        // create new ones
        onCreate(db);
    }

    public void insert(UserInfo userInfo) {
        /* user info table */
        ContentValues values = new ContentValues();

        values.put(USER_NAME, userInfo.getUserName());
        values.put(TABLES.USER_INFO.PASSWORD, userInfo.getPASSWORD());

        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLES.USER_INFO.TABLE_NAME, null, values);
        db.close();
    }

    public void updateEntryWithKeyValue(UserInfo userInfo, String key, String value) {
        /* user info table */
        ContentValues values = new ContentValues();

        values.put(USER_NAME, userInfo.getUserName());
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


}