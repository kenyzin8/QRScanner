package com.example.qrscanner;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.os.Environment;
import androidx.annotation.RequiresApi;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DBHandler extends SQLiteOpenHelper
{
    private static final String DATABASE_NAME = "Attendance";
    private static final int DATABASE_VERSION = 1;

    private static final String ID_COL = "id";

    private static final String TABLE_ATTENDANCE = "Attendance";
    private static final String COLUMN_STUDENT_NAME = "student_name";
    private static final String COLUMN_TEAM_NAME = "team_name";
    private static final String COLUMN_SCHOOL_ID = "school_id";
    private static final String COLUMN_COURSE = "program";
    private static final String ARRIVAL_COLUMN_DATE = "arrival";
    private static final String DEPARTURE_COLUMN_DATE = "departure";

    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String Query =
                "CREATE TABLE " + TABLE_ATTENDANCE + " ("
                        + ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + COLUMN_STUDENT_NAME + " TEXT NOT NULL UNIQUE,"
                        + COLUMN_TEAM_NAME + " TEXT NOT NULL,"
                        + COLUMN_SCHOOL_ID + " TEXT NOT NULL,"
                        + COLUMN_COURSE + " TEXT NOT NULL,"
                        + ARRIVAL_COLUMN_DATE + " TEXT,"
                        + DEPARTURE_COLUMN_DATE + " TEXT)";

        sqLiteDatabase.execSQL(Query);
    }

    @SuppressLint("Range")
    public boolean isTimeInNull(String studentName)
    {
        studentName = studentName.trim();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT " + ARRIVAL_COLUMN_DATE + " FROM " + TABLE_ATTENDANCE + " WHERE " + COLUMN_STUDENT_NAME + " = ?";

        Cursor cursor = db.rawQuery(query, new String[] {studentName});

        if (cursor.moveToFirst())
        {
            return cursor.getString(cursor.getColumnIndex(ARRIVAL_COLUMN_DATE)) == null;
        }
        else
        {
            return true;
        }
    }

    @SuppressLint("Range")
    public boolean isTimeOutNull(String studentName)
    {
        studentName = studentName.trim();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT " + DEPARTURE_COLUMN_DATE + " FROM " + TABLE_ATTENDANCE + " WHERE " + COLUMN_STUDENT_NAME + " = ?";

        Cursor cursor = db.rawQuery(query, new String[] {studentName});

        if (cursor.moveToFirst())
        {
            return cursor.getString(cursor.getColumnIndex(DEPARTURE_COLUMN_DATE)) == null;
        }
        else
        {
            return true;
        }
    }

    public void AddORUpdateAttendance(String studentName, String teamName, String schoolID, String course, String date)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(COLUMN_STUDENT_NAME, studentName);
        values.put(COLUMN_TEAM_NAME, teamName);
        values.put(COLUMN_SCHOOL_ID, schoolID);
        values.put(COLUMN_COURSE, course);

        if(MainActivity.home.rbArrival.isChecked())
        {
            values.put(ARRIVAL_COLUMN_DATE, date);
        }

        if(MainActivity.home.rbDeparture.isChecked())
        {
            values.put(DEPARTURE_COLUMN_DATE, date);
        }

        Cursor cursor = db.query(TABLE_ATTENDANCE,
                new String[]{ID_COL},
                COLUMN_STUDENT_NAME + "=?",
                new String[]{studentName},
                null, null, null);

        if (cursor.moveToFirst()) {
            // If a student with the given name already exists, update the existing record
            int id = cursor.getInt(0);
            db.update(TABLE_ATTENDANCE, values, ID_COL + "=" + id, null);
        } else {
            // Otherwise, insert a new record into the table
            db.insert(TABLE_ATTENDANCE, null, values);
        }

        cursor.close();
        db.close();
    }

    public Cursor getAllAttendance()
    {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM Attendance ORDER BY id DESC "; //DESC

        Cursor cursor = db.rawQuery(selectQuery, null);

        return cursor;
    }

    public void resetList()
    {
        SQLiteDatabase db = getWritableDatabase();

        db.delete("Attendance", null, null);
    }

    public boolean isEmpty()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_ATTENDANCE, null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        if (count == 0) {
            return true;
        }
        else
        {
            return false;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void exportToFile() throws IOException
    {
        SQLiteDatabase db = this.getReadableDatabase();

        // Create a cursor to iterate through the data in the database
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_ATTENDANCE, null);

        // Create a file object to represent the download folder on the device
        File downloadFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

        LocalDateTime dateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd  - hh mm a");;

        // Create a file object to represent the CSV file

        String eventName = MainActivity.home.eventName;

        File csvFile = new File(downloadFolder, eventName + " - " + dateTime.format(formatter) + ".csv");

        // Create a writer to write the data from the database to the CSV file
        BufferedWriter writer = new BufferedWriter(new FileWriter(csvFile));
        // Write the column names from the database as the header row in the CSV file
        for (int i = 0; i < cursor.getColumnCount(); i++) {
            writer.write(cursor.getColumnName(i));
            writer.write(",");
        }
        writer.newLine();

        // Write the data from the database to the CSV file
        while (cursor.moveToNext()) {
            for (int i = 0; i < cursor.getColumnCount(); i++) {
                if(cursor.isNull(i))
                {
                    writer.write("null");
                }
                else
                {
                    String holder = cursor.getString(i);
                    if(holder.contains(","))
                    {
                        holder = holder.replace(',', ' ');
                    }
                    writer.write(holder);
                }

                writer.write(",");
            }
            writer.newLine();
        }

        writer.close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_ATTENDANCE);
        onCreate(sqLiteDatabase);
    }
}
