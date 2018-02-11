package com.mstoyanov.musiclessons;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
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
import android.widget.TextView;
import android.widget.Toast;

import com.mstoyanov.musiclessons.model.Student;

public class StudentDetailsActivity extends AppCompatActivity {
    private Student student;
    private String number;
    private long updatedStudentId;
    public static final int PERMISSION_REQUEST_CALL_PHONE = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_details);

        if (getIntent().getSerializableExtra("STUDENT") != null) {
            // coming from StudentsAdapter:
            student = (Student) getIntent().getSerializableExtra("STUDENT");
        } else if (getIntent().getSerializableExtra("UPDATED_STUDENT") != null) {
            // coming from EditStudentActivity:
            student = (Student) getIntent().getSerializableExtra("UPDATED_STUDENT");
            updatedStudentId = student.getStudentId();
        }

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final TextView name = findViewById(R.id.name);
        name.setText(student.getFirstName() + " " + student.getLastName());

        final RecyclerView phoneNumbers = findViewById(R.id.phone_numbers_list);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        phoneNumbers.setLayoutManager(new LinearLayoutManager(this));
        final StudentDetailsAdapter adapter = new StudentDetailsAdapter(student.getPhoneNumbers(), this);
        phoneNumbers.setAdapter(adapter);
        final DividerItemDecoration divider = new DividerItemDecoration(phoneNumbers.getContext(), layoutManager.getOrientation());
        phoneNumbers.addItemDecoration(divider);

        final TextView email = findViewById(R.id.email);
        if (student.getEmail().length() > 0) {
            email.setText(student.getEmail());
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
        if (student.getNotes().length() > 0) {
            notes.setText(student.getNotes());
        } else {
            notes.setVisibility(View.GONE);
        }

        final FloatingActionButton edit = findViewById(R.id.edit);
        edit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(StudentDetailsActivity.this, EditStudentActivity.class);
                intent.putExtra("STUDENT", student);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (updatedStudentId == 0) {
                    NavUtils.navigateUpFromSameTask(this);
                } else {
                    Intent intent = new Intent(StudentDetailsActivity.this, MainActivity.class);
                    intent.putExtra("UPDATED_STUDENT_ID", updatedStudentId);
                    startActivity(intent);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CALL_PHONE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    dial(number);
                } else {
                    Toast.makeText(StudentDetailsActivity.this, "Permission CALL_PHONE denied", Toast.LENGTH_SHORT).show();
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
        new AlertDialog.Builder(StudentDetailsActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }
}