package com.mstoyanov.musiclessons;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.mstoyanov.musiclessons.model.Lesson;
import com.mstoyanov.musiclessons.model.Student;
import com.mstoyanov.musiclessons.model.Weekday;
import com.mstoyanov.musiclessons.repository.AppDatabase;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class EditLessonActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private Spinner students;
    private static EditLessonActivity.StudentsAdapter adapter;
    private NumberPicker hourFrom;
    private NumberPicker minuteFrom;
    private NumberPicker hourTo;
    private NumberPicker minuteTo;
    private ProgressBar progressBar;
    private static Lesson lesson;
    private static List<Student> studentList;
    private static boolean studentListIsEmpty;
    private static final String[] MINUTES = {"00", "15", "30", "45"};
    private static final AppDatabase DB = MusicLessonsApplication.getDB();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_lesson);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressBar = findViewById(R.id.progress_bar);
        progressBar.setIndeterminate(true);

        final Spinner weekday = findViewById(R.id.weekday);
        final ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.weekdays,
                R.layout.weekday_item);
        arrayAdapter.setDropDownViewResource(R.layout.phone_type_dropdown_item);
        weekday.setAdapter(arrayAdapter);
        weekday.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                lesson.setWeekday(Weekday.values()[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // do nothing
            }
        });

        students = findViewById(R.id.students);
        studentList = new ArrayList<>();
        adapter = new EditLessonActivity.StudentsAdapter(this, studentList);
        students.setAdapter(adapter);
        students.setOnItemSelectedListener(this);

        hourFrom = findViewById(R.id.hour_from);
        hourFrom.setMinValue(8);
        hourFrom.setMaxValue(21);
        hourFrom.setWrapSelectorWheel(false);
        hourFrom.setOnValueChangedListener(hourFromOnValueChangedListener);

        minuteFrom = findViewById(R.id.minute_from);
        minuteFrom.setDisplayedValues(MINUTES);
        minuteFrom.setMaxValue(3);
        minuteFrom.setWrapSelectorWheel(true);
        minuteFrom.setOnValueChangedListener(minuteFromOnValueChangedListener);

        hourTo = findViewById(R.id.hour_to);
        hourTo.setMinValue(8);
        hourTo.setMaxValue(22);
        hourTo.setWrapSelectorWheel(false);
        hourTo.setOnValueChangedListener(hourToOnValueChangedListener);

        minuteTo = findViewById(R.id.minute_to);
        minuteTo.setDisplayedValues(MINUTES);
        minuteTo.setMaxValue(3);
        minuteTo.setWrapSelectorWheel(true);
        minuteTo.setOnValueChangedListener(minuteToOnValueChangedListener);

        if (savedInstanceState == null) {
            // coming from LessonDetails:
            lesson = (Lesson) getIntent().getSerializableExtra("LESSON");
            initializeTime();
            studentListIsEmpty = true;
            new EditLessonActivity.LoadStudents(students, progressBar, this).execute();
        } else {
            // after screen rotation:
            progressBar.setVisibility(View.GONE);
            lesson = (Lesson) savedInstanceState.getSerializable("LESSON");
            studentList = (List<Student>) savedInstanceState.getSerializable("STUDENTS");
            adapter.addAll(studentList);
            students.setSelection(studentList.indexOf(lesson.getStudent()));

            hourFrom.setValue(savedInstanceState.getInt("HOUR_FROM"));
            minuteFrom.setValue(savedInstanceState.getInt("MINUTE_FROM"));
            hourTo.setValue(savedInstanceState.getInt("HOUR_TO"));
            minuteTo.setValue(savedInstanceState.getInt("MINUTE_TO"));

            invalidateOptionsMenu();
        }

        weekday.setSelection(lesson.getWeekday().ordinal());
    }

    @Override
    protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);

        state.putSerializable("LESSON", lesson);
        state.putSerializable("STUDENTS", (Serializable) studentList);

        state.putInt("HOUR_FROM", hourFrom.getValue());
        state.putInt("MINUTE_FROM", minuteFrom.getValue());
        state.putInt("HOUR_TO", hourTo.getValue());
        state.putInt("MINUTE_TO", minuteTo.getValue());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_edit_lesson, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.action_update:
                setTime();
                progressBar.setVisibility(View.VISIBLE);
                new UpdateLesson(progressBar, this).execute(lesson);
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
        if (studentListIsEmpty) {
            menu.findItem(R.id.action_update).setEnabled(false);
            menu.findItem(R.id.action_update).getIcon().setAlpha(127);
            menu.findItem(R.id.action_delete).setEnabled(false);
            menu.findItem(R.id.action_delete).getIcon().setAlpha(127);
        } else {
            menu.findItem(R.id.action_update).setEnabled(true);
            menu.findItem(R.id.action_update).getIcon().setAlpha(255);
            menu.findItem(R.id.action_delete).setEnabled(true);
            menu.findItem(R.id.action_delete).getIcon().setAlpha(255);
        }
        return true;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        lesson.setStudentId(studentList.get(i).getStudentId());
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        // do nothing
    }

    private NumberPicker.OnValueChangeListener hourFromOnValueChangedListener = new NumberPicker.OnValueChangeListener() {
        @Override
        public void onValueChange(NumberPicker numberPicker, int oldValue, int newValue) {
            // 21:30 is maximum value:
            if (newValue == 21 && minuteFrom.getValue() == 3) minuteFrom.setValue(2);
            synchronizeTimeToWithTimeFrom();
        }
    };

    private NumberPicker.OnValueChangeListener minuteFromOnValueChangedListener = new NumberPicker.OnValueChangeListener() {
        @Override
        public void onValueChange(NumberPicker numberPicker, int oldValue, int newValue) {
            // overflow:
            if (oldValue == 3 && newValue == 0) hourFrom.setValue(hourFrom.getValue() + 1);
            if (oldValue == 0 && newValue == 3) hourFrom.setValue(hourFrom.getValue() - 1);
            // max value:
            if (newValue == 3 && hourFrom.getValue() == 21) minuteFrom.setValue(2);
            synchronizeTimeToWithTimeFrom();
        }
    };

    private NumberPicker.OnValueChangeListener hourToOnValueChangedListener = new NumberPicker.OnValueChangeListener() {
        @Override
        public void onValueChange(NumberPicker numberPicker, int oldValue, int newValue) {
            // 8:30 is minimum value:
            if (newValue == 8 && (minuteTo.getValue() == 0 || minuteTo.getValue() == 1))
                minuteTo.setValue(2);
            // 22:00 is maximum value:
            if (newValue == 22 && minuteTo.getValue() != 0) minuteTo.setValue(0);
            synchronizeTimeFromWithTimeTo();
        }
    };

    private NumberPicker.OnValueChangeListener minuteToOnValueChangedListener = new NumberPicker.OnValueChangeListener() {
        @Override
        public void onValueChange(NumberPicker numberPicker, int oldValue, int newValue) {
            // overflow:
            if (oldValue == 3 && newValue == 0) hourTo.setValue(hourTo.getValue() + 1);
            if (oldValue == 0 && newValue == 3) hourTo.setValue(hourTo.getValue() - 1);
            // 8:30 is minimum value:
            if (hourTo.getValue() == 8 && (newValue == 0 || newValue == 1)) minuteTo.setValue(2);
            // 22:00 is maximum value:
            if (hourTo.getValue() == 22) minuteTo.setValue(0);
            synchronizeTimeFromWithTimeTo();
        }
    };

    private void initializeTime() {
        SimpleDateFormat format = new SimpleDateFormat("HH", Locale.US);
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        String hrFrom = format.format(lesson.getTimeFrom());
        String hrTo = format.format(lesson.getTimeTo());

        format = new SimpleDateFormat("mm", Locale.US);
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        String mntFrom = format.format(lesson.getTimeFrom());
        String mntTo = format.format(lesson.getTimeTo());

        hourFrom.setValue(Integer.parseInt(hrFrom));
        hourTo.setValue(Integer.parseInt(hrTo));
        minuteFrom.setValue(Arrays.binarySearch(MINUTES, mntFrom));
        minuteTo.setValue(Arrays.binarySearch(MINUTES, mntTo));
    }

    private void synchronizeTimeToWithTimeFrom() {
        int lessonLength = (hourTo.getValue() - hourFrom.getValue()) * 4 + minuteTo.getValue() - minuteFrom.getValue();
        if (lessonLength < 2) {
            if (minuteFrom.getValue() < 2) {
                hourTo.setValue(hourFrom.getValue());
                minuteTo.setValue(minuteFrom.getValue() + 2);
            } else {
                hourTo.setValue(hourFrom.getValue() + 1);
                minuteTo.setValue(minuteFrom.getValue() - 2);
            }
        }
    }

    private void synchronizeTimeFromWithTimeTo() {
        int lessonLength = (hourTo.getValue() - hourFrom.getValue()) * 4 + minuteTo.getValue() - minuteFrom.getValue();
        if (lessonLength < 2) {
            if (minuteTo.getValue() >= 2) {
                hourFrom.setValue(hourTo.getValue());
                minuteFrom.setValue(minuteTo.getValue() - 2);
            } else {
                hourFrom.setValue(hourTo.getValue() - 1);
                minuteFrom.setValue(minuteTo.getValue() + 2);
            }
        }
    }

    private void setTime() {
        String timeFromString = hourFrom.getValue() + ":" + MINUTES[minuteFrom.getValue()];
        String timeToString = hourTo.getValue() + ":" + MINUTES[minuteTo.getValue()];

        SimpleDateFormat format = new SimpleDateFormat("HH:mm", Locale.US);
        format.setTimeZone(TimeZone.getTimeZone("UTC"));

        Date dateFrom = null;
        Date dateTo = null;

        try {
            dateFrom = format.parse(timeFromString);
            dateTo = format.parse(timeToString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        lesson.setTimeFrom(dateFrom);
        lesson.setTimeTo(dateTo);
    }

    private void createAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String message;
        if (lesson.getStudent().getFirstName().replaceAll("\\s+", "").length() == 0 &&
                lesson.getStudent().getLastName().replace(" ", "").length() != 0) {
            message = "Delete lesson with " + lesson.getStudent().getLastName() + "?";
        } else if (lesson.getStudent().getFirstName().replace(" ", "").length() != 0 &&
                lesson.getStudent().getLastName().replace(" ", "").length() == 0) {
            message = "Delete lesson with " + lesson.getStudent().getFirstName() + "?";
        } else {
            message = "Delete lesson with " +
                    lesson.getStudent().getFirstName() +
                    " " +
                    lesson.getStudent().getLastName() + "?";
        }
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                delete();
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

    private void delete() {
        progressBar.setVisibility(View.VISIBLE);
        new DeleteLesson(progressBar, this).execute();
    }

    private static class StudentsAdapter extends ArrayAdapter<Student> {

        StudentsAdapter(@NonNull Context context, @NonNull List<Student> studentList) {
            super(context, 0, studentList);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            final Student student = getItem(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.student_item, parent, false);
            }
            TextView name = convertView.findViewById(R.id.name);
            name.setText(new StringBuilder().
                    append(student.getFirstName()).
                    append(convertView.getContext().getString(R.string.space)).
                    append(student.getLastName()).toString());
            return convertView;
        }

        @Override
        public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
            final Student student = getItem(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.student_dropdown_item, parent, false);
            }
            TextView dropDownName = convertView.findViewById(R.id.name);
            dropDownName.setText(new StringBuilder().
                    append(student.getFirstName()).
                    append(convertView.getContext().getString(R.string.space)).
                    append(student.getLastName()).toString());
            return convertView;
        }
    }

    private static class LoadStudents extends AsyncTask<Long, Integer, List<Student>> {
        private final WeakReference<Spinner> studentsWeakReference;
        private final WeakReference<ProgressBar> progressBarWeakReference;
        private final WeakReference<Activity> activityWeakReference;

        LoadStudents(Spinner students, ProgressBar progressBar, Activity activity) {
            this.studentsWeakReference = new WeakReference<>(students);
            this.progressBarWeakReference = new WeakReference<>(progressBar);
            this.activityWeakReference = new WeakReference<>(activity);
        }

        @Override
        protected List<Student> doInBackground(Long... params) {
            /*try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/
            return DB.getStudentDao().findAll();
        }

        @Override
        protected void onPostExecute(List<Student> result) {
            final ProgressBar progressBar = progressBarWeakReference.get();
            if (progressBar != null) progressBar.setVisibility(View.GONE);
            Collections.sort(result);
            studentList = result;
            studentListIsEmpty = studentList.size() == 0;
            adapter.addAll(studentList);
            final Spinner students = studentsWeakReference.get();
            if (students != null) students.setSelection(studentList.indexOf(lesson.getStudent()));
            final Activity activity = activityWeakReference.get();
            if (activity != null) activity.invalidateOptionsMenu();
        }
    }

    private static class UpdateLesson extends AsyncTask<Lesson, Integer, Lesson> {
        private final WeakReference<ProgressBar> progressBarWeakReference;
        private final WeakReference<Context> contextWeakReference;

        UpdateLesson(ProgressBar progressBar, Context context) {
            this.progressBarWeakReference = new WeakReference<>(progressBar);
            this.contextWeakReference = new WeakReference<>(context);
        }

        @Override
        protected Lesson doInBackground(Lesson... params) {
            /*try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/
            DB.getLessonDao().update(lesson);
            return lesson;
        }

        @Override
        protected void onPostExecute(Lesson result) {
            final ProgressBar progressBar = progressBarWeakReference.get();
            if (progressBar != null) progressBar.setVisibility(View.GONE);
            final Context context = contextWeakReference.get();
            if (context != null) {
                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra("WEEKDAY", lesson.getWeekday());
                context.startActivity(intent);
            }
        }
    }

    private static class DeleteLesson extends AsyncTask<Void, Integer, Lesson> {
        private final WeakReference<ProgressBar> progressBarWeakReference;
        private final WeakReference<Context> contextWeakReference;

        DeleteLesson(ProgressBar progressBar, Context context) {
            this.progressBarWeakReference = new WeakReference<>(progressBar);
            this.contextWeakReference = new WeakReference<>(context);
        }

        @Override
        protected Lesson doInBackground(Void... params) {
            /*try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/
            DB.getLessonDao().delete(lesson);
            return lesson;
        }

        @Override
        protected void onPostExecute(Lesson lesson) {
            final ProgressBar progressBar = progressBarWeakReference.get();
            if (progressBar != null) progressBar.setVisibility(View.GONE);
            final Context context = contextWeakReference.get();
            if (context != null) {
                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra("WEEKDAY", lesson.getWeekday());
                context.startActivity(intent);
            }
        }
    }
}