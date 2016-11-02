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
    public int SUBMIT_STATUS;

    public FeedbackResponseDef() {
    }

    public FeedbackResponseDef(Cursor cursor) {
        if (cursor != null) {
            this.PK = Integer.parseInt(cursor.getString(cursor.getColumnIndex(Helper.PRIMARY_KEY)));
            this.FEEDBACK_FORM_PK = Integer.parseInt(cursor.getString(cursor.getColumnIndex(db.TABLES.FEEDBACK_RESPONSES.FEEDBACK_FORM_PK)));
            this.SUBMIT_STATUS = Integer.parseInt(cursor.getString(cursor.getColumnIndex(db.TABLES.FEEDBACK_RESPONSES.SUBMIT_STATUS)));
            this.ANSWER_SET = cursor.getString(cursor.getColumnIndex(db.TABLES.FEEDBACK_RESPONSES.ANSWER_SET));
            this.COMMENT = cursor.getString(cursor.getColumnIndex(db.TABLES.FEEDBACK_RESPONSES.COMMENT));
        } else {
            Log.i(TLog.TAG, "FeedbackResponseDef: Tried to initialize instance with null cursor");
        }
    }

    public FeedbackResponseDef(JSONObject jsonObject) {
        if (jsonObject != null) {
            try {
                this.FEEDBACK_FORM_PK = (int) jsonObject.get(JSONResponseKeys.FEEDBACK_FORM_PK);
                this.ANSWER_SET = (String) jsonObject.get(JSONResponseKeys.ANSWER_SET);
                this.COMMENT = (String) jsonObject.get(JSONResponseKeys.COMMENT);
                this.SUBMIT_STATUS  = 1;
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
    public boolean identical(FeedbackResponseDef B) {
        if (B.FEEDBACK_FORM_PK != this.FEEDBACK_FORM_PK) {
            Log.i(TLog.TAG, "FB Responses not identical >> fb form pk's " + this.FEEDBACK_FORM_PK + " , " + B.FEEDBACK_FORM_PK);
            return false;
        }
        if (B.SUBMIT_STATUS != this.SUBMIT_STATUS) {
            Log.i(TLog.TAG, "FB Responses not identical >> submit status " + this.SUBMIT_STATUS + " , " + B.SUBMIT_STATUS );
            return false;
        }
        if (!B.COMMENT.equals(this.COMMENT)) {
            Log.i(TLog.TAG, "FB Responses not identical >> comment " + this.COMMENT + " , " + B.COMMENT );
            return false;
        }
        if (!B.ANSWER_SET.equals(this.ANSWER_SET)) {
            Log.i(TLog.TAG, "FB Responses not identical >> answer sets " + this.ANSWER_SET + " , " + B.ANSWER_SET);
            return false;
        }
        return true;
    }

    public String toString() {
        return this.PK + " "
                + this.COMMENT + " "
                + this.ANSWER_SET + " "
                + this.FEEDBACK_FORM_PK + " ";
    }
}
