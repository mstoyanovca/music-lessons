package com.mstoyanov.musiclessons;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

import com.mstoyanov.musiclessons.data.SchoolContract;
import com.mstoyanov.musiclessons.data.SchoolContract.Schedule;
import com.mstoyanov.musiclessons.data.SchoolContract.Students;

import java.util.ArrayList;
import java.util.Arrays;

/*EditLessonActivity is being called by the LessonDetailsActivity in
 * single pane mode, or the ScheduleFragment in dual pane mode, with
 * lessonId passed in.
 * It returns to the MainActivity on "save", to the day the lesson 
 * was on, before editing.*/

public class EditLessonActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor>, OnItemSelectedListener {
    // the day this lesson was on, and where this activity has to return to in the MainActivity:
    private String weekdayTo;
    private boolean dualPane;

    private int lessonId;
    private String weekday;
    private String timeFrom;
    private String timeTo;
    private int studentId;
    private String firstName;
    private String lastName;

    private Spinner spinnerWeekdays;
    private Spinner spinnerTimeFrom;
    private Spinner spinnerTimeTo;
    private Spinner spinnerStudents;

    private ArrayAdapter<String> weekdaysAdapter;
    private ArrayAdapter<String> timesFromAdapter;
    private ArrayAdapter<String> timesToAdapter;
    private SimpleCursorAdapter studentsAdapter;

    private String[] timesFromArray;
    private String[] timesToArray;

    ArrayList<String> fromList;
    ArrayList<String> toList;

    private static final int LESSONS_LOADER = 0;
    private static final int STUDENTS_LOADER = 1;

    private static final String innerJoin = " INNER JOIN "
            + Students.TABLE_NAME + " ON " + "schedule."
            + Schedule.COLUMN_NAME_STUDENT_ID + " = " + "students."
            + Students.COLUMN_NAME_STUDENT_ID;
    private static final Uri uri = Uri.withAppendedPath(SchoolContract.SCHEDULE_TABLE_CONTENTURI, innerJoin);
    private static final String selection = "lessonID = ?";
    private String[] selectionArgs = new String[1];

