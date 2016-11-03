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

import triangle.feeder36.Activities.ReadTask;
import triangle.feeder36.DB.Def.CourseDef;
import triangle.feeder36.DB.Def.TaskDef;
import triangle.feeder36.R;

public class TaskItem extends BaseAdapter{

    Context context;

    Vector<TaskDef> taskDefs;
    Vector<CourseDef> coursesOfTasks;

    private static LayoutInflater inflater = null;

    public TaskItem(Context mainActivity, Vector<TaskDef> taskDefs, Vector<CourseDef> coursesOfTasks) {
        this.context = mainActivity;
        this.taskDefs = taskDefs;
        this.coursesOfTasks = coursesOfTasks;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return taskDefs.size();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View rowView = inflater.inflate(R.layout.task_item,null);

        Holder holder=new Holder(rowView);
        holder.setText(position);

        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /* takes to read task activity */

                Intent readTask = new Intent(context,ReadTask.class);
                readTask.putExtra("courseCode",coursesOfTasks.get(position).CODE);
                readTask.putExtra("courseName",coursesOfTasks.get(position).NAME);
                readTask.putExtra("taskTag",taskDefs.get(position).TAG);
                readTask.putExtra("taskDetail",taskDefs.get(position).DETAIL);
                readTask.putExtra("taskDeadline",taskDefs.get(position).DEADLINE);
                context.startActivity(readTask);
            }
        });
        return rowView;
    }

    public class Holder {
        RelativeLayout task_item_layout;
        TextView task_course_code;
        TextView task_tag;

        public Holder(View rowView){
            this.task_item_layout= (RelativeLayout) rowView.findViewById(R.id.task_item_layout);
            this.task_course_code = (TextView) this.task_item_layout.findViewById(R.id.task_course_code);
            this.task_tag = (TextView) this.task_item_layout.findViewById(R.id.task_tag);
        }

        public void setText(final int position){
            this.task_course_code.setText(coursesOfTasks.get(position).CODE);
            this.task_tag.setText(taskDefs.get(position).TAG);
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