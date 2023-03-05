package com.example.qrscanner;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import android.os.Environment;
import android.text.Html;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import es.dmoral.toasty.Toasty;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class FirstFragment extends Fragment {

    public RadioButton rbArrival,
            rbDeparture;

    public RadioGroup rbAttendanceModeGroup;

    private Button btnReset, btnExport;

    private DBHandler dbHandler;

    public EditText editEventName;

    public String eventName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_first, container, false);

        rbArrival = view.findViewById(R.id.arrivalRB);
        rbDeparture = view.findViewById(R.id.departureRB);

        rbAttendanceModeGroup = view.findViewById(R.id.attendanceModeRBGroup);

        dbHandler = new DBHandler(getActivity());

        btnReset = view.findViewById(R.id.btnDeleteList);

        btnExport = view.findViewById(R.id.btnExport);

        editEventName = view.findViewById(R.id.editEventName);

        editEventName.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN)
                {
                    loseFocus();
                    return true;
                }
                return false;
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                if(dbHandler.isEmpty())
                {
                    Toasty.error(getContext(), "List Empty", Toast.LENGTH_SHORT, true).show();
                    return;
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(Html.fromHtml("<font color='#000000'>Confirmation</font>"));
                builder.setMessage(Html.fromHtml("<font color='#000000'><br> Are you sure you want to reset the list?</font>"));

                builder.setPositiveButton(Html.fromHtml("<font color='#000000'>Yes</font>"), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // If the user clicks "Yes", call the resetList() method
                        resetList();

                        Toasty.success(getContext(), "List Deleted", Toast.LENGTH_SHORT, true).show();
                    }
                });
                builder.setNegativeButton(Html.fromHtml("<font color='#000000'>No</font>"), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog alertDialog = builder.create();

                alertDialog.show();
            }
        });

        btnExport.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                if(editEventName.getText().toString().isEmpty())
                {
                    Toasty.error(getContext(), "Missing Event Name", Toast.LENGTH_SHORT, true).show();
                    return;
                }
                if(dbHandler.isEmpty())
                {
                    Toasty.error(getContext(), "List Empty", Toast.LENGTH_SHORT, true).show();
                    return;
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(Html.fromHtml("<font color='#000000'>Confirmation</font>"));

                File downloadFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                LocalDateTime dateTime = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd  - hh mm a");;

                eventName = editEventName.getText().toString();

                builder.setMessage(Html.fromHtml("<font color='#000000'><br> Are you sure you want to export the list? <br><br> Your file will be saved on your download folder. <br><br> " + String.valueOf(downloadFolder + "/"+ eventName + " - " + dateTime.format(formatter) + ".csv") + "</font>"));

                builder.setPositiveButton(Html.fromHtml("<font color='#000000'>Yes</font>"), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            dbHandler.exportToFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Toasty.success(getContext(), "List Exported", Toast.LENGTH_SHORT, true).show();
                    }
                });
                builder.setNegativeButton(Html.fromHtml("<font color='#000000'>No</font>"), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog alertDialog = builder.create();

                alertDialog.show();
            }
        });

        rbArrival.setChecked(true);

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
        editEventName.clearFocus();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void resetList()
    {
        dbHandler.resetList();
        Cursor cursor = dbHandler.getAllAttendance();
        MainActivity.attendance.loadList(cursor);
    }
}