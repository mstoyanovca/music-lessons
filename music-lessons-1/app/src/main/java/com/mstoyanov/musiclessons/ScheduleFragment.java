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
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.mstoyanov.musiclessons.data.SchoolContract;
import com.mstoyanov.musiclessons.data.SchoolContract.Schedule;
import com.mstoyanov.musiclessons.data.SchoolContract.Students;
import com.mstoyanov.musiclessons.model.Actions;
import com.mstoyanov.musiclessons.model.ActionsAdapter;

import java.util.ArrayList;
import java.util.List;

/*	Six ScheduleFragments are available under the Schedule tab, one for each weekday.
 * 	The ScheduleFragment is started by the MainActivity, with weekday passed in.
 * 	In single pane mode, only "add lesson" menu is available, it starts the AddLessonActivity.
 * 	Students, already in the database, can be assigned lesson day and time.
 * 	The LessonDetailsActivity starts on lesson item click, with lesson Id passed in, it reveals
 * 	student contact data and it allows call, SMS or email. From the LessonDetailsActivity,
 * 	the lesson can be deleted, or the EditLessonActivity can be called, again with
 * 	lesson Id passed in.
 * 
 * 	In dual pane mode, the LessonDetailsActivity logic and layout is incorporated into the
 * 	ScheduleFragment, so the "edit" and "delete" menus appear in the ScheduleFragment in dual
 * 	pane mode only.*/

