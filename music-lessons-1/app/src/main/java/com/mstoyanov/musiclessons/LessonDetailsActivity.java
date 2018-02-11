package com.mstoyanov.musiclessons;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.mstoyanov.musiclessons.data.SchoolContract;
import com.mstoyanov.musiclessons.data.SchoolContract.Schedule;
import com.mstoyanov.musiclessons.data.SchoolContract.Students;
import com.mstoyanov.musiclessons.model.Actions;
import com.mstoyanov.musiclessons.model.ActionsAdapter;

import java.util.ArrayList;
import java.util.List;

public class LessonDetailsActivity extends ListActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private int lessonId;
    private String weekday;
    private List<Actions> actions;
    private ActionsAdapter adapter;

    private TextView weekdayTextView;
    private TextView timeTextView;
    private TextView nameTextView;

    private static final int LESSON_DETAILS_LOADER = 0;

    private static final String innerJoin = " INNER JOIN "
            + Students.TABLE_NAME + " ON " + "schedule."
            + Schedule.COLUMN_NAME_STUDENT_ID + " = " + "students."
            + Students.COLUMN_NAME_STUDENT_ID;
    private static final Uri uri = Uri.withAppendedPath(SchoolContract.SCHEDULE_TABLE_CONTENTURI, innerJoin);
    private static final String selection = "lessonID = ?";
    private String[] selectionArgs = new String[1];

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lesson_details);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setTitle("Lesson Details");

        lessonId = getIntent().getIntExtra("LESSON_ID", 0);
        selectionArgs[0] = String.valueOf(lessonId);

        actions = new ArrayList<>();
        adapter = new ActionsAdapter(this, actions);
        setListAdapter(adapter);

        weekdayTextView = findViewById(R.id.weekday);
        timeTextView = findViewById(R.id.timeFrom);
        nameTextView = findViewById(R.id.firstName);

        getLoaderManager().initLoader(LESSON_DETAILS_LOADER, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.lesson_details_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // return to the same day of the schedule:
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("WEEKDAY", weekday);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            case R.id.menu_edit_lesson:
                intent = new Intent(this, EditLessonActivity.class);
                intent.putExtra("LESSON_ID", lessonId);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            case R.id.menu_delete_lesson:
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(LessonDetailsActivity.this);
                alertDialogBuilder.setMessage("Delete lesson?").setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int id) {
                        getContentResolver().delete(SchoolContract.SCHEDULE_TABLE_CONTENTURI, selection, selectionArgs);
                        Intent intent = new Intent(LessonDetailsActivity.this, MainActivity.class);
                        intent.putExtra("WEEKDAY", weekday);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int id) {
                        dialog.cancel();
                    }
                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                return true;
            default:
                throw new IllegalArgumentException("Invalid itemId: " + item.getItemId());
        }
    }

    @Override
    public void onListItemClick(ListView lv, View view, int position, long id) {
        Actions action = actions.get(position);
        switch (action.getType()) {
            case Actions.ACTION_CALL:
                Uri callUri = Uri.parse("tel:" + action.getData());
                Intent intent = new Intent(Intent.ACTION_CALL, callUri);
                startActivity(intent);
                break;
            case Actions.ACTION_EMAIL:
                intent = new Intent(Intent.ACTION_SEND);
                intent.setType("plain/text");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{action.getData()});
                startActivity(intent);
                break;
            case Actions.ACTION_SMS:
                Uri smsUri = Uri.parse("sms:" + action.getData());
                intent = new Intent(Intent.ACTION_VIEW, smsUri);
                startActivity(intent);
                break;
            default:
                throw new IllegalArgumentException("Invalid action type: " + action.getType());
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderID, Bundle bundle) {
        switch (loaderID) {
            case LESSON_DETAILS_LOADER:
                return new CursorLoader(this, uri, null, selection, selectionArgs, null);
            default:
                throw new IllegalArgumentException("Invalid loaderID was passed in: " + loaderID);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        switch (loader.getId()) {
            case LESSON_DETAILS_LOADER:
                cursor.moveToFirst();

                weekday = cursor.getString(cursor.getColumnIndex("weekday"));
                String timeFrom = cursor.getString(cursor.getColumnIndex("timeFrom"));
                String timeTo = cursor.getString(cursor.getColumnIndex("timeTo"));
                String firstName = cursor.getString(cursor.getColumnIndex("firstName"));
                String lastName = cursor.getString(cursor.getColumnIndex("lastName"));

                weekdayTextView.setText(weekday);
                timeTextView.setText(timeFrom + " - " + timeTo);
                nameTextView.setText(firstName + " " + lastName);

                String homePhone = cursor.getString(cursor.getColumnIndex("homePhone"));
                if (homePhone.length() > 0) {
                    actions.add(new Actions("Home", homePhone, Actions.ACTION_CALL));
                }
                String cellPhone = cursor.getString(cursor.getColumnIndex("cellPhone"));
                if (cellPhone.length() > 0) {
                    actions.add(new Actions("Mobile", cellPhone, Actions.ACTION_CALL));
                    actions.add(new Actions("SMS", cellPhone, Actions.ACTION_SMS));
                }
                String workPhone = cursor.getString(cursor.getColumnIndex("workPhone"));
                if (workPhone.length() > 0) {
                    actions.add(new Actions("Office", workPhone, Actions.ACTION_CALL));
                }
                String email = cursor.getString(cursor.getColumnIndex("email"));
                if (email.length() > 0) {
                    actions.add(new Actions("Email", email, Actions.ACTION_EMAIL));
                }
                adapter.notifyDataSetChanged();
                break;
            default:
                throw new IllegalArgumentException("Invalid loaderID: " + loader.getId());
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId()) {
            case LESSON_DETAILS_LOADER:
                adapter.clear();
                break;
            default:
                throw new IllegalArgumentException("Invalid loaderID: " + loader.getId());
        }
    }
}