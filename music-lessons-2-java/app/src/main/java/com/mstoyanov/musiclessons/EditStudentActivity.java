package com.mstoyanov.musiclessons;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.mstoyanov.musiclessons.model.PhoneNumber;
import com.mstoyanov.musiclessons.model.PhoneNumberType;
import com.mstoyanov.musiclessons.model.Student;
import com.mstoyanov.musiclessons.repository.AppDatabase;

import java.lang.ref.WeakReference;

public class EditStudentActivity extends AppCompatActivity {
    private EditText firstName;
    private EditText lastName;
    private EditText email;
    private EditText notes;
    private EditStudentAdapter adapter;
    private ProgressBar progressBar;
    private static Student student;
    private static final AppDatabase DB = MusicLessonsApplication.getDB();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_student);
        student = (Student) getIntent().getSerializableExtra("STUDENT");

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firstName = findViewById(R.id.first_name);
        firstName.setText(student.getFirstName());
        firstName.addTextChangedListener(new EditStudentTextWatcher(this));
        lastName = findViewById(R.id.last_name);
        lastName.setText(student.getLastName());
        lastName.addTextChangedListener(new EditStudentTextWatcher(this));
        email = findViewById(R.id.email);
        email.setText(student.getEmail());
        notes = findViewById(R.id.notes);
        notes.setText(student.getNotes());

        progressBar = findViewById(R.id.progress_bar);
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.GONE);

        final RecyclerView recyclerView = findViewById(R.id.phone_numbers_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        // to avoid loosing focus when adding/removing an item (item animator bug):
        recyclerView.setItemAnimator(null);
        adapter = new EditStudentAdapter(student.getPhoneNumbers());
        adapter.setHasStableIds(true);  // needed for the item animator bug
        recyclerView.setAdapter(adapter);

        final FloatingActionButton button = findViewById(R.id.add_phone_number);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                student.getPhoneNumbers().add(new PhoneNumber("", PhoneNumberType.HOME));
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_edit_student, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.action_update:
                updateStudent();
                return true;
            case R.id.action_delete:
                createAlertDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (studentIsValid()) {
            menu.findItem(R.id.action_update).setEnabled(true);
            menu.findItem(R.id.action_update).getIcon().setAlpha(255);
        } else {
            menu.findItem(R.id.action_update).setEnabled(false);
            menu.findItem(R.id.action_update).getIcon().setAlpha(127);
        }
        return true;
    }

    public void stopProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

    private class EditStudentTextWatcher implements TextWatcher {
        private Activity activity;

        EditStudentTextWatcher(Activity activity) {
            this.activity = activity;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // do nothing
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // do nothing
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (nameIsValid()) {
                firstName.setError(null);
            } else {
                firstName.setError(activity.getResources().getString(R.string.name_error));
            }
            activity.invalidateOptionsMenu();
        }
    }

    private void updateStudent() {
        student.setFirstName(stripString(firstName.getText().toString()));
        student.setLastName(stripString(lastName.getText().toString()));
        student.setPhoneNumbers(adapter.getPhoneNumbers());
        student.setEmail(stripString(email.getText().toString()));
        student.setNotes(stripString(notes.getText().toString()));

        progressBar.setVisibility(View.VISIBLE);
        new UpdateStudent(progressBar, this).execute();
    }

    private void createAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String message;
        if (student.getFirstName().replaceAll("\\s+", "").length() == 0 &&
                student.getLastName().replace(" ", "").length() != 0) {
            message = "Delete student " + student.getLastName() + "?";
        } else if (student.getFirstName().replace(" ", "").length() != 0 &&
                student.getLastName().replace(" ", "").length() == 0) {
            message = "Delete student " + student.getFirstName() + "?";
        } else {
            message = "Delete student " + student.getFirstName() + " " + student.getLastName() + "?";
        }
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteStudent();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // do nothing
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void deleteStudent() {
        progressBar.setVisibility(View.VISIBLE);
        new DeleteStudent(progressBar, this).execute();
    }

    private boolean studentIsValid() {
        if (!nameIsValid()) return false;
        for (PhoneNumber phoneNumber : student.getPhoneNumbers()) {
            if (!phoneNumber.isValid()) return false;
        }
        return true;
    }

    private boolean nameIsValid() {
        return !(firstName.getText().toString().replaceAll("\\s+", "").length() == 0 &&
                lastName.getText().toString().replaceAll("\\s+", "").length() == 0);
    }

    private String stripString(String string) {
        if (string.replaceAll("\\s+", "").length() != 0) {
            return string.trim();
        } else {
            return "";
        }
    }

    private static class UpdateStudent extends AsyncTask<Student, Integer, Student> {
        private final WeakReference<ProgressBar> progressBarWeakReference;
        private final WeakReference<Context> contextWeakReference;

        UpdateStudent(ProgressBar progressBar, Context context) {
            this.progressBarWeakReference = new WeakReference<>(progressBar);
            this.contextWeakReference = new WeakReference<>(context);
        }

        @Override
        protected Student doInBackground(Student... params) {
            /*try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/
            DB.getStudentDao().update(student);
            for (PhoneNumber phoneNumber : student.getPhoneNumbers()) {
                phoneNumber.setStudentId(student.getStudentId());
            }
            DB.getPhoneNumberDao().insertAll(student.getPhoneNumbers());
            return student;
        }

        @Override
        protected void onPostExecute(Student result) {
            final ProgressBar progressBar = progressBarWeakReference.get();
            if (progressBar != null) progressBar.setVisibility(View.GONE);
            final Context context = contextWeakReference.get();
            if (context != null) {
                Intent intent = new Intent(context, StudentDetailsActivity.class);
                intent.putExtra("UPDATED_STUDENT", student);
                context.startActivity(intent);
            }
        }
    }

    private static class DeleteStudent extends AsyncTask<Void, Integer, Student> {
        private final WeakReference<ProgressBar> progressBarWeakReference;
        private final WeakReference<Context> contextWeakReference;

        DeleteStudent(ProgressBar progressBar, Context context) {
            this.progressBarWeakReference = new WeakReference<>(progressBar);
            this.contextWeakReference = new WeakReference<>(context);
        }

        @Override
        protected Student doInBackground(Void... params) {
            /*try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/
            DB.getStudentDao().delete(student);
            return student;
        }

        @Override
        protected void onPostExecute(Student student) {
            final ProgressBar progressBar = progressBarWeakReference.get();
            if (progressBar != null) progressBar.setVisibility(View.GONE);
            final Context context = contextWeakReference.get();
            if (context != null) {
                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra("DELETED_STUDENT_ID", student.getStudentId());
                context.startActivity(intent);
            }
        }
    }
}