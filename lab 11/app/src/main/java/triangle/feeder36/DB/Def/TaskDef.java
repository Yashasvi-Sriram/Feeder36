package triangle.feeder36.DB.Def;

import android.database.Cursor;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import triangle.feeder36.DB.Helpers.Helper;
import triangle.feeder36.DB.Helpers.db;
import triangle.feeder36.Log.TLog;

public class TaskDef {

    public class JSONResponseKeys {
        public static final String TASKS_DICT = "tasks";

        public static final String DJANGO_PK = "django_pk";
        public static final String TAG = "tag";
        public static final String DEADLINE = "deadline";
        public static final String DETAIL = "detail";
        public static final String COURSE_PK = "course_pk";
    }

    public int PK;
    public int COURSE_PK;
    public int DJANGO_PK;
    public String TAG;
    public String DEADLINE;
    public String DETAIL;

    public TaskDef() {
    }

    public TaskDef(Cursor cursor) {
        if (cursor != null) {
            this.PK = Integer.parseInt(cursor.getString(cursor.getColumnIndex(Helper.PRIMARY_KEY)));
            this.COURSE_PK = Integer.parseInt(cursor.getString(cursor.getColumnIndex(db.TABLES.TASKS.COURSE_PK)));
            this.DJANGO_PK = Integer.parseInt(cursor.getString(cursor.getColumnIndex(db.TABLES.TASKS.DJANGO_PK)));
            this.TAG = cursor.getString(cursor.getColumnIndex(db.TABLES.TASKS.TAG));
            this.DEADLINE = cursor.getString(cursor.getColumnIndex(db.TABLES.TASKS.DEADLINE));
            this.DETAIL = cursor.getString(cursor.getColumnIndex(db.TABLES.TASKS.DETAIL));
        } else {
            Log.i(TLog.TAG, "TaskDef: Tried to initialize instance with null cursor");
        }
    }

    public TaskDef(JSONObject jsonObject) {
        if (jsonObject != null) {
            try {
                this.DJANGO_PK = (int) jsonObject.get(JSONResponseKeys.DJANGO_PK);
                this.COURSE_PK = (int) jsonObject.get(JSONResponseKeys.COURSE_PK);
                this.TAG = (String) jsonObject.get(JSONResponseKeys.TAG);
                this.DEADLINE = (String) jsonObject.get(JSONResponseKeys.DEADLINE);
                this.DETAIL = (String) jsonObject.get(JSONResponseKeys.DETAIL);
            }
            catch (JSONException e) {
                e.printStackTrace();
                Log.i(TLog.TAG, "TaskDef: Tried to initialize instance with IMPROPER jsonObject : " + jsonObject.toString());
            }
        }
        else {
            Log.i(TLog.TAG, "TaskDef: Tried to initialize instance with null jsonObject");
        }
    }

    /**
     * Does not consider the equality of PK (of local table)
     */
    public boolean identical(TaskDef B) {
        if (!B.TAG.equals(this.TAG)) return false;
        if (!B.DETAIL.equals(this.DETAIL)) return false;
        if (!B.DEADLINE.equals(this.DEADLINE)) return false;
        if (B.DJANGO_PK != this.DJANGO_PK) return false;
        if (B.COURSE_PK != this.COURSE_PK) return false;

        return true;
    }

    public String toString() {
        return this.PK + " "
                + this.DJANGO_PK + " "
                + this.TAG + " "
                + this.DEADLINE + " "
                + this.DETAIL + " "
                + this.COURSE_PK + " ";
    }

}
