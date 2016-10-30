package triangle.feeder36.DB.Def;

import android.database.Cursor;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import triangle.feeder36.DB.Helpers.Helper;
import triangle.feeder36.DB.Helpers.db;
import triangle.feeder36.Log.TLog;

public class FeedbackFormDef {

    public class JSONResponseKeys {
        public static final String FEEDBACK_FORM_DICT = "feedback_forms";

        public static final String DJANGO_PK = "django_pk";
        public static final String NAME = "name";
        public static final String DEADLINE = "deadline";
        public static final String QUESTION_SET = "question_set";
        public static final String COURSE_PK = "course_pk";
    }

    public int PK;
    public int COURSE_PK;
    public int DJANGO_PK;
    public String NAME;
    public String DEADLINE;
    public String QUESTION_SET;

    public FeedbackFormDef() {
    }

    public FeedbackFormDef(Cursor cursor) {
        if (cursor != null) {
            this.PK = Integer.parseInt(cursor.getString(cursor.getColumnIndex(Helper.PRIMARY_KEY)));
            this.COURSE_PK = Integer.parseInt(cursor.getString(cursor.getColumnIndex(db.TABLES.FEEDBACK_FORMS.COURSE_PK)));
            this.DJANGO_PK = Integer.parseInt(cursor.getString(cursor.getColumnIndex(db.TABLES.FEEDBACK_FORMS.DJANGO_PK)));
            this.NAME = cursor.getString(cursor.getColumnIndex(db.TABLES.FEEDBACK_FORMS.NAME));
            this.DEADLINE = cursor.getString(cursor.getColumnIndex(db.TABLES.FEEDBACK_FORMS.DEADLINE));
            this.QUESTION_SET = cursor.getString(cursor.getColumnIndex(db.TABLES.FEEDBACK_FORMS.QUESTION_SET));
        } else {
            Log.i(TLog.TAG, "FeedbackFormDef: Tried to initialize instance with null cursor");
        }
    }

    public FeedbackFormDef(JSONObject jsonObject) {
        if (jsonObject != null) {
            try {
                this.DJANGO_PK = (int) jsonObject.get(FeedbackFormDef.JSONResponseKeys.DJANGO_PK);
                this.COURSE_PK = (int) jsonObject.get(FeedbackFormDef.JSONResponseKeys.COURSE_PK);
                this.NAME = (String) jsonObject.get(FeedbackFormDef.JSONResponseKeys.NAME);
                this.DEADLINE = (String) jsonObject.get(FeedbackFormDef.JSONResponseKeys.DEADLINE);
                this.QUESTION_SET = (String) jsonObject.get(FeedbackFormDef.JSONResponseKeys.QUESTION_SET);
            }
            catch (JSONException e) {
                e.printStackTrace();
                Log.i(TLog.TAG, "FeedbackFormDef: Tried to initialize instance with IMPROPER jsonObject : " + jsonObject.toString());
            }
        }
        else {
            Log.i(TLog.TAG, "FeedbackFormDef: Tried to initialize instance with null jsonObject");
        }
    }

    /**
     * Does not consider the equality of PK (of local table)
     */
    public boolean identical(FeedbackFormDef B) {
        if (!B.NAME.equals(this.NAME)) return false;
        if (!B.QUESTION_SET.equals(this.QUESTION_SET)) return false;
        if (!B.DEADLINE.equals(this.DEADLINE)) return false;
        if (B.DJANGO_PK != this.DJANGO_PK) return false;
        if (B.COURSE_PK != this.COURSE_PK) return false;

        return true;
    }

    public String toString() {
        return this.PK + " "
                + this.DJANGO_PK + " "
                + this.NAME + " "
                + this.DEADLINE + " "
                + this.QUESTION_SET + " "
                + this.COURSE_PK + " ";
    }

}
