package triangle.feeder36.CustomAdapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Vector;

import triangle.feeder36.Activities.FillFeedback;
import triangle.feeder36.DB.Def.CourseDef;
import triangle.feeder36.DB.Def.FeedbackFormDef;
import triangle.feeder36.R;

public class FeedbackItem extends BaseAdapter {
    Context context;


    Vector<FeedbackFormDef> feedbackFormDefs;
    Vector<CourseDef> coursesOfFbForms;

    private static LayoutInflater inflater = null;

    public FeedbackItem(Context mainActivity, Vector<FeedbackFormDef> feedbackFormDefs, Vector<CourseDef> courseDefs) {
        this.context = mainActivity;
        this.feedbackFormDefs = feedbackFormDefs;
        this.coursesOfFbForms = courseDefs;
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

                /* TODO : takes to feedback form if not filled
                * else just show a toast which saya form has been filled
                * */

                /* Need to implement it */

                Intent fillFeedback = new Intent(context, FillFeedback.class);
                fillFeedback.putExtra("courseCode", coursesOfFbForms.get(position).CODE);
                fillFeedback.putExtra("courseName", coursesOfFbForms.get(position).NAME);
                fillFeedback.putExtra("feedbackName", feedbackFormDefs.get(position).NAME);
                fillFeedback.putExtra("feedbackQuestionSet", feedbackFormDefs.get(position).QUESTION_SET);
                fillFeedback.putExtra("feedbackDeadline", feedbackFormDefs.get(position).DEADLINE);
                context.startActivity(fillFeedback);
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