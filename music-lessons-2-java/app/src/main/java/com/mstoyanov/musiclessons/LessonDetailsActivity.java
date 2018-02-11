package com.mstoyanov.musiclessons;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mstoyanov.musiclessons.model.Lesson;
import com.mstoyanov.musiclessons.model.PhoneNumber;
import com.mstoyanov.musiclessons.repository.AppDatabase;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import static com.mstoyanov.musiclessons.StudentDetailsActivity.PERMISSION_REQUEST_CALL_PHONE;

public class LessonDetailsActivity extends AppCompatActivity {
    private ProgressBar progressBar;
    private RecyclerView phoneNumbers;
    private static Lesson lesson;
    private String number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson_details);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressBar = findViewById(R.id.progress_bar);
        progressBar.setIndeterminate(true);

        phoneNumbers = findViewById(R.id.phone_numbers);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        phoneNumbers.setLayoutManager(layoutManager);
        final DividerItemDecoration divider = new DividerItemDecoration(phoneNumbers.getContext(), layoutManager.getOrientation());
        phoneNumbers.addItemDecoration(divider);

        if (savedInstanceState == null && getIntent().getSerializableExtra("LESSON") != null) {
            // coming from LessonsAdapter:
            lesson = (Lesson) getIntent().getSerializableExtra("LESSON");
            new FindAllPhoneNumbersByStudentId(lesson.getStudentId(), progressBar, phoneNumbers, this).execute();
        } else if (savedInstanceState == null && getIntent().getSerializableExtra("UPDATED_LESSON") != null) {
            // coming from EditLessonActivity:
            progressBar.setVisibility(View.GONE);
            lesson = (Lesson) getIntent().getSerializableExtra("UPDATED_LESSON");
            final LessonDetailsAdapter adapter = new LessonDetailsAdapter(lesson.getStudent().getPhoneNumbers(), this);
            phoneNumbers.setAdapter(adapter);
        } else if (savedInstanceState != null) {
            // after screen rotation:
            progressBar.setVisibility(View.GONE);
            lesson = (Lesson) savedInstanceState.getSerializable("LESSON");
            final LessonDetailsAdapter adapter = new LessonDetailsAdapter(lesson.getStudent().getPhoneNumbers(), this);
            phoneNumbers.setAdapter(adapter);
        }

        final TextView weekday = findViewById(R.id.weekday);
        weekday.setText(lesson.getWeekday().displayValue());

        final TextView time = findViewById(R.id.time);
        SimpleDateFormat format = new SimpleDateFormat("HH:mm", Locale.US);
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        String timeFrom = format.format(lesson.getTimeFrom());
        String timeTo = format.format(lesson.getTimeTo());
        time.setText(new StringBuilder().append(timeFrom).append(getString(R.string.space)).append(timeTo).toString());

        final TextView name = findViewById(R.id.name);
        name.setText(new StringBuilder().
                append(lesson.getStudent().getFirstName()).
                append(getString(R.string.space)).
                append(lesson.getStudent().getLastName()).toString());

        final TextView email = findViewById(R.id.email);
        if (lesson.getStudent().getEmail().length() > 0) {
            email.setText(lesson.getStudent().getEmail());
            email.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_SENDTO);
                    intent.setData(Uri.parse("mailto:" + email.getText().toString()));
                    intent.putExtra(Intent.EXTRA_SUBJECT, "Music Lessons");
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivity(intent);
                    }
                }
            });
        } else {
            email.setVisibility(View.GONE);
        }

        final TextView notes = findViewById(R.id.notes);
        if (lesson.getStudent().getNotes().length() > 0) {
            notes.setText(lesson.getStudent().getNotes());
        } else {
            notes.setVisibility(View.GONE);
        }

        final FloatingActionButton edit = findViewById(R.id.edit);
        edit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(LessonDetailsActivity.this, EditLessonActivity.class);
                intent.putExtra("LESSON", lesson);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        state.putSerializable("LESSON", lesson);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CALL_PHONE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    dial(number);
                } else {
                    Toast.makeText(LessonDetailsActivity.this, "Permission CALL_PHONE denied", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void dial(String number) {
        this.number = number;
        final Activity activity = this;
        int hasPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE);
        if (hasPermission != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WRITE_CONTACTS)) {
                showMessageOKCancel("You need to provide CALL_PHONE permission",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CALL_PHONE}, PERMISSION_REQUEST_CALL_PHONE);
                            }
                        });
                return;
            }
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, PERMISSION_REQUEST_CALL_PHONE);
            return;
        }
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
        startActivity(intent);
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(LessonDetailsActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    private static class FindAllPhoneNumbersByStudentId extends AsyncTask<Long, Integer, List<PhoneNumber>> {
        private final long studentId;
        private final WeakReference<ProgressBar> progressBarWeakReference;
        private final WeakReference<RecyclerView> phoneNumberWeakReference;
        private final WeakReference<Context> contextWeakReference;
        private static final AppDatabase DB = MusicLessonsApplication.getDB();

        FindAllPhoneNumbersByStudentId(
                long studentId,
                ProgressBar progressBar,
                RecyclerView phoneNumbers,
                Context context) {

            this.studentId = studentId;
            this.progressBarWeakReference = new WeakReference<>(progressBar);
            this.phoneNumberWeakReference = new WeakReference<>(phoneNumbers);
            this.contextWeakReference = new WeakReference<>(context);
        }

        @Override
        protected List<PhoneNumber> doInBackground(Long... params) {
            /*try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/
            return DB.getPhoneNumberDao().findAllByStudentId(studentId);
        }

        @Override
        protected void onPostExecute(List<PhoneNumber> result) {
            final ProgressBar progressBar = progressBarWeakReference.get();
            if (progressBar != null) progressBar.setVisibility(View.GONE);
            lesson.getStudent().setPhoneNumbers(result);
            final Context context = contextWeakReference.get();
            final RecyclerView phoneNumbers = phoneNumberWeakReference.get();
            if (context != null && phoneNumbers != null) {
                final LessonDetailsAdapter adapter = new LessonDetailsAdapter(lesson.getStudent().getPhoneNumbers(), context);
                phoneNumbers.setAdapter(adapter);
            }
        }
    }
}