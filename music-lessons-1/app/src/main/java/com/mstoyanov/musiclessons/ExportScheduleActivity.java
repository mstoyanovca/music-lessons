package com.mstoyanov.musiclessons;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;

import com.mstoyanov.musiclessons.data.SchoolContract;
import com.mstoyanov.musiclessons.data.SchoolContract.Schedule;
import com.mstoyanov.musiclessons.data.SchoolContract.Students;
import com.mstoyanov.musiclessons.model.Cell;
import com.mstoyanov.musiclessons.pdf.CreatePDF;

import java.util.ArrayList;
import java.util.List;

public class ExportScheduleActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor> {
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private List<Cell> cells;

    public static final String SETTINGS = "Settings";
    private static final int CELLS_LOADER = 0;
    private static final String sortOrder = Schedule.COLUMN_NAME_TIME_FROM + ", " + Schedule.COLUMN_NAME_TIME_TO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.export_schedule);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setTitle("Export Schedule");

        SharedPreferences settings = getSharedPreferences(SETTINGS, 0);
        firstName = settings.getString("FIRST_NAME", firstName);
        lastName = settings.getString("LAST_NAME", lastName);

        getLoaderManager().initLoader(CELLS_LOADER, null, this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            default:
                throw new IllegalArgumentException("Invalid itemId: " + item.getItemId());
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderID, Bundle bundle) {
        switch (loaderID) {
            case CELLS_LOADER:
                String innerJoin = " INNER JOIN " + Students.TABLE_NAME + " ON "
                        + "schedule." + Schedule.COLUMN_NAME_STUDENT_ID + " = "
                        + "students." + Students.COLUMN_NAME_STUDENT_ID;
                Uri uri = Uri.withAppendedPath(SchoolContract.SCHEDULE_TABLE_CONTENTURI, innerJoin);
                return new CursorLoader(this, uri, null, null, null, sortOrder);
            default:
                throw new IllegalArgumentException("Invalid loaderID: " + loaderID);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        switch (loader.getId()) {
            case CELLS_LOADER:
                cells = new ArrayList<>();
                cursor.moveToFirst();
                for (int i = 0; i < cursor.getCount(); i++) {
                    String weekday = cursor.getString(cursor.getColumnIndex("weekday"));
                    String timeFrom = cursor.getString(cursor.getColumnIndex("timeFrom"));
                    String timeTo = cursor.getString(cursor.getColumnIndex("timeTo"));
                    String studentName = cursor.getString(
                            cursor.getColumnIndex("firstName"))
                            + " "
                            + cursor.getString(cursor.getColumnIndex("lastName"));
                    String homePhone = cursor.getString(cursor.getColumnIndex("homePhone"));
                    String cellPhone = cursor.getString(cursor.getColumnIndex("cellPhone"));
                    String workPhone = cursor.getString(cursor.getColumnIndex("workPhone"));
                    // only one number is exported on paper:
                    if (homePhone.length() != 0) {
                        phoneNumber = homePhone + " /home/";
                    } else if (cellPhone.length() != 0) {
                        phoneNumber = cellPhone + " /cell/";
                    } else if (workPhone.length() != 0) {
                        phoneNumber = workPhone + " /work/";
                    }
                    Cell cell = new Cell(weekday, timeFrom, timeTo, studentName, phoneNumber);
                    cells.add(cell);
                    cursor.moveToNext();
                }
                if (new CreatePDF(cells, firstName + " " + lastName, this).exportPDF()) {
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
                break;
            default:
                throw new IllegalArgumentException("Invalid loaderID: " + loader.getId());
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId()) {
            case CELLS_LOADER:
                break;
            default:
                throw new IllegalArgumentException("Invalid loaderID: " + loader.getId());
        }
    }
}