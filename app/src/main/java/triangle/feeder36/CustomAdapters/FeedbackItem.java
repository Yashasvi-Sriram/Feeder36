package triangle.feeder36.CustomAdapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Vector;

import triangle.feeder36.Activities.FillFeedback;
import triangle.feeder36.Activities.Home;
import triangle.feeder36.Calender.Date;
import triangle.feeder36.Calender.DateTime;
import triangle.feeder36.DB.Def.CourseDef;
import triangle.feeder36.DB.Def.FeedbackFormDef;
import triangle.feeder36.DB.Helpers.db;
import triangle.feeder36.R;

public class FeedbackItem extends BaseAdapter {
    Context context;

    Vector<FeedbackFormDef> feedbackFormDefs;
    Vector<CourseDef> coursesOfFbForms;
    DateTime currentDateTime;

    private static LayoutInflater inflater = null;

    public FeedbackItem(Context mainActivity, Vector<FeedbackFormDef> feedbackFormDefs, Vector<CourseDef> courseDefs,DateTime currentDateTime) {
        this.context = mainActivity;
        this.feedbackFormDefs = feedbackFormDefs;
        this.coursesOfFbForms = courseDefs;
        this.currentDateTime = currentDateTime;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return feedbackFormDefs.size();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View rowView = inflater.inflate(R.layout.feedback_item, null);

        Holder holder = new FeedbackItem.Holder(rowView);
        holder.setText(position);

        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* Takes to feedback form if not filled
                * else just shows a toast which says form has been filled
                * */

                db dbManager;
                dbManager = new db(context,db.DB_NAME,null,db.DB_VERSION);

                DateTime feedbackDeadline = new DateTime(feedbackFormDefs.get(position).DEADLINE,"/",":"," ");

                if(dbManager.getResponseOf(feedbackFormDefs.get(position)) != null) {
                    Toast.makeText(context,"You have already filled feedback",Toast.LENGTH_SHORT).show();
                }
                else {
                    if(DateTime.isPast(currentDateTime,feedbackDeadline)) {
                        Intent fillFeedback = new Intent(context, FillFeedback.class);
                        fillFeedback.putExtra("courseCode", coursesOfFbForms.get(position).CODE);
                        fillFeedback.putExtra("courseName", coursesOfFbForms.get(position).NAME);
                        fillFeedback.putExtra("feedbackName", feedbackFormDefs.get(position).NAME);
                        fillFeedback.putExtra("feedbackQuestionSet", feedbackFormDefs.get(position).QUESTION_SET);
                        fillFeedback.putExtra("feedbackDeadline", feedbackFormDefs.get(position).DEADLINE);
                        fillFeedback.putExtra("feedbackDjangoPk", feedbackFormDefs.get(position).DJANGO_PK);
                        context.startActivity(fillFeedback);
                    }
                    else {
                        Toast.makeText(context,"Oops !! The deadline is over",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        return rowView;
    }

    public class Holder {
        RelativeLayout feedback_item_layout;
        TextView feedback_course_code;
        TextView feedback_name;

        public Holder(View rowView) {
            this.feedback_item_layout = (RelativeLayout) rowView.findViewById(R.id.feedback_item_layout);
            this.feedback_course_code = (TextView) this.feedback_item_layout.findViewById(R.id.feedback_course_code);
            this.feedback_name = (TextView) this.feedback_item_layout.findViewById(R.id.feedback_name);
        }

        public void setText(final int position) {
            this.feedback_course_code.setText(coursesOfFbForms.get(position).CODE);
            this.feedback_name.setText(feedbackFormDefs.get(position).NAME);
        }
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}