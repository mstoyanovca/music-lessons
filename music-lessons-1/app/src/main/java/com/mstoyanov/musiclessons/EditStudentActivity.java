package com.mstoyanov.musiclessons;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.mstoyanov.musiclessons.data.SchoolContract;

/*	The EditStudentActivity is being called with a studentId
 * 	passed in from the StudentDetailsActivity in single pane mode,
 * 	or directly from the StudentsFragment in dual pane mode, since
 * 	in dual pane mode the StudentsFragment incorporates the
 *  StudentDetailsActivity.
 * 
 * 	On "save" it returns to the StudentDetailsActivity in single pane mode,
 * 	passing studentId, or directly to the students list in dual pane,
 * 	passing the selectedStudent index, so his selection can be preserved.	*/

public class EditStudentActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor> {
    private int studentId;
    private int selectedStudent;
    private boolean dualPane;

    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText homePhoneEditText;
    private EditText cellPhoneEditText;
    private EditText workPhoneEditText;
    private EditText emailEditText;

    private static final int STUDENT_DETAILS_LOADER = 0;
    private static final String selection = "studentID = ?";
    private static final String[] selectionArgs = new String[1];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_student);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setTitle("Edit Student");

        // preserve the selected student, on return to schedule:
        selectedStudent = getIntent().getIntExtra("SELECTED_STUDENT", 0);
        dualPane = getIntent().getBooleanExtra("DUAL_PANE", false);
        studentId = getIntent().getIntExtra("STUDENT_ID", 0);
        selectionArgs[0] = String.valueOf(studentId);

        firstNameEditText = findViewById(R.id.first_name);
        lastNameEditText = findViewById(R.id.last_name);
        homePhoneEditText = findViewById(R.id.home_phone);
        homePhoneEditText.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        cellPhoneEditText = findViewById(R.id.cell_phone);
        cellPhoneEditText.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        workPhoneEditText = findViewById(R.id.work_phone);
        workPhoneEditText.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        emailEditText = findViewById(R.id.email);

        getLoaderManager().initLoader(STUDENT_DETAILS_LOADER, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_student_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (dualPane) {
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.putExtra("SELECTED_TAB", 1);
                    intent.putExtra("SELECTED_STUDENT", selectedStudent);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    return true;
                } else {
                    Intent intent = new Intent(this, StudentDetailsActivity.class);
                    intent.putExtra("STUDENT_ID", studentId);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    return true;
                }
            case R.id.menu_save:
                ContentValues values = new ContentValues();
                if (validateStudent()) {
                    values.put("firstName", firstNameEditText.getText().toString().trim());
                    values.put("lastName", lastNameEditText.getText().toString().trim());
                    values.put("homePhone", homePhoneEditText.getText().toString().trim());
                    values.put("cellPhone", cellPhoneEditText.getText().toString().trim());
                    values.put("workPhone", workPhoneEditText.getText().toString().trim());
                    values.put("email", emailEditText.getText().toString().trim());
                } else {
                    return true;
                }
                if (getContentResolver().update(SchoolContract.STUDENTS_TABLE_CONTENTURI, values, selection, selectionArgs) > 0) {
                    if (dualPane) {
                        Intent intent = new Intent(this, MainActivity.class);
                        intent.putExtra("SELECTED_TAB", 1);
                        intent.putExtra("SELECTED_STUDENT", selectedStudent);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        return true;
                    } else {
                        Intent intent = new Intent(this, StudentDetailsActivity.class);
                        intent.putExtra("STUDENT_ID", studentId);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        return true;
                    }
                }
            default:
                throw new IllegalArgumentException("Invalid itemId: " + item.getItemId());
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {
        switch (loaderId) {
            case STUDENT_DETAILS_LOADER:
                return new CursorLoader(this, SchoolContract.STUDENTS_TABLE_CONTENTURI, null, selection, selectionArgs, null);
            default:
                throw new IllegalArgumentException("Invalid loaderID: " + loaderId);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        switch (loader.getId()) {
            case STUDENT_DETAILS_LOADER:
                cursor.moveToFirst();

                String firstName = cursor.getString(cursor.getColumnIndex("firstName"));
                String lastName = cursor.getString(cursor.getColumnIndex("lastName"));
                String homePhone = cursor.getString(cursor.getColumnIndex("homePhone"));
                String cellPhone = cursor.getString(cursor.getColumnIndex("cellPhone"));
                String workPhone = cursor.getString(cursor.getColumnIndex("workPhone"));
                String email = cursor.getString(cursor.getColumnIndex("email"));

                firstNameEditText.setText(firstName);
                lastNameEditText.setText(lastName);
                homePhoneEditText.setText(homePhone);
                cellPhoneEditText.setText(cellPhone);
                workPhoneEditText.setText(workPhone);
                emailEditText.setText(email);

                break;
            default:
                throw new IllegalArgumentException("Invalid loaderID: " + loader.getId());
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId()) {
            case STUDENT_DETAILS_LOADER:
                break;
            default:
                throw new IllegalArgumentException("Invalid loaderID: " + loader.getId());
        }
    }

    public boolean validateStudent() {
        if (firstNameEditText.getText().toString().trim().length() == 0 && lastNameEditText.getText().toString().trim().length() == 0) {
            firstNameEditText.setError("A name is required");
            return false;
        } else {
            firstNameEditText.setError(null);
        }
        return true;
    }
}