package triangle.feeder36.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import triangle.feeder36.Calender.DateTime;
import triangle.feeder36.DB.Def.FeedbackFormDef;
import triangle.feeder36.R;

public class FillFeedback extends AppCompatActivity {
    RelativeLayout fill_feedback_layout;
    TextView feedback_course_code,feedback_course_name,feedback_name,feedback_deadline;
    LinearLayout feedback_questions_layout;
    Button submit_feedback;
    String courseCode,courseName,feedbackName,feedbackQuestionSet,feedbackDeadlineFormatted;
    String [] feedbackQns;
    String feedbackResponse = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fill_feedback);

        gettingReferences();
        gettingDataFromHomeActivity();
        setTextViews();
        addQnsAndRatingBars();
        setAttributesRatingBars();

        submit_feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i=0;i<feedbackQns.length;i++) {
                    int ratingBarId = 2*i + 2;
                    RatingBar rating_i = (RatingBar) feedback_questions_layout.findViewById(ratingBarId);
                    feedbackResponse += String.valueOf(rating_i.getRating());
                    if(i!= feedbackQns.length-1) feedbackResponse += FeedbackFormDef.JSONResponseKeys.SEP_ANSWER_SET;
                }
                Toast.makeText(getApplicationContext(),feedbackResponse,Toast.LENGTH_SHORT).show();
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

        String feedbackDeadlineStored = dataFromFeedbackItem.getString("feedbackDeadline");

        /* Feedback Deadline converted to better forms of representation */
        DateTime feedbackDeadlineDateTime = new DateTime(feedbackDeadlineStored,"/",":"," ");
        feedbackDeadlineFormatted = feedbackDeadlineDateTime.formal12Representation();
    }

    public void addQnsAndRatingBars() {
        /* Add questions as text views and corresponding rating bars to the feedback_questions_layout */
        feedbackQns = feedbackQuestionSet.split(FeedbackFormDef.JSONResponseKeys.SEP_QUESTION_SET);
        int noOfQns = feedbackQns.length;

        for(int i=0;i<noOfQns;i++) {
            TextView qn_i = new TextView(this);
            qn_i.setText(feedbackQns[i]);
            qn_i.setId(2*i+1);
            qn_i.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

            feedback_questions_layout.addView(qn_i);

            RatingBar rating_i = new RatingBar(this);
            rating_i.setId(2*i+2);
            rating_i.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            feedback_questions_layout.addView(rating_i);
        }
    }

    public void setAttributesRatingBars() {
        for(int i=0;i<feedbackQns.length;i++) {
            int ratingBarId = 2*i + 2;
            RatingBar rating_i = (RatingBar) feedback_questions_layout.findViewById(ratingBarId);
            rating_i.setNumStars(5);
            rating_i.setStepSize((float) 0.5);
        }
    }
}