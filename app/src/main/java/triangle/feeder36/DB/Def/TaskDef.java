package triangle.feeder36.DB.Def;

import android.database.Cursor;
import android.util.Log;

import triangle.feeder36.DB.Helpers.Helper;
import triangle.feeder36.DB.Helpers.db;
import triangle.feeder36.Log.TLog;

public class TaskDef {
    public String PK;
    public String COURSE_PK;
    public String TAG;
    public String DEADLINE;
    public String DETAIL;

    public TaskDef() {}

    public TaskDef(Cursor cursor){
        if(cursor != null){
            this.PK = cursor.getString(cursor.getColumnIndex(Helper.PRIMARY_KEY));
            this.TAG = cursor.getString(cursor.getColumnIndex(db.TABLES.TASKS.TAG));
            this.DEADLINE = cursor.getString(cursor.getColumnIndex(db.TABLES.TASKS.DEADLINE));
            this.DETAIL = cursor.getString(cursor.getColumnIndex(db.TABLES.TASKS.DETAIL));
            this.COURSE_PK = cursor.getString(cursor.getColumnIndex(db.TABLES.TASKS.COURSE_PK));
        }
        else {
            Log.i(TLog.TAG, "TaskDef: Tried to initialize instance with null cursor");
        }
    }
}
