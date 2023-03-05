package com.example.qrscanner;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Filterable;
import android.widget.TextView;
import androidx.annotation.NonNull;
import java.util.ArrayList;

public class AttendanceListAdapter extends ArrayAdapter<Attendance> implements Filterable{

    private Context mContext;
    private int mResource;

    private int lastPosition = -1;

    private ArrayList<Attendance> attendanceList;

    static class ViewHolder
    {
        TextView tvStudentName;
        TextView tvTeamName;
        TextView tvIDNumber;
        TextView tvCourse;
        TextView tvTimeIn;
        TextView tvTimeOut;
    }

    public AttendanceListAdapter(Context context, int resource, ArrayList<Attendance> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
        attendanceList = objects;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String studentName = getItem(position).getStudentName();
        String teamName = getItem(position).getTeamName();
        String idNumber = getItem(position).getIdNumber();
        String course = getItem(position).getCourse();
        String timeIn = getItem(position).getTimeIn();
        String timeOut = getItem(position).getTimeOut();

        Attendance attendance = new Attendance(studentName, teamName, idNumber, course, timeIn, timeOut);

        final View result;
        ViewHolder holder;

        if(convertView == null)
        {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);
            holder = new ViewHolder();
            holder.tvStudentName = convertView.findViewById(R.id.studentNameListView);
            holder.tvTeamName = convertView.findViewById(R.id.teamNameListView);
            holder.tvIDNumber = convertView.findViewById(R.id.idNumberListView);
            holder.tvCourse = convertView.findViewById(R.id.courseListView);
            holder.tvTimeIn = convertView.findViewById(R.id.arrivalListView);
            holder.tvTimeOut = convertView.findViewById(R.id.departureListView);
            result = convertView;
            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        if(position % 2 == 0)
            convertView.setBackgroundColor(Color.parseColor("#FFFFFF"));
        else
            convertView.setBackgroundColor(Color.parseColor("#DDDDDD"));

        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.loading_down : R.anim.loading_up);

        result.startAnimation(animation);
        lastPosition = position;

        holder.tvStudentName.setText(studentName);
        holder.tvTeamName.setText(teamName);
        holder.tvIDNumber.setText(idNumber);
        holder.tvCourse.setText(course);
        holder.tvTimeIn.setText("Time In\n" + timeIn);
        holder.tvTimeOut.setText("Time Out\n" + timeOut);

        return convertView;
    }
}
