package com.mstoyanov.musiclessons;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.mstoyanov.musiclessons.data.SchoolContract;
import com.mstoyanov.musiclessons.model.Actions;
import com.mstoyanov.musiclessons.model.ActionsAdapter;

import java.util.ArrayList;
import java.util.List;

/* 	A fragment of the MainActivity, under tab 2 of total 3 tabs,
 * 	which Displays a list of all students.
 * 
 * 	In single pane mode it calls StudentDetailsActivity, with student
 * 	Id passed in. The StudentDetailsActivity contains the clicked student's
 *  contact data, so call, SMS or email action can be performed. 
 * 
 * 	In dual pane mode the layout of the StudentDetailsActivity appears
 * 	on the right side of the screen of the StudentsFragment.	*/

public class StudentsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, OnItemClickListener {
    private boolean dualPane;
    private int selectedStudent;
    // there are students entered into the database:
    private boolean studentsEntered;
    private int studentId;
    private List<Actions> actions; // call, SMS, email
    private SimpleCursorAdapter studentsAdapter;
    private ActionsAdapter studentDetailsAdapter;
    private ListView studentsList;
    private ListView studentDetailsList;
    private TextView firstNameTextView;
    private TextView lastNameTextView;
    private TextView firstNameLabel;
    private TextView lastNameLabel;
    private static final int STUDENTS_LOADER = 0;
    private static final int STUDENT_DETAILS_LOADER = 1;
    private static final int LESSONS_LOADER = 2;
    private static final String selection = "studentID = ?";
    private String[] selectionArgs = new String[1];
    private static final String[] projection = {
            SchoolContract.Students.COLUMN_NAME_STUDENT_ID + " as _id",
            SchoolContract.Students.COLUMN_NAME_FIRST_NAME,
            SchoolContract.Students.COLUMN_NAME_LAST_NAME};
    private static final String SORT_ORDER = SchoolContract.Students.COLUMN_NAME_FIRST_NAME + ", " + SchoolContract.Students.COLUMN_NAME_LAST_NAME;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.students_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);

        String[] fromColumns = new String[]{"firstName", "lastName"};
        int[] toViews = new int[]{R.id.firstName, R.id.lastName};
        studentsAdapter = new SimpleCursorAdapter(getActivity(), R.layout.student_list_item, null, fromColumns, toViews, 0);
        studentsList = getActivity().findViewById(R.id.students_list);
        studentsList.setOnItemClickListener(this);
        studentsList.setAdapter(studentsAdapter);

        View studentDetailsView = getActivity().findViewById(R.id.student_details_pane);
        dualPane = studentDetailsView != null && studentDetailsView.getVisibility() == View.VISIBLE;

        if (dualPane) {
            if (savedInstanceState != null) {
                selectedStudent = savedInstanceState.getInt("SELECTED_STUDENT", 0);
            } else if (getActivity().getIntent() != null) {
                selectedStudent = getActivity().getIntent().getIntExtra("SELECTED_STUDENT", selectedStudent);
            }

            firstNameLabel = getActivity().findViewById(R.id.label_fname_students);
            firstNameTextView = getActivity().findViewById(R.id.fname_students);
            lastNameLabel = getActivity().findViewById(R.id.label_lname_students);
            lastNameTextView = getActivity().findViewById(R.id.lname_students);

            studentDetailsList = getActivity().findViewById(R.id.student_details_list);
            studentDetailsList.setOnItemClickListener(this);
            actions = new ArrayList<>();
            studentDetailsAdapter = new ActionsAdapter(getActivity(), actions);
            studentDetailsList.setAdapter(studentDetailsAdapter);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(STUDENTS_LOADER, null, this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (dualPane) {
            getActivity().getMenuInflater().inflate(R.menu.student_details_menu, menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_edit_student:
                if (studentsEntered) {
                    Intent intent = new Intent(getActivity(), EditStudentActivity.class);
                    intent.putExtra("STUDENT_ID", studentId);
                    intent.putExtra("DUAL_PANE", dualPane);
                    intent.putExtra("SELECTED_STUDENT", selectedStudent);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    return true;
                }
                return true;
            case R.id.menu_delete_student:
                if (studentsEntered) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                    alertDialogBuilder.setMessage("Delete student?").setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                            int id) {
                            getActivity().getContentResolver().delete(SchoolContract.STUDENTS_TABLE_CONTENTURI, selection, selectionArgs);
                            // check if this student has associated lessons, and delete them, if so:
                            getLoaderManager().initLoader(LESSONS_LOADER, null, StudentsFragment.this);
                            Intent intent = new Intent(getActivity(), MainActivity.class);
                            // select the "Students" tab:
                            intent.putExtra("SELECTED_TAB", 1);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                    return true;
                }
                return true;
            default:
                throw new IllegalArgumentException("Invalid ItemId: " + item.getItemId());
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (dualPane) {
            outState.putInt("SELECTED_STUDENT", selectedStudent);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.students_list:
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                studentId = cursor.getInt(cursor.getColumnIndex("_id"));
                String firstName = cursor.getString(cursor.getColumnIndex("firstName"));
                String lastName = cursor.getString(cursor.getColumnIndex("lastName"));
                if (dualPane) {
                    studentsList.setItemChecked(position, true);
                    selectedStudent = position;
                    selectionArgs[0] = String.valueOf(studentId);
                    firstNameLabel.setText("First Name");
                    firstNameTextView.setText(firstName);
                    lastNameLabel.setText("Last Name");
                    lastNameTextView.setText(lastName);
                    getLoaderManager().restartLoader(STUDENT_DETAILS_LOADER, null, this);
                } else {
                    Intent intent = new Intent(getActivity(), StudentDetailsActivity.class);
                    intent.putExtra("STUDENT_ID", studentId);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
                break;
            case R.id.student_details_list:
                Actions action = actions.get(position);
                switch (action.getType()) {
                    case Actions.ACTION_CALL:
                        Uri callUri = Uri.parse("tel:" + action.getData());
                        Intent intent = new Intent(Intent.ACTION_CALL, callUri);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        break;
                    case Actions.ACTION_EMAIL:
                        intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("plain/text");
                        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{action.getData()});
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        break;
                    case Actions.ACTION_SMS:
                        Uri smsUri = Uri.parse("sms:" + action.getData());
                        intent = new Intent(Intent.ACTION_VIEW, smsUri);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    default:
                        throw new IllegalArgumentException("Invalid action type: " + action.getType());
                }
            default:
                throw new IllegalArgumentException("Invalid parentId: " + parent.getId());
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderID, Bundle bundle) {
        switch (loaderID) {
            case STUDENTS_LOADER:
                return new CursorLoader(getActivity(), SchoolContract.STUDENTS_TABLE_CONTENTURI, projection, null, null, SORT_ORDER);
            case STUDENT_DETAILS_LOADER:
                return new CursorLoader(getActivity(), SchoolContract.STUDENTS_TABLE_CONTENTURI, null, selection, selectionArgs, null);
            case LESSONS_LOADER:
                return new CursorLoader(getActivity(), SchoolContract.SCHEDULE_TABLE_CONTENTURI, null, selection, selectionArgs, null);
            default:
                throw new IllegalArgumentException("Invalid loaderID: " + loaderID);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        switch (loader.getId()) {
            case STUDENTS_LOADER:
                studentsAdapter.swapCursor(cursor);
                // select the first student, or recover previous state, if the list is not empty:
                if (dualPane && cursor.getCount() > 0) {
                    studentsEntered = true;
                    onItemClick(studentsList, studentsList.getChildAt(selectedStudent), selectedStudent, -1);
                }
                break;
            case STUDENT_DETAILS_LOADER:
                if (cursor != null) {
                    cursor.moveToFirst();
                    actions.clear();
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
                }
                studentDetailsAdapter.notifyDataSetChanged();
                break;
            case LESSONS_LOADER:
                if (cursor.getCount() > 0) {
                    getActivity().getContentResolver().delete(SchoolContract.SCHEDULE_TABLE_CONTENTURI, selection, selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown loaderId " + loader.getId());
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId()) {
            case STUDENTS_LOADER:
                studentsAdapter.swapCursor(null);
                break;
            case STUDENT_DETAILS_LOADER:
                studentDetailsAdapter.clear();
                break;
            case LESSONS_LOADER:
                // at this point the cursor is out of scope and respectively null;
                // no adapters have been created to clear;
                break;
        }
    }
}