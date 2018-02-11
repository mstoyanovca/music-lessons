package com.mstoyanov.musiclessons;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

/*	This activity allows for entering teacher's name,
 * 	to appear on the exported PDF table, as well as
 * 	user name and password for future data synchronization
 * 	with a web site.	*/

public class SettingsActivity extends Activity {

    private String firstName;
    private String lastName;
    public static final String SETTINGS = "Settings";
    private EditText firstNameEditText;
    private EditText lastNameEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setTitle("Settings");

        // This class uses shared preferences for storing teacher's name,
        // ID and PW for login and cloud synchronization.
        // Restore the preferences:
        SharedPreferences settings = getSharedPreferences(SETTINGS, 0);
        firstName = settings.getString("FIRST_NAME", firstName);
        lastName = settings.getString("LAST_NAME", lastName);

        firstNameEditText = findViewById(R.id.first_name);
        firstNameEditText.setText(firstName);
        lastNameEditText = findViewById(R.id.last_name);
        lastNameEditText.setText(lastName);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            case R.id.menu_save_settings:
                firstName = firstNameEditText.getText().toString();
                lastName = lastNameEditText.getText().toString();

                SharedPreferences settings = getSharedPreferences(SETTINGS, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("FIRST_NAME", firstName);
                editor.putString("LAST_NAME", lastName);
                editor.commit();

                intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            default:
                throw new IllegalArgumentException("Invalid ItemId: "
                        + item.getItemId());
        }
    }
}