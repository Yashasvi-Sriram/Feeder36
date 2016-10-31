package triangle.feeder36.CustomAdapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import triangle.feeder36.Activities.Home;
import triangle.feeder36.Activities.FillFeedback;
import triangle.feeder36.R;

public class FeedbackItem extends BaseAdapter {
    Context context;

    String[] courseCode;
    String[] courseName;
    String[] feedbackName;
    String[] feedbackQuestionSet;
    String[] feedbackDeadline;

    private static LayoutInflater inflater = null;
    public FeedbackItem(Home mainActivity,String[] courseCodeDB,String [] courseNameDB,String [] feedbackNameDB,String [] feedbackQuestionSetDB,String [] feedbackDeadlineDB) {
        context = mainActivity;
        courseCode = courseCodeDB;
        courseName = courseNameDB;
        feedbackName = feedbackNameDB;
        feedbackQuestionSet = feedbackQuestionSetDB;
        feedbackDeadline = feedbackDeadlineDB;

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return courseCode.length;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class Holder {
        RelativeLayout feedback_item_layout;
        TextView feedback_course_code;
        TextView feedback_name;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        FeedbackItem.Holder holder=new FeedbackItem.Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.feedback_item,null);
        holder.feedback_item_layout = (RelativeLayout) rowView.findViewById(R.id.feedback_item_layout);
        holder.feedback_course_code = (TextView) holder.feedback_item_layout.findViewById(R.id.feedback_course_code);
        holder.feedback_name = (TextView) holder.feedback_item_layout.findViewById(R.id.feedback_name);
        holder.feedback_course_code.setText(courseCode[position]);
        holder.feedback_name.setText(feedbackName[position]);

        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /* takes to feedback form if not filled
                * else just show a toast which saya form has been filled
                * */

                /* Need to implement it */

                Intent goToFillFeedback = new Intent(context,FillFeedback.class);
                goToFillFeedback.putExtra("courseCode",courseCode[position]);
                goToFillFeedback.putExtra("courseName",courseName[position]);
                goToFillFeedback.putExtra("feedbackName",feedbackName[position]);
                goToFillFeedback.putExtra("feedbackQuestionSet",feedbackQuestionSet[position]);
                goToFillFeedback.putExtra("feedbackDeadline",feedbackDeadline[position]);
                context.startActivity(goToFillFeedback);
            }
        });
        return rowView;
    }
}