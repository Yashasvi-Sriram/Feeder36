package triangle.feeder36.DB.Def;

import android.database.Cursor;
import android.util.Log;

import triangle.feeder36.DB.Helpers.Helper;
import triangle.feeder36.DB.Helpers.db;
import triangle.feeder36.Log.TLog;

public class CourseDef {
    public String PK;
    public String DJANGO_PK;
    public String CODE;
    public String NAME;

    public CourseDef() {}

    public CourseDef(Cursor cursor){
        if(cursor != null){
            this.PK = cursor.getString(cursor.getColumnIndex(Helper.PRIMARY_KEY));
            this.DJANGO_PK = cursor.getString(cursor.getColumnIndex(db.TABLES.COURSES.DJANGO_PK));
            this.CODE = cursor.getString(cursor.getColumnIndex(db.TABLES.COURSES.CODE));
            this.NAME = cursor.getString(cursor.getColumnIndex(db.TABLES.COURSES.NAME));
        }
        else {
            Log.i(TLog.TAG, "CourseDef: Tried to initialize instance with null cursor");
        }
    }
}
