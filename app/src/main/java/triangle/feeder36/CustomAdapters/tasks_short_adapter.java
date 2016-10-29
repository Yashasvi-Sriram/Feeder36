package triangle.feeder36.CustomAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import triangle.feeder36.Activities.Home;
import triangle.feeder36.R;

public class tasks_short_adapter extends BaseAdapter{

    Context context;
    String [] field_1;
    String [] field_2;

    private static LayoutInflater inflater = null;
    public tasks_short_adapter(Home mainActivity,String[] arr1,String[] arr2) {
        context = mainActivity;
        field_1 = arr1;
        field_2 = arr2;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return field_1.length;
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
        TextView courseCode;
        TextView taskTag;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Holder holder=new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.short_view_tasks,null);
        holder.courseCode = (TextView) rowView.findViewById(R.id.courseCode);
        holder.taskTag = (TextView) rowView.findViewById(R.id.taskTag);
        holder.courseCode.setText(field_1[position]);
        holder.taskTag.setText(field_2[position]);

        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "You Clicked " + field_1[position],Toast.LENGTH_LONG).show();
            }
        });
        return rowView;
    }
}