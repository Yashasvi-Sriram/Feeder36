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
import triangle.feeder36.Activities.ReadTask;
import triangle.feeder36.R;

public class TaskItem extends BaseAdapter{

    Context context;

    String[] courseCodeCop;
    String[] courseNameCop;
    String[] taskTagCop;
    String[] taskDetailCop;
    String[] taskDeadlineCop;

    private static LayoutInflater inflater = null;
    public TaskItem(Home mainActivity, String[] courseCode, String[] courseName, String[] taskTag, String[] taskDetail, String[] taskDeadline) {
        context = mainActivity;
        courseCodeCop = courseCode;
        courseNameCop = courseName;
        taskTagCop = taskTag;
        taskDetailCop = taskDetail;
        taskDeadlineCop = taskDeadline;

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return courseCodeCop.length;
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
        RelativeLayout task_item_layout;
        TextView task_course_code;
        TextView task_tag;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Holder holder=new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.task_item,null);
        holder.task_item_layout= (RelativeLayout) rowView.findViewById(R.id.task_item_layout);
        holder.task_course_code = (TextView) holder.task_item_layout.findViewById(R.id.task_course_code);
        holder.task_tag = (TextView) holder.task_item_layout.findViewById(R.id.task_tag);
        holder.task_course_code.setText(courseCodeCop[position]);
        holder.task_tag.setText(taskTagCop[position]);

        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /* takes to long view of the task */

                Intent goToLongTaskView = new Intent(context,ReadTask.class);
                goToLongTaskView.putExtra("courseCode",courseCodeCop[position]);
                goToLongTaskView.putExtra("courseName",courseNameCop[position]);
                goToLongTaskView.putExtra("taskTag",taskTagCop[position]);
                goToLongTaskView.putExtra("taskDetail",taskDetailCop[position]);
                goToLongTaskView.putExtra("taskDeadline",taskDeadlineCop[position]);
                context.startActivity(goToLongTaskView);
            }
        });
        return rowView;
    }
}