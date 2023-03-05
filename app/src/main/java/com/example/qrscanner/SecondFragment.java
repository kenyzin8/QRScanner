package com.example.qrscanner;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Calendar;

import es.dmoral.toasty.Toasty;

public class SecondFragment extends Fragment
{
    Button btnScan;
    Button btnInsertToAttendance;
    TextView txtTeamName;
    TextView txtStudentName;
    TextView txtCourseName;
    TextView txtSchoolID;

    TextView tvAttendanceMode;
    TextView tvAttendanceModeOther;

    String qrContents[];
    //Order - 3 > 0 > 1 > 2

    private DBHandler dbHandler;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_second, container, false);

        btnScan = view.findViewById(R.id.btn_scan_qr);
        btnInsertToAttendance = view.findViewById(R.id.btnAdd);

        txtTeamName = view.findViewById(R.id.teamName);
        txtTeamName.setPaintFlags(txtTeamName.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        txtStudentName = view.findViewById(R.id.studentName);
        txtStudentName.setPaintFlags(txtStudentName.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        txtCourseName = view.findViewById(R.id.courseName);
        txtCourseName.setPaintFlags(txtCourseName.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        txtSchoolID = view.findViewById(R.id.idNumber);
        txtSchoolID.setPaintFlags(txtSchoolID.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        tvAttendanceMode = view.findViewById(R.id.attendanceMode);
        tvAttendanceModeOther = view.findViewById(R.id.attendanceModeOther);

        dbHandler = new DBHandler(getActivity());

        enableFields(false);

        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanQRCode();
            }
        });

        btnInsertToAttendance.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                if(dbHandler.isTimeInNull(qrContents[0]) && MainActivity.home.rbArrival.isChecked())
                {
                    insertToDatabase(1, false);
                }
                else if(dbHandler.isTimeOutNull(qrContents[0]) && MainActivity.home.rbDeparture.isChecked())
                {
                    insertToDatabase(2, false);
                }
                else if(!dbHandler.isTimeInNull(qrContents[0]) && MainActivity.home.rbArrival.isChecked())
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle(Html.fromHtml("<font color='#000000'>Confirmation</font>"));
                    builder.setMessage(Html.fromHtml("<font color='#000000'><br> This student has already timed in. <br><br> Update it instead?</font>"));

                    builder.setPositiveButton(Html.fromHtml("<font color='#000000'>Yes</font>"), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            insertToDatabase(1, true);

                            Toasty.success(getContext(), "Time In Updated", Toast.LENGTH_SHORT, true).show();
                        }
                    });
                    builder.setNegativeButton(Html.fromHtml("<font color='#000000'>Cancel</font>"), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    AlertDialog alertDialog = builder.create();

                    alertDialog.show();
                }
                else if(!dbHandler.isTimeOutNull(qrContents[0]) && MainActivity.home.rbDeparture.isChecked())
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle(Html.fromHtml("<font color='#000000'>Confirmation</font>"));

                    builder.setMessage(Html.fromHtml("<font color='#000000'><br> This student has already timed out. <br><br> Update it instead?</font>"));

                    builder.setPositiveButton(Html.fromHtml("<font color='#000000'>Yes</font>"), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            insertToDatabase(2, true);

                            Toasty.success(getContext(), "Time Out Updated", Toast.LENGTH_SHORT, true).show();
                        }
                    });
                    builder.setNegativeButton(Html.fromHtml("<font color='#000000'>Cancel</font>"), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    AlertDialog alertDialog = builder.create();

                    alertDialog.show();
                }
            }
        });

        return view;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if(MainActivity.home.rbArrival.isChecked())
        {
            tvAttendanceMode.setText("Arrival Mode");
            tvAttendanceModeOther.setText("Time In");
        }

        if(MainActivity.home.rbDeparture.isChecked())
        {
            tvAttendanceMode.setText("Departure Mode");
            tvAttendanceModeOther.setText("Time Out");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void insertToDatabase(int mode, boolean isUpdate)
    {
        LocalDateTime timeNow = LocalDateTime.now();
        if(qrContents.length == 4)
            dbHandler.AddORUpdateAttendance(qrContents[0], qrContents[3], qrContents[2], qrContents[1], timeNow.toString());
        else if(qrContents.length == 3)
            dbHandler.AddORUpdateAttendance(qrContents[0], qrContents[2], qrContents[1], qrContents[1], timeNow.toString());

        String msg = " ";

        if(mode == 1 && !isUpdate)
            msg = "Time In Accepted";
        else if(mode == 2 && !isUpdate)
            msg = "Time Out Accepted";
        else if(mode == 1 && isUpdate)
            msg = "Time In Updated";
        else if(mode == 2 && isUpdate)
            msg = "Time Out Updated";

        Toasty.success(getContext(), msg, Toast.LENGTH_SHORT, true).show();

        if(ThirdFragment.searchAttendance.length() <= 0)
        {
            MainActivity.attendance.loadList(dbHandler.getAllAttendance());
        }

        enableFields(false);
    }

    private boolean isDay()
    {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        if(hour >= 6 && hour < 18)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    private void scanQRCode()
    {
        IntentIntegrator intentIntegrator = IntentIntegrator.forSupportFragment(SecondFragment.this);
        intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        intentIntegrator.setPrompt("Scan QR Code");
        intentIntegrator.setCameraId(0);
        intentIntegrator.setBeepEnabled(true);
        intentIntegrator.setBarcodeImageEnabled(false);
        intentIntegrator.setOrientationLocked(true);

        if(isDay())
            intentIntegrator.setTorchEnabled(false);
        else
            intentIntegrator.setTorchEnabled(true);

        intentIntegrator.initiateScan();
    }

    private void enableFields(boolean status)
    {
        if(!status)
        {
            txtTeamName.setVisibility(View.INVISIBLE);
            txtStudentName.setVisibility(View.INVISIBLE);
            txtCourseName.setVisibility(View.INVISIBLE);
            txtSchoolID.setVisibility(View.INVISIBLE);
            btnInsertToAttendance.setVisibility(View.INVISIBLE);
        }
        else
        {
            txtTeamName.setVisibility(View.VISIBLE);
            txtStudentName.setVisibility(View.VISIBLE);
            txtCourseName.setVisibility(View.VISIBLE);
            txtSchoolID.setVisibility(View.VISIBLE);
            btnInsertToAttendance.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        String scanContent = "";
        String scanFormat = "";

        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (scanningResult != null)
        {
            if (scanningResult.getContents() != null)
            {
                if(qrContents != null)
                    Arrays.fill(qrContents, null);

                scanContent = scanningResult.getContents().toString();
                scanFormat = scanningResult.getFormatName().toString();

                qrContents = scanContent.split("\\r?\\n");

                if(qrContents.length != 4 && qrContents.length != 3)
                {
                    Toasty.warning(getContext(), "Only STI QR Codes", Toast.LENGTH_SHORT, true).show();
                    return;
                }
                else if(qrContents.length == 3)
                {
                    if(qrContents[2].isEmpty())
                    {
                        Toast.makeText(getContext(), "Only STI Codes", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(qrContents[2].length() != 11 && qrContents[2].equals("SSG OFFICER"))
                    {
                        Toast.makeText(getContext(), "Only STI Codes", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                enableFields(true);

                if(qrContents.length == 4)
                {
                    txtTeamName.setText(qrContents[3]);
                    txtStudentName.setText(qrContents[0]);
                    txtCourseName.setText(qrContents[1]);
                    txtSchoolID.setText(qrContents[2]);
                }
                else if(qrContents.length == 3)
                {
                    txtTeamName.setText(qrContents[2]);
                    txtStudentName.setText(qrContents[0]);
                    txtCourseName.setText(qrContents[1]);
                    txtSchoolID.setText(qrContents[1]);
                }

            }
            else
            {
                Toasty.warning(getContext(), "Nothing Scanned", Toast.LENGTH_SHORT, true).show();
            }
        }
        else
        {
            Toasty.warning(getContext(), "Nothing Scanned", Toast.LENGTH_SHORT, true).show();
        }
    }
}