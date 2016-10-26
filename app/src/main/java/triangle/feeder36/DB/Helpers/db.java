package triangle.feeder36.DB.Helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

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
        public static final class USER_INFO_TABLE {
            /* user info table - name*/
            public static final String USER_INFO_TABLE_NAME = "user_info";

            /* user info table - columns*/
            // PRIMARY KEY
            public static final String USER_NAME = "user_name";
            public static final String PASSWORD = "password";

            /* create table query */
            private static final String CREATE_TABLE_QUERY =
                    "CREATE TABLE "
                            + USER_INFO_TABLE_NAME
                            + "("
                            + PRIMARY_KEY + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                            + USER_NAME + " VARCHAR(255) NOT NULL,"
                            + PASSWORD + " VARCHAR(255) NOT NULL"
                            + ")";
        }
    }

    public db(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        super.onCreate(db);
        // create tables
        TABLES.createTable(db, TABLES.USER_INFO_TABLE.CREATE_TABLE_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onUpgrade(db, oldVersion, newVersion);
        // drop tables
        TABLES.dropTable(db, TABLES.USER_INFO_TABLE.USER_INFO_TABLE_NAME);
        // create new ones
        onCreate(db);
    }

    public void insert(UserInfo userInfo,String table) {
        switch (table) {
            case TABLES.USER_INFO_TABLE.USER_INFO_TABLE_NAME :
                /* user info table */
                ContentValues values = new ContentValues();

                values.put(TABLES.USER_INFO_TABLE.USER_NAME, userInfo.getUserName());
                values.put(TABLES.USER_INFO_TABLE.PASSWORD, userInfo.getPASSWORD());

                SQLiteDatabase db = getWritableDatabase();
                db.insert(table, null, values);
                db.close();

                break;
            default:
                break;
        }
    }

    public void updateEntryWithKeyValue(UserInfo userInfo,String table,String key,String value) {
        switch (table) {
            case TABLES.USER_INFO_TABLE.USER_INFO_TABLE_NAME:
                /* user info table */
                ContentValues values = new ContentValues();

                values.put(TABLES.USER_INFO_TABLE.USER_NAME, userInfo.getUserName());
                values.put(TABLES.USER_INFO_TABLE.PASSWORD, userInfo.getPASSWORD());

                SQLiteDatabase db = getWritableDatabase();
                String where = key + " = '" + value + "' ";
                db.update(table, values, where, null);
                db.close();

                break;
            default:
                break;
        }
    }
}