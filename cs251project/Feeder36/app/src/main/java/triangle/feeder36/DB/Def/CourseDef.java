package triangle.feeder36.DB.Def;

import android.database.Cursor;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import triangle.feeder36.DB.Helpers.Helper;
import triangle.feeder36.DB.Helpers.db;
import triangle.feeder36.Log.TLog;

public class CourseDef {

    public class JSONResponseKeys{
        public static final String COURSES_DICT = "courses";

        public static final String CODE = "code";
        public static final String NAME = "name";
        public static final String DJANGO_PK = "django_pk";
    }

    public int PK;
    public int DJANGO_PK;
    public String CODE;
    public String NAME;

    public CourseDef() {}

    public CourseDef(CourseDef def){
        this.PK = def.PK;
        this.DJANGO_PK = def.DJANGO_PK;
        this.CODE = def.CODE;
        this.NAME = def.NAME;
    }

    public CourseDef(Cursor cursor){
        if(cursor != null){
            this.PK = Integer.parseInt(cursor.getString(cursor.getColumnIndex(Helper.PRIMARY_KEY)));
            this.DJANGO_PK = Integer.parseInt(cursor.getString(cursor.getColumnIndex(db.TABLES.COURSES.DJANGO_PK)));
            this.CODE = cursor.getString(cursor.getColumnIndex(db.TABLES.COURSES.CODE));
            this.NAME = cursor.getString(cursor.getColumnIndex(db.TABLES.COURSES.NAME));
        }
        else {
            Log.i(TLog.TAG, "CourseDef: Tried to initialize instance with null cursor");
        }
    }

    public CourseDef(JSONObject jsonObject){
        if(jsonObject != null){
            try {
                this.DJANGO_PK = (int) jsonObject.get(JSONResponseKeys.DJANGO_PK);
                this.CODE = (String) jsonObject.get(JSONResponseKeys.CODE);
                this.NAME = (String) jsonObject.get(JSONResponseKeys.NAME);
            } catch (JSONException e) {
                e.printStackTrace();
                Log.i(TLog.TAG, "CourseDef: Tried to initialize instance with IMPROPER jsonObject : " + jsonObject.toString());
            }
        }
        else {
            Log.i(TLog.TAG, "CourseDef: Tried to initialize instance with null jsonObject");
        }
    }

    /** Does not consider the equality of PK (of local table) */
    public boolean identical(CourseDef B){
        if(!B.CODE.equals(this.CODE))return false;
        if(!B.NAME.equals(this.NAME))return false;
        if(B.DJANGO_PK != this.DJANGO_PK)return false;

        return true;
    }

    public String toString(){
        return this.PK + " "
                + this.CODE + " "
                + this.NAME + " "
                + this.DJANGO_PK + " ";
    }


}
