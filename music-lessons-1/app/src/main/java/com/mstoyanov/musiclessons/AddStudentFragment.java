package com.mstoyanov.musiclessons;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.mstoyanov.musiclessons.data.SchoolContract;

public class AddStudentFragment extends Fragment {
    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText homePhoneEditText;
    private EditText cellPhoneEditText;
    private EditText workPhoneEditText;
    private EditText emailEditText;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.add_student_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);

        firstNameEditText = getView().findViewById(R.id.first_name);
        lastNameEditText = getView().findViewById(R.id.last_name);
        homePhoneEditText = getView().findViewById(R.id.home_phone);
        homePhoneEditText.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        cellPhoneEditText = getView().findViewById(R.id.cell_phone);
        cellPhoneEditText.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        workPhoneEditText = getView().findViewById(R.id.work_phone);
        workPhoneEditText.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        emailEditText = getView().findViewById(R.id.email);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.add_student_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_save_student:
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
                if (getActivity().getContentResolver().insert(SchoolContract.STUDENTS_TABLE_CONTENTURI, values) != null) {
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    intent.putExtra("SELECTED_TAB", 1);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    return true;
                } else {
                    return false;
                }
            default:
                throw new IllegalArgumentException("Invalid itemId: " + item.getItemId());
        }
    }

    public boolean validateStudent() {

        if (firstNameEditText.getText().toString().trim().length() == 0 &&
                lastNameEditText.getText().toString().trim().length() == 0) {
            firstNameEditText.setError("A name is required");
            return false;
        } else {
            firstNameEditText.setError(null);
        }
        return true;
    }
}