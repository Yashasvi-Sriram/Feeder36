package triangle.feeder36.DB.Def;

import android.database.Cursor;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import triangle.feeder36.DB.Helpers.Helper;
import triangle.feeder36.DB.Helpers.db;
import triangle.feeder36.Log.TLog;

public class FeedbackResponseDef {

    public class JSONResponseKeys {
        public static final String FEEDBACK_RESPONSE_DICT = "feedback_responses";

        public static final String ANSWER_SET = "answer_set";
        public static final String COMMENT = "comment";
        public static final String FEEDBACK_FORM_PK = "feedback_form_pk";
    }

    public int PK;
    public int FEEDBACK_FORM_PK;
    public String COMMENT;
    public String ANSWER_SET;

    public FeedbackResponseDef() {
    }

    public FeedbackResponseDef(Cursor cursor) {
        if (cursor != null) {
            this.PK = Integer.parseInt(cursor.getString(cursor.getColumnIndex(Helper.PRIMARY_KEY)));
            this.ANSWER_SET = cursor.getString(cursor.getColumnIndex(db.TABLES.FEEDBACK_FORMS.QUESTION_SET));
            this.COMMENT = cursor.getString(cursor.getColumnIndex(db.TABLES.FEEDBACK_FORMS.NAME));
        } else {
            Log.i(TLog.TAG, "FeedbackResponseDef: Tried to initialize instance with null cursor");
        }
    }

    public FeedbackResponseDef(JSONObject jsonObject) {
        if (jsonObject != null) {
            try {
                this.ANSWER_SET = (String) jsonObject.get(JSONResponseKeys.ANSWER_SET);
                this.COMMENT = (String) jsonObject.get(JSONResponseKeys.COMMENT);
            }
            catch (JSONException e) {
                e.printStackTrace();
                Log.i(TLog.TAG, "FeedbackResponseDef: Tried to initialize instance with IMPROPER jsonObject : " + jsonObject.toString());
            }
        }
        else {
            Log.i(TLog.TAG, "FeedbackResponseDef: Tried to initialize instance with null jsonObject");
        }
    }

    /**
     * Does not consider the equality of PK (of local table)
     */
    public boolean identical(FeedbackResponseDef B) {
        if (!B.COMMENT.equals(this.COMMENT)) return false;
        if (!B.ANSWER_SET.equals(this.ANSWER_SET)) return false;

        return true;
    }

    public String toString() {
        return this.PK + " "
                + this.COMMENT + " "
                + this.ANSWER_SET + " ";
    }
}
