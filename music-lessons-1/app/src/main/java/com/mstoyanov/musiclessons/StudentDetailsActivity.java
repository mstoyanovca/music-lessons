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
import com.mstoyanov.musiclessons.model.Actions;
import com.mstoyanov.musiclessons.model.ActionsAdapter;

import java.util.ArrayList;
import java.util.List;

/*	StudentDetailsActivity is available in single pane mode only.
 * 	It is being called from the StudentsFragment of the MainActivity,
 * 	with the studentId of the selected student passed in.	*/

public class StudentDetailsActivity extends ListActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private int studentID;
    private List<Actions> actions; // call, SMS, email
    private ActionsAdapter adapter;

    private static final int STUDENT_DETAILS_LOADER = 0;
    private static final int LESSONS_LOADER = 1;

    private static final String selection = "studentID = ?";
    private String[] selectionArgs = new String[1];

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.student_details);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setTitle("Student Details");

        studentID = getIntent().getIntExtra("STUDENT_ID", 0);
        selectionArgs[0] = String.valueOf(studentID);

        actions = new ArrayList<>();
        adapter = new ActionsAdapter(this, actions);

        getLoaderManager().initLoader(STUDENT_DETAILS_LOADER, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.student_details_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Return to the Students tab:
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("SELECTED_TAB", 1);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            case R.id.menu_edit_student:
                intent = new Intent(this, EditStudentActivity.class);
                intent.putExtra("STUDENT_ID", studentID);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            case R.id.menu_delete_student:
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setMessage("Delete student?").setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int id) {
                        getContentResolver().delete(SchoolContract.STUDENTS_TABLE_CONTENTURI, selection, selectionArgs);
                        // check if this student has associated lessons, and delete them, if so:
                        getLoaderManager().initLoader(LESSONS_LOADER, null, StudentDetailsActivity.this);
                        Intent intent = new Intent(StudentDetailsActivity.this, MainActivity.class);
                        intent.putExtra("SELECTED_TAB", 1);
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
            case STUDENT_DETAILS_LOADER:
                return new CursorLoader(this, SchoolContract.STUDENTS_TABLE_CONTENTURI, null, selection, selectionArgs, null);
            case LESSONS_LOADER:
                return new CursorLoader(this, SchoolContract.SCHEDULE_TABLE_CONTENTURI, null, selection, selectionArgs, null);
            default:
                throw new IllegalArgumentException("Invalid loaderID: " + loaderID);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        switch (loader.getId()) {
            case STUDENT_DETAILS_LOADER:
                cursor.moveToFirst();

                String firstName = cursor.getString(cursor.getColumnIndex("firstName"));
                String lastName = cursor.getString(cursor.getColumnIndex("lastName"));

                TextView studentName = findViewById(R.id.studentName);
                studentName.setText(firstName + " " + lastName);

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
                ActionsAdapter adapter = new ActionsAdapter(this, actions);
                setListAdapter(adapter);
                break;
            case LESSONS_LOADER:
                if (cursor.getCount() > 0) {
                    getContentResolver().delete(SchoolContract.SCHEDULE_TABLE_CONTENTURI, selection, selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Invalid loaderID: " + loader.getId());
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId()) {
            case STUDENT_DETAILS_LOADER:
                adapter.clear();
                setListAdapter(adapter);
                break;
            case LESSONS_LOADER:
                // the cursor is null at this line;
                // no adapter to clear;
                break;
            default:
                throw new IllegalArgumentException("Invalid loaderID: " + loader.getId());
        }
    }
}