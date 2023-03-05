package com.example.qrscanner;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import es.dmoral.toasty.Toasty;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class ThirdFragment extends Fragment
{
    private ListView listView;
    private DBHandler dbHandler;
    private AttendanceListAdapter attendanceListAdapter;
    private ArrayList<Attendance> arrayList;
    private Cursor cursor;
    private String qrContents[];

    private TextView tvNotFound;

    public static EditText searchAttendance;

    ConstraintLayout layoutParent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onHiddenChanged(boolean hidden) {
        if(arrayList.size() == 0)
        {
            if(searchAttendance.length() == 0)
            {
                tvNotFound.setText("Empty");
                searchAttendance.setEnabled(false);
            }
            tvNotFound.setVisibility(View.VISIBLE);
            listView.setVisibility(View.INVISIBLE);

        }
        else
        {
            tvNotFound.setVisibility(View.INVISIBLE);
            listView.setVisibility(View.VISIBLE);

            if(searchAttendance.length() == 0) {
                searchAttendance.setEnabled(true);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_third, container, false);

        listView = view.findViewById(R.id.attendanceList);

        tvNotFound = view.findViewById(R.id.not_found_text_view);
        tvNotFound.setVisibility(View.INVISIBLE);

        dbHandler = new DBHandler(getActivity());

        arrayList = new ArrayList<Attendance>();

        //layoutParent = view.findViewById(R.id.focusableLayout);

        attendanceListAdapter = new AttendanceListAdapter(getContext(), R.layout.attendance_list_layout, arrayList);

        listView.setAdapter(attendanceListAdapter);

        searchAttendance = view.findViewById(R.id.searchListView);

        searchAttendance.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b)
                {
                    MainActivity.bottomNavigationView.setVisibility(View.INVISIBLE);
                }
                else
                {
                    MainActivity.bottomNavigationView.setVisibility(View.VISIBLE);
                }
            }
        });

        searchAttendance.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_DOWN)
                {
                    loseFocus();
                    return true;
                }

                return false;
            }
        });

        searchAttendance.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                String query = charSequence.toString();
                List<Attendance> matchingStudents = null;
                if(query.isEmpty())
                {
                    cursor = dbHandler.getAllAttendance();
                    loadList(cursor);
                    //loseFocus();
                }
                else
                {
                    matchingStudents = searchStudents(query);

                    if(matchingStudents.size() <= 0)
                    {
                        tvNotFound.setText("Not Found");
                        tvNotFound.setVisibility(View.VISIBLE);
                        listView.setVisibility(View.INVISIBLE);
                    }
                    else
                    {
                        tvNotFound.setVisibility(View.INVISIBLE);
                        listView.setVisibility(View.VISIBLE);
                    }
                    attendanceListAdapter.clear();
                    attendanceListAdapter.addAll(matchingStudents);
                }
                attendanceListAdapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable editable)
            {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                qrContents = listView.getItemAtPosition(i).toString().split("\\r?\\n");
                Attendance attendance = arrayList.get(i);

                Toasty.info(getContext(), attendance.getStudentName(), Toast.LENGTH_SHORT, true).show();
            }
        });

        cursor = dbHandler.getAllAttendance();

        loadList(cursor);

        return view;
    }

    private void loseFocus()
    {
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
        if (inputMethodManager.isAcceptingText()) {
            View views = getActivity().getCurrentFocus();
            if(views != null)
            {
                inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
            }
        }
        searchAttendance.clearFocus();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public List<Attendance> searchStudents(String query) {

        List<Attendance> matchingStudents = new ArrayList<>();

        if(arrayList.size() == 0)
        {
            cursor = dbHandler.getAllAttendance();
            loadList(cursor);
        }

        for (Attendance student : arrayList) {

            if (student.getStudentName().toLowerCase().contains(query.toLowerCase())
                    || student.getIdNumber().toLowerCase().contains(query.toLowerCase())
                    || student.getCourse().toLowerCase().contains(query.toLowerCase())
                    || student.getTeamName().toLowerCase().contains(query.toLowerCase())) {
                matchingStudents.add(student);

            }
        }

        return matchingStudents;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void loadList(Cursor cursor)
    {
        arrayList.clear();

        int id = 0;

        String studentName= "", teamName = "", schoolID = "", course = "", arrival = "", departure = "";

        LocalDateTime dateTime = null;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd  - hh:mm a");;

        Attendance attendance = null;

        while (cursor.moveToNext())
        {
            id = cursor.getInt(0);
            studentName = cursor.getString(1);
            teamName = cursor.getString(2);
            schoolID = cursor.getString(3);
            course = cursor.getString(4);

            arrival = cursor.getString(5);
            departure = cursor.getString(6);


            if(arrival == null)
            {
                arrival = "...";
            }
            else
            {
                dateTime = LocalDateTime.parse(arrival);
                arrival = dateTime.format(formatter);
            }

            if(departure == null)
            {
                departure = "...";
            }
            else
            {
                dateTime = LocalDateTime.parse(departure);
                departure = dateTime.format(formatter);
            }

            attendance = new Attendance(studentName, teamName, schoolID, course, arrival, departure);

            arrayList.add(attendance);
        }

        attendanceListAdapter.notifyDataSetChanged();
    }

}