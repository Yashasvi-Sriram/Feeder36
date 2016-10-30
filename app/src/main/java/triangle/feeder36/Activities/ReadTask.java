package triangle.feeder36.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.TextView;

import triangle.feeder36.R;

public class ReadTask extends AppCompatActivity {

    RelativeLayout long_view_tasks;
    TextView course_code,course_name,task_tag,task_detail,task_deadline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.read_task);

        Bundle dataFromHome = getIntent().getExtras();
        String courseCode = dataFromHome.getString("courseCode");
        String courseName = dataFromHome.getString("courseName");
        String taskTag = dataFromHome.getString("taskTag");
        String taskDetail = dataFromHome.getString("taskDetail");
        String taskDeadline = dataFromHome.getString("taskDeadline");

        long_view_tasks = (RelativeLayout) findViewById(R.id.long_view_tasks);
        course_code = (TextView) long_view_tasks.findViewById(R.id.course_code);
        course_name = (TextView) long_view_tasks.findViewById(R.id.course_name);
        task_tag = (TextView) long_view_tasks.findViewById(R.id.task_tag);
        task_detail = (TextView) long_view_tasks.findViewById(R.id.task_detail);
        task_deadline = (TextView) long_view_tasks.findViewById(R.id.task_deadline);

        course_code.setText(courseCode);
        course_name.setText(courseName);
        task_tag.setText(taskTag);
        task_detail.setText(taskDetail);
        task_deadline.setText(taskDeadline);
    }
}