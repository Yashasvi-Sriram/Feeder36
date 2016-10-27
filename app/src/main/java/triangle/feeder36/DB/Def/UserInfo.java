package triangle.feeder36.DB.Def;

import android.database.Cursor;
import android.util.Log;

import triangle.feeder36.DB.Helpers.Helper;
import triangle.feeder36.DB.Helpers.db;
import triangle.feeder36.Log.TLog;

public class UserInfo {
    public String PK;
    public String USER_NAME;
    public String PASSWORD;

    public UserInfo(String user_name,String pwd) {
        USER_NAME = user_name;
        PASSWORD = pwd;
    }

    public UserInfo(Cursor cursor){
        if(cursor != null){
            this.PK = cursor.getString(cursor.getColumnIndex(Helper.PRIMARY_KEY));
            this.USER_NAME = cursor.getString(cursor.getColumnIndex(db.TABLES.USER_INFO.USER_NAME));
            this.PASSWORD = cursor.getString(cursor.getColumnIndex(db.TABLES.USER_INFO.PASSWORD));
        }
        else {
            Log.i(TLog.TAG, "UserInfo: Tried to initialize user Info instance with null cursor");
        }
    }

    public String getUserName() {
        return USER_NAME;
    }

    public void setUserName(String userName) {
        this.USER_NAME = userName;
    }

    public String getPASSWORD() {
        return PASSWORD;
    }

    public void setPASSWORD(String PASSWORD) {
        this.PASSWORD = PASSWORD;
    }
}