public class ScheduleFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, OnItemClickListener {
    private View view;
    private String weekday;
    private int lessonId;
    private boolean dualPane;
    // lessons are assigned to this day:
    private boolean lessonsBooked;
    private int selectedLesson;
    private List<Actions> actions;  // call, SMS, email
    private ListView lessonsList;
    private ListView actionsList;
    private TextView firstNameTextView;
    private TextView lastNameTextView;
    private TextView firstNameLabel;
    private TextView lastNameLabel;
    private SimpleCursorAdapter lessonsAdapter;
    private ActionsAdapter actionsAdapter;
    private static final int LESSONS_LOADER = 0;
    private static final int ACTIONS_LOADER = 1;
    private static final String[] projection_lessons = {
            Schedule.COLUMN_NAME_LESSON_ID + " as _id",
            Schedule.COLUMN_NAME_TIME_FROM, Schedule.COLUMN_NAME_TIME_TO,
            "schedule." + Schedule.COLUMN_NAME_STUDENT_ID,
            Students.COLUMN_NAME_FIRST_NAME, Students.COLUMN_NAME_LAST_NAME};
    private static final String selection_lessons = "weekday = ?";
    private String[] selectionArgs_lessons = new String[1];
    private static final String sortOrder = Schedule.COLUMN_NAME_TIME_FROM + ", " + Schedule.COLUMN_NAME_TIME_TO;
    private static final String[] projection_actions = {
            Students.COLUMN_NAME_STUDENT_ID + " as _id",
            Students.COLUMN_NAME_HOME_PHONE, Students.COLUMN_NAME_CELL_PHONE,
            Students.COLUMN_NAME_WORK_PHONE, Students.COLUMN_NAME_EMAIL};
    private static final String selection_actions = "studentID = ?";
    private String[] selectionArgs_actions = new String[1];
    private static final String selection_delete = "lessonId = ?";
    private String[] selectionArgs_delete = new String[1];

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.schedule_fragment, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);

        // get the weekday from the MainActivity:
        weekday = getArguments().getString("WEEKDAY");
        ((TextView) view.findViewById(R.id.weekday)).setText(weekday);
        selectionArgs_lessons[0] = weekday;

        View actionsView = getActivity().findViewById(R.id.lesson_details_pane);
        dualPane = actionsView != null && actionsView.getVisibility() == View.VISIBLE;

        String[] fromColumns = new String[]{"timeFrom", "timeTo", "firstName", "lastName"};
        int[] toViews = new int[]{R.id.timeFrom, R.id.timeTo, R.id.firstName, R.id.lastName};
        lessonsAdapter = new SimpleCursorAdapter(getActivity(), R.layout.lesson_list_item, null, fromColumns, toViews, 0);
        lessonsList = view.findViewById(R.id.lessons_list);
        lessonsList.setOnItemClickListener(this);
        lessonsList.setAdapter(lessonsAdapter);

        // in dual pane mode, the controls from the LessonDetailsActivity
        // appear in the right column of the ScheduleFragment:
        if (dualPane) {
            if (savedInstanceState != null) {
                selectedLesson = savedInstanceState.getInt("SELECTED_LESSON", 0);
            }

            firstNameLabel = view.findViewById(R.id.label_fname_schedule);
            firstNameTextView = view.findViewById(R.id.fname_schedule);
            lastNameLabel = view.findViewById(R.id.label_lname_schedule);
            lastNameTextView = view.findViewById(R.id.lname_schedule);

            actionsList = view.findViewById(R.id.actions_list);
            actionsList.setOnItemClickListener(this);
            actions = new ArrayList<>();
            actionsAdapter = new ActionsAdapter(getActivity(), actions);
            actionsList.setAdapter(actionsAdapter);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(LESSONS_LOADER, null, this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (dualPane) outState.putInt("SELECTED_LESSON", selectedLesson);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.schedule_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add_lesson:
                Intent intent = new Intent(getActivity(), AddLessonActivity.class);
                intent.putExtra("WEEKDAY", weekday);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            case R.id.menu_edit_lesson:
                if (lessonsBooked) {
                    intent = new Intent(getActivity(), EditLessonActivity.class);
                    intent.putExtra("LESSON_ID", lessonId);
                    intent.putExtra("DUAL_PANE", dualPane);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    return true;
                }
                return true;
            case R.id.menu_delete_lesson:
                if (lessonsBooked) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                    alertDialogBuilder.setMessage("Delete lesson?").setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                            int id) {
                            getActivity().getContentResolver().delete(SchoolContract.SCHEDULE_TABLE_CONTENTURI, selection_delete, selectionArgs_delete);
                            Intent intent = new Intent(getActivity(), MainActivity.class);
                            intent.putExtra("WEEKDAY", weekday);
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
            case R.id.menu_export_schedule:
                intent = new Intent(getActivity(), ExportScheduleActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            case R.id.menu_settings:
                intent = new Intent(getActivity(), SettingsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            default:
                throw new IllegalArgumentException("Invalid ItemId: " + item.getItemId());
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.lessons_list:
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                int studentId = cursor.getInt(cursor.getColumnIndex("studentID"));
                lessonId = cursor.getInt(cursor.getColumnIndex("_id"));
                String firstName = cursor.getString(cursor.getColumnIndex("firstName"));
                String lastName = cursor.getString(cursor.getColumnIndex("lastName"));
                if (dualPane) {
                    selectionArgs_delete[0] = String.valueOf(lessonId);

                    lessonsList.setItemChecked(position, true);
                    selectedLesson = position;

                    selectionArgs_actions[0] = String.valueOf(studentId);

                    firstNameLabel.setText("First Name");
                    firstNameTextView.setText(firstName);
                    lastNameLabel.setText("Last Name");
                    lastNameTextView.setText(lastName);

                    getLoaderManager().restartLoader(ACTIONS_LOADER, null, this);
                } else {
                    Intent intent = new Intent(getActivity(), LessonDetailsActivity.class);
                    intent.putExtra("LESSON_ID", lessonId);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
                break;
            case R.id.actions_list:
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
                        break;
                    default:
                        throw new IllegalArgumentException("Invalid action type: " + action.getType());
                }
                break;
            default:
                throw new IllegalArgumentException("Invalid parentId: " + parent.getId());
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderID, Bundle bundle) {
        switch (loaderID) {
            case LESSONS_LOADER:
                String innerJoin = " INNER JOIN " + Students.TABLE_NAME + " ON "
                        + "schedule." + Schedule.COLUMN_NAME_STUDENT_ID + " = "
                        + "students." + Students.COLUMN_NAME_STUDENT_ID;
                Uri uri = Uri.withAppendedPath(SchoolContract.SCHEDULE_TABLE_CONTENTURI, innerJoin);
                return new CursorLoader(getActivity(), uri, projection_lessons, selection_lessons, selectionArgs_lessons, sortOrder);
            case ACTIONS_LOADER:
                return new CursorLoader(getActivity(), SchoolContract.STUDENTS_TABLE_CONTENTURI, projection_actions, selection_actions, selectionArgs_actions, null);
            default:
                throw new IllegalArgumentException("Invalid loaderID: " + loaderID);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        switch (loader.getId()) {
            case LESSONS_LOADER:
                lessonsAdapter.swapCursor(cursor);
                if (dualPane && cursor.getCount() > 0) {
                    lessonsBooked = true;
                    onItemClick(lessonsList, lessonsList.getChildAt(selectedLesson), selectedLesson, -1);
                }
                break;
            case ACTIONS_LOADER:
                if (cursor != null) {
                    cursor.moveToFirst();
                    actions.clear();
                    firstNameLabel.setText(getString(R.string.first_name));
                    lastNameLabel.setText(getString(R.string.last_name));
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
                    actionsAdapter.notifyDataSetChanged();
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown loaderId: " + loader.getId());
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId()) {
            case LESSONS_LOADER:
                lessonsAdapter.swapCursor(null);
                break;
            case ACTIONS_LOADER:
                actionsAdapter.clear();
                break;
            default:
                throw new IllegalArgumentException("Unknown loaderId: " + loader.getId());
        }
    }
}