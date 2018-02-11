package com.mstoyanov.musiclessons;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.MergeCursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.mstoyanov.musiclessons.data.SchoolContract;

import java.util.ArrayList;
import java.util.Arrays;

public class AddLessonActivity extends Activity implements OnItemSelectedListener, LoaderManager.LoaderCallbacks<Cursor> {
    private String weekday;
    private String timeFrom;
    private String timeTo;
    private long studentId;
    private String[] timesFromArray;
    private String[] timesToArray;
    private ArrayList<String> toList;
    private Spinner spinnerTimeFrom;
    private Spinner spinnerTimeTo;
    private Spinner spinnerStudents;
    private ArrayAdapter<String> timesFromAdapter;
    private ArrayAdapter<String> timesToAdapter;
    private SimpleCursorAdapter studentsAdapter;
    private static final int STUDENTS_LOADER = 0;
    private String[] projection = {
            SchoolContract.Students.COLUMN_NAME_STUDENT_ID + " as _id",
            SchoolContract.Students.COLUMN_NAME_FIRST_NAME,
            SchoolContract.Students.COLUMN_NAME_LAST_NAME};
    private static final String SORT_ORDER = "firstName, lastName";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_lesson);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setTitle("Add Lesson");

        weekday = getIntent().getStringExtra("WEEKDAY");
        ((TextView) findViewById(R.id.weekday)).setText(weekday);

        if (weekday.equals("Saturday")) {
            timesFromArray = getResources().getStringArray(R.array.saturday_times_from_array);
            timesToArray = getResources().getStringArray(R.array.saturday_times_to_array);
        } else {
            timesFromArray = getResources().getStringArray(R.array.weekdays_times_from_array);
            timesToArray = getResources().getStringArray(R.array.weekdays_times_to_array);
        }

        timesFromAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, timesFromArray);
        spinnerTimeFrom = findViewById(R.id.time_from_spinner);
        spinnerTimeFrom.setAdapter(timesFromAdapter);
        spinnerTimeFrom.setOnItemSelectedListener(this);
        spinnerTimeFrom.setSelection(0);

        toList = new ArrayList<>();
        toList.addAll(Arrays.asList(timesToArray));
        timesToAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, toList);
        spinnerTimeTo = findViewById(R.id.time_to_spinner);
        spinnerTimeTo.setAdapter(timesToAdapter);
        spinnerTimeTo.setOnItemSelectedListener(this);
        spinnerTimeTo.setSelection(0);

        String[] fromColumns = new String[]{"firstName", "lastName"};
        int[] toViews = new int[]{R.id.firstName, R.id.lastName};
        studentsAdapter = new SimpleCursorAdapter(this, R.layout.student_list_item, null, fromColumns, toViews, 0);
        spinnerStudents = findViewById(R.id.student_spinner);
        spinnerStudents.setAdapter(studentsAdapter);
        spinnerStudents.setOnItemSelectedListener(this);

        getLoaderManager().initLoader(STUDENTS_LOADER, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_lesson_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("WEEKDAY", weekday);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            case R.id.menu_save_lesson:
                ContentValues values = new ContentValues();
                values.put("weekday", weekday);
                values.put("timeFrom", timeFrom);
                values.put("timeTo", timeTo);
                values.put("studentId", studentId);
                getContentResolver().insert(SchoolContract.SCHEDULE_TABLE_CONTENTURI, values);

                intent = new Intent(this, MainActivity.class);
                intent.putExtra("WEEKDAY", weekday);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            default:
                throw new IllegalArgumentException("Invalid ItemId: " + item.getItemId());
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        switch (parent.getId()) {
            case R.id.time_from_spinner:
                timeFrom = parent.getItemAtPosition(pos).toString();
                if (weekday.equals("Saturday")) {
                    timesToArray = getResources().getStringArray(R.array.saturday_times_to_array);
                } else {
                    timesToArray = getResources().getStringArray(R.array.weekdays_times_to_array);
                }
                timesToArray = java.util.Arrays.copyOfRange(timesToArray, pos, timesToArray.length);
                toList.clear();
                toList.addAll(Arrays.asList(timesToArray));
                timesToAdapter.notifyDataSetChanged();
                spinnerTimeTo.setSelection(0);
                timeTo = toList.get(0);
                break;
            case R.id.time_to_spinner:
                timeTo = parent.getItemAtPosition(pos).toString();
                break;
            case R.id.student_spinner:
                Cursor cursor = (Cursor) parent.getItemAtPosition(pos);
                if (pos == cursor.getCount() - 1) {
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.putExtra("SELECTED_TAB", 2);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
                studentId = id;
                break;
            default:
                throw new IllegalArgumentException("Invalid parentId: "
                        + parent.getId());
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // do nothing
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderID, Bundle bundle) {
        switch (loaderID) {
            case STUDENTS_LOADER:
                return new CursorLoader(this, SchoolContract.STUDENTS_TABLE_CONTENTURI, projection, null, null, SORT_ORDER);
            default:
                throw new IllegalArgumentException("Invalid loaderID: " + loaderID);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        switch (loader.getId()) {
            case STUDENTS_LOADER:
                // add "Add student" item at the end of the cursor:
                MatrixCursor extras = new MatrixCursor(new String[]{"_id", "firstName", "lastName"});
                extras.addRow(new String[]{String.valueOf(cursor.getCount() + 1), "Add", "student"});
                Cursor[] cursors = {cursor, extras};
                Cursor extendedCursor = new MergeCursor(cursors);
                studentsAdapter.swapCursor(extendedCursor);
                break;
            default:
                throw new IllegalArgumentException("Invalid loaderID: " + loader.getId());
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId()) {
            case STUDENTS_LOADER:
                studentsAdapter.swapCursor(null);
                break;
            default:
                throw new IllegalArgumentException("Invalid loaderID: " + loader.getId());
        }
    }
}