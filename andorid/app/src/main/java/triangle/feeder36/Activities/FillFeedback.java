package triangle.feeder36.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import triangle.feeder36.Calender.DateTime;
import triangle.feeder36.DB.Def.FeedbackFormDef;
import triangle.feeder36.DB.Def.FeedbackResponseDef;
import triangle.feeder36.DB.Helpers.db;
import triangle.feeder36.Log.TLog;
import triangle.feeder36.R;

public class FillFeedback extends AppCompatActivity {

    public final static int numOfStars = 5;
    public final static float stepSizeOfRating = (float) 1;

    RelativeLayout fill_feedback_layout;

    TextView feedback_course_code, feedback_course_name, feedback_name, feedback_deadline;
    EditText feedback_comment;
    LinearLayout feedback_questions_layout;
    Button submit_feedback;
    String courseCode, courseName, feedbackName, feedbackQuestionSet, feedbackDeadlineFormatted;
    String[] feedbackQns;
    String feedbackResponse = "";
    int feedback_form_django_pk;

    db dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fill_feedback);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        dbManager = new db(this, db.DB_NAME, null, db.DB_VERSION);

        gettingReferences();
        gettingDataFromHomeActivity();
        setTextViews();
        addQnsAndRatingBars();
        setAttributesRatingBars();

        submit_feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                feedbackResponse = "";
                for (int i = 0; i < feedbackQns.length; i++) {
                    int ratingBarId = 2 * i + 2;
                    RatingBar rating_i = (RatingBar) feedback_questions_layout.findViewById(ratingBarId);
                    feedbackResponse += String.valueOf(rating_i.getRating());
                    if (i != feedbackQns.length - 1)
                        feedbackResponse += FeedbackFormDef.JSONResponseKeys.SEP_ANSWER_SET;
                }

                FeedbackResponseDef feedbackResponseDef = new FeedbackResponseDef();
                feedbackResponseDef.FEEDBACK_FORM_PK = feedback_form_django_pk;
                feedbackResponseDef.ANSWER_SET = feedbackResponse;
                feedbackResponseDef.COMMENT = feedback_comment.getText().toString();

                dbManager.insert(feedbackResponseDef, 0);
                Log.i(TLog.TAG, "response to feedback form with pk " + feedback_form_django_pk + " is stored locally ");
                Toast.makeText(getApplicationContext(), "Response recorded", Toast.LENGTH_SHORT).show();

                Intent home = new Intent(FillFeedback.this, Home.class);
                startActivity(home);
            }
        });
    }

    public void gettingReferences() {
        fill_feedback_layout = (RelativeLayout) findViewById(R.id.fill_feedback_layout);
        feedback_course_code = (TextView) fill_feedback_layout.findViewById(R.id.feedback_course_code);
        feedback_course_name = (TextView) fill_feedback_layout.findViewById(R.id.feedback_course_name);
        feedback_name = (TextView) fill_feedback_layout.findViewById(R.id.feedback_name);
        feedback_deadline = (TextView) fill_feedback_layout.findViewById(R.id.feedback_deadline);
        feedback_questions_layout = (LinearLayout) fill_feedback_layout.findViewById(R.id.feedback_questions_layout);
        feedback_comment = (EditText) fill_feedback_layout.findViewById(R.id.feedback_comment);
        submit_feedback = (Button) fill_feedback_layout.findViewById(R.id.submit_feedback);
    }

    public void setTextViews() {
        feedback_course_code.setText(courseCode);
        feedback_course_name.setText(courseName);
        feedback_name.setText(feedbackName);
        feedback_deadline.setText(feedbackDeadlineFormatted);
    }

    public void gettingDataFromHomeActivity() {
        Bundle dataFromFeedbackItem = getIntent().getExtras();
        courseCode = dataFromFeedbackItem.getString("courseCode");
        courseName = dataFromFeedbackItem.getString("courseName");
        feedbackName = dataFromFeedbackItem.getString("feedbackName");
        feedbackQuestionSet = dataFromFeedbackItem.getString("feedbackQuestionSet");
        feedback_form_django_pk = dataFromFeedbackItem.getInt("feedbackDjangoPk");

        String feedbackDeadlineStored = dataFromFeedbackItem.getString("feedbackDeadline");

        /* Feedback Deadline converted to better forms of representation */
        DateTime feedbackDeadlineDateTime = new DateTime(feedbackDeadlineStored, "/", ":", " ");
        feedbackDeadlineFormatted = feedbackDeadlineDateTime.formal12Representation();
    }

    public void addQnsAndRatingBars() {
        /* Add questions as text views and corresponding rating bars to the feedback_questions_layout */
        feedbackQns = feedbackQuestionSet.split(FeedbackFormDef.JSONResponseKeys.SEP_QUESTION_SET);
        int noOfQns = feedbackQns.length;

        LinearLayout.LayoutParams questionParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        questionParams.setMargins(10, 10, 10, 10);

        LinearLayout.LayoutParams answerParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        answerParams.setMargins(10, 10, 10, 10);

        for (int i = 0; i < noOfQns; i++) {
            TextView qn_i = new TextView(this);
            qn_i.setText(feedbackQns[i]);
            qn_i.setId(2 * i + 1);
            qn_i.setLayoutParams(questionParams);
            qn_i.setTextSize(20);
            qn_i.setTextColor(Color.RED);

            feedback_questions_layout.addView(qn_i);

            RatingBar rating_i = new RatingBar(this);
            rating_i.setId(2 * i + 2);
            rating_i.setLayoutParams(answerParams);
            feedback_questions_layout.addView(rating_i);
        }

    }

    public void setAttributesRatingBars() {
        for (int i = 0; i < feedbackQns.length; i++) {
            int ratingBarId = 2 * i + 2;
            RatingBar rating_i = (RatingBar) feedback_questions_layout.findViewById(ratingBarId);
            rating_i.setNumStars(numOfStars);
            rating_i.setStepSize(stepSizeOfRating);
        }
    }
}