    private static final String[] projection = {
            SchoolContract.Students.COLUMN_NAME_STUDENT_ID + " as _id",
            SchoolContract.Students.COLUMN_NAME_FIRST_NAME,
            SchoolContract.Students.COLUMN_NAME_LAST_NAME};
    private String sortOrder = "firstName, lastName";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_lesson);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setTitle("Edit Lesson");

        dualPane = getIntent().getBooleanExtra("DUAL_PANE", false);
        lessonId = getIntent().getIntExtra("LESSON_ID", 0);
        selectionArgs[0] = String.valueOf(lessonId);

        spinnerWeekdays = findViewById(R.id.weekdays_spinner);
        spinnerWeekdays.setOnItemSelectedListener(this);
        weekdaysAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, getResources()
                .getStringArray(R.array.weekdays_array));

        fromList = new ArrayList<>();
        toList = new ArrayList<>();

        spinnerTimeFrom = findViewById(R.id.time_from_spinner);
        spinnerTimeFrom.setOnItemSelectedListener(this);

        spinnerTimeTo = findViewById(R.id.time_to_spinner);
        spinnerTimeTo.setOnItemSelectedListener(this);

        spinnerStudents = findViewById(R.id.student_spinner);
        spinnerStudents.setOnItemSelectedListener(this);
        String[] fromColumns = new String[]{"firstName", "lastName"};
        int[] toViews = new int[]{R.id.firstName, R.id.lastName};
        studentsAdapter = new SimpleCursorAdapter(this, R.layout.edit_lesson_item, null, fromColumns, toViews, 0);
        spinnerStudents.setAdapter(studentsAdapter);

        getLoaderManager().initLoader(LESSONS_LOADER, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_lesson_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (dualPane) {
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.putExtra("WEEKDAY", weekdayTo);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    return true;
                }
                Intent intent = new Intent(this, LessonDetailsActivity.class);
                intent.putExtra("LESSON_ID", lessonId);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            case R.id.menu_save_lesson:
                ContentValues values = new ContentValues();
                values.put("weekday", weekday);
                values.put("timeFrom", timeFrom);
                values.put("TimeTo", timeTo);
                values.put("studentId", studentId);
                getContentResolver().update(SchoolContract.SCHEDULE_TABLE_CONTENTURI, values, selection, selectionArgs);
                // Navigate back to the schedule day, where left off:
                intent = new Intent(this, MainActivity.class);
                intent.putExtra("WEEKDAY", weekdayTo);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            default:
                throw new IllegalArgumentException("Invalid itemId: " + item.getItemId());
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        switch (parent.getId()) {
            case R.id.weekdays_spinner:
                if (parent.getItemAtPosition(pos).toString().equalsIgnoreCase("Saturday") && weekday.equalsIgnoreCase("Saturday")) {
                    break;
                } else if (!parent.getItemAtPosition(pos).toString().equalsIgnoreCase("Saturday") && !weekday.equalsIgnoreCase("Saturday")) {
                    weekday = parent.getItemAtPosition(pos).toString();
                    break;
                } else {
                    weekday = parent.getItemAtPosition(pos).toString();
                    // Modify the time arrays, depending on the selected weekday:
                    if (weekday.equals("Saturday")) {
                        timesFromArray = getResources().getStringArray(R.array.saturday_times_from_array);
                        timesToArray = getResources().getStringArray(R.array.saturday_times_to_array);
                    } else {
                        timesFromArray = getResources().getStringArray(R.array.weekdays_times_from_array);
                        timesToArray = getResources().getStringArray(R.array.weekdays_times_to_array);
                    }
                    fromList.clear();
                    fromList.addAll(Arrays.asList(timesFromArray));
                    timesFromAdapter.notifyDataSetChanged();
                    spinnerTimeFrom.setSelection(0);
                    timeFrom = fromList.get(0);

                    toList.clear();
                    toList.addAll(Arrays.asList(timesToArray));
                    timesToAdapter.notifyDataSetChanged();
                    spinnerTimeTo.setSelection(0);
                    timeTo = toList.get(0);

                    break;
                }
            case R.id.time_from_spinner:
                if (parent.getItemAtPosition(pos).toString().equalsIgnoreCase(timeFrom)) {
                    break;
                } else {
                    timeFrom = parent.getItemAtPosition(pos).toString();
                    // get the original length times_to_array, depending on the selected weekday:
                    if (weekday.equals("Saturday")) {
                        timesToArray = getResources().getStringArray(R.array.saturday_times_to_array);
                    } else {
                        timesToArray = getResources().getStringArray(R.array.weekdays_times_to_array);
                    }
                    // shorten the times_to_array, depending on the selected timeFrom:
                    timesToArray = java.util.Arrays.copyOfRange(timesToArray, pos, timesToArray.length);
                    toList.clear();
                    toList.addAll(Arrays.asList(timesToArray));
                    timesToAdapter.notifyDataSetChanged();
                    spinnerTimeTo.setSelection(0);
                    timeTo = toList.get(0);
                    break;
                }
            case R.id.time_to_spinner:
                timeTo = parent.getItemAtPosition(pos).toString();
                break;
            case R.id.student_spinner:
                studentId = (int) id;
                break;
            default:
                throw new IllegalArgumentException("Invalid Id: " + parent.getId());
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // do nothing
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderID, Bundle bundle) {
        switch (loaderID) {
            case LESSONS_LOADER:
                return new CursorLoader(this, uri, null, selection, selectionArgs, null);
            case STUDENTS_LOADER:
                return new CursorLoader(this, SchoolContract.STUDENTS_TABLE_CONTENTURI, projection, null, null, sortOrder);
            default:
                throw new IllegalArgumentException("Invalid loaderID was passed in: " + loaderID);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        switch (loader.getId()) {
            case LESSONS_LOADER:
                cursor.moveToFirst();

                weekday = cursor.getString(cursor.getColumnIndex("weekday"));
                timeFrom = cursor.getString(cursor.getColumnIndex("timeFrom"));
                timeTo = cursor.getString(cursor.getColumnIndex("timeTo"));
                firstName = cursor.getString(cursor.getColumnIndex("firstName"));
                lastName = cursor.getString(cursor.getColumnIndex("lastName"));

                if (weekdayTo == null) weekdayTo = weekday;

                if (weekday.equals("Saturday")) {
                    timesFromArray = getResources().getStringArray(R.array.saturday_times_from_array);
                    timesToArray = getResources().getStringArray(R.array.saturday_times_to_array);
                } else {
                    timesFromArray = getResources().getStringArray(R.array.weekdays_times_from_array);
                    timesToArray = getResources().getStringArray(R.array.weekdays_times_to_array);
                }

                spinnerWeekdays.setAdapter(weekdaysAdapter);
                spinnerWeekdays.setSelection(weekdaysAdapter.getPosition(weekday));

                fromList.addAll(Arrays.asList(timesFromArray));
                timesFromAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, fromList);
                spinnerTimeFrom.setAdapter(timesFromAdapter);
                spinnerTimeFrom.setSelection(timesFromAdapter.getPosition(timeFrom));

                // shorten the times_to_array, depending on the selected timeFrom:
                timesToArray = java.util.Arrays.copyOfRange(timesToArray, timesFromAdapter.getPosition(timeFrom), timesToArray.length);
                toList.addAll(Arrays.asList(timesToArray));
                timesToAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, toList);
                spinnerTimeTo.setAdapter(timesToAdapter);
                spinnerTimeTo.setSelection(timesToAdapter.getPosition(timeTo));

                getLoaderManager().initLoader(STUDENTS_LOADER, null, this);
                break;
            case STUDENTS_LOADER:
                studentsAdapter.swapCursor(cursor);
                cursor.moveToFirst();
                // Select the current student:
                for (int i = 0; i < cursor.getCount(); i++) {
                    if ((cursor.getString(cursor.getColumnIndex("firstName")).equalsIgnoreCase(firstName) &&
                            cursor.getString(cursor.getColumnIndex("lastName")).equals(lastName))) {
                        spinnerStudents.setSelection(i);
                        studentsAdapter.notifyDataSetChanged();
                        break;
                    }
                    cursor.moveToNext();
                }
                break;
            default:
                throw new IllegalArgumentException("Invalid ID was passed in: " + loader.getId());
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId()) {
            case LESSONS_LOADER:
                timesFromAdapter.clear();
                timesToAdapter.clear();
                break;
            case STUDENTS_LOADER:
                studentsAdapter.swapCursor(null);
                break;
            default:
                throw new IllegalArgumentException("Invalid ID was passed in: " + loader.getId());
        }
    }
}