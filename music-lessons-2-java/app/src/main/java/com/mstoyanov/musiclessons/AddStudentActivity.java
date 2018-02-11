package com.mstoyanov.musiclessons;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
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
import java.util.ArrayList;
import java.util.List;

public class AddStudentActivity extends AppCompatActivity {
    private ProgressBar progressBar;
    private EditText firstName;
    private TextWatcher firstNameTextWatcher;
    private EditText lastName;
    private AddStudentAdapter adapter;
    private static Student student;
    private boolean pristine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_student);

        if (savedInstanceState == null) {
            List<PhoneNumber> phoneNumbers = new ArrayList<>();
            phoneNumbers.add(new PhoneNumber("", PhoneNumberType.HOME));
            student = new Student();
            student.setPhoneNumbers(phoneNumbers);
            pristine = true;
        } else {
            student = (Student) savedInstanceState.getSerializable("STUDENT");
            pristine = (boolean) savedInstanceState.get("PRISTINE");
        }

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressBar = findViewById(R.id.progress_bar);
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.GONE);

        firstName = findViewById(R.id.first_name);
        firstNameTextWatcher = new NameTextWatcher(this);
        firstName.addTextChangedListener(firstNameTextWatcher);
        lastName = findViewById(R.id.last_name);
        lastName.addTextChangedListener(new NameTextWatcher(this));

        final RecyclerView recyclerView = findViewById(R.id.phone_numbers_list);
        // to avoid loosing focus when adding/removing an item (item animator bug):
        recyclerView.setItemAnimator(null);
        adapter = new AddStudentAdapter(student.getPhoneNumbers());
        adapter.setHasStableIds(true);  // needed for the item animator bug
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        final FloatingActionButton button = findViewById(R.id.add_phone_number);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                student.getPhoneNumbers().add(new PhoneNumber("", PhoneNumberType.HOME));
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        state.putSerializable("STUDENT", student);
        state.putBoolean("PRISTINE", pristine);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_insert_student, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.action_insert:
                insertStudent();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (studentIsValid()) {
            menu.findItem(R.id.action_insert).setEnabled(true);
            menu.findItem(R.id.action_insert).getIcon().setAlpha(255);
        } else {
            menu.findItem(R.id.action_insert).setEnabled(false);
            menu.findItem(R.id.action_insert).getIcon().setAlpha(127);
        }
        return true;
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

    private void insertStudent() {
        student.setFirstName(stripString(firstName.getText().toString()));
        student.setLastName(stripString(lastName.getText().toString()));

        student.setPhoneNumbers(adapter.getPhoneNumbers());

        EditText email = findViewById(R.id.email);
        student.setEmail(stripString(email.getText().toString()));

        EditText notes = findViewById(R.id.notes);
        student.setNotes(stripString(notes.getText().toString()));

        progressBar.setVisibility(View.VISIBLE);
        new AddStudent(progressBar, this).execute(student);
    }

    private String stripString(String string) {
        if (string.replaceAll("\\s+", "").length() != 0) {
            return string.trim();
        } else {
            return "";
        }
    }

    public void invokeTextChanged() {
        firstNameTextWatcher.afterTextChanged(firstName.getText());
    }

    public boolean isPristine() {
        return pristine;
    }

    public void setPristine(boolean pristine) {
        this.pristine = pristine;
    }

    private class NameTextWatcher implements TextWatcher {
        private final Activity activity;

        NameTextWatcher(Activity activity) {
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
            if (pristine && s.length() > 0) {
                pristine = false;
                adapter.notifyDataSetChanged();
            }
            if (nameIsValid()) {
                firstName.setError(null);
            } else {
                if (!pristine)
                    firstName.setError(activity.getResources().getString(R.string.name_error));
            }
            activity.invalidateOptionsMenu();
        }
    }

    private static class AddStudent extends AsyncTask<Student, Integer, Student> {
        private final WeakReference<ProgressBar> progressBarWeakReference;
        private final WeakReference<Activity> activityWeakReference;
        private final AppDatabase DB = MusicLessonsApplication.getDB();

        AddStudent(ProgressBar progressBar, Activity activity) {
            this.progressBarWeakReference = new WeakReference<>(progressBar);
            this.activityWeakReference = new WeakReference<>(activity);
        }

        @Override
        protected Student doInBackground(Student... params) {
            /*try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/
            long id = DB.getStudentDao().insert(student);
            student.setStudentId(id);
            for (PhoneNumber phoneNumber : student.getPhoneNumbers()) {
                phoneNumber.setStudentId(id);
            }
            DB.getPhoneNumberDao().insertAll(student.getPhoneNumbers());
            return student;
        }

        @Override
        protected void onPostExecute(Student result) {
            final ProgressBar progressBar = progressBarWeakReference.get();
            if (progressBar != null) progressBar.setVisibility(View.GONE);
            final Activity activity = activityWeakReference.get();
            if (activity != null) {
                Intent intent = new Intent(activity, MainActivity.class);
                intent.putExtra("ADDED_STUDENT_ID", student.getStudentId());
                activity.startActivity(intent);
            }
        }
    }
}