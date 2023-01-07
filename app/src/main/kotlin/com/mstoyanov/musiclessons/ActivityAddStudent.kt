package com.mstoyanov.musiclessons

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.NavUtils
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.mstoyanov.musiclessons.model.PhoneNumber
import com.mstoyanov.musiclessons.model.Student
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ActivityAddStudent : AppCompatActivity() {
    private lateinit var firstName: EditText
    private lateinit var firstNameTextWatcher: TextWatcher
    private lateinit var lastName: EditText
    private lateinit var progressBar: ProgressBar
    private lateinit var adapter: AdapterAddStudent
    private lateinit var student: Student
    var pristine = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_student)

        if (savedInstanceState == null) {
            student = Student()
            student.phoneNumbers.add(PhoneNumber())
        } else {
            student = savedInstanceState.getSerializable("STUDENT") as Student
            pristine = savedInstanceState.get("PRISTINE") as Boolean
        }

        setSupportActionBar(findViewById<View>(R.id.toolbar) as Toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        progressBar = findViewById(R.id.progress_bar)
        progressBar.isIndeterminate = true
        progressBar.visibility = View.GONE

        firstName = findViewById(R.id.first_name)
        firstNameTextWatcher = NameTextWatcher(this)
        firstName.addTextChangedListener(firstNameTextWatcher)

        lastName = findViewById(R.id.last_name)
        lastName.addTextChangedListener(NameTextWatcher(this))

        val recyclerView = findViewById<RecyclerView>(R.id.phone_numbers_list)
        adapter = AdapterAddStudent(student.phoneNumbers)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        val button = findViewById<FloatingActionButton>(R.id.add_phone_number)
        button.setOnClickListener {
            student.phoneNumbers.add(PhoneNumber())
            adapter.notifyDataSetChanged()
        }
    }

    override fun onSaveInstanceState(state: Bundle) {
        super.onSaveInstanceState(state)
        state.putSerializable("STUDENT", student)
        state.putBoolean("PRISTINE", pristine)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_insert_student, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                NavUtils.navigateUpFromSameTask(this)
                true
            }
            R.id.action_insert -> {
                insertStudent()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        if (studentIsValid()) {
            menu.findItem(R.id.action_insert).isEnabled = true
            menu.findItem(R.id.action_insert).icon?.alpha = 255
        } else {
            menu.findItem(R.id.action_insert).isEnabled = false
            menu.findItem(R.id.action_insert).icon?.alpha = 127
        }
        return true
    }

    private fun studentIsValid(): Boolean {
        if (!nameIsValid()) return false
        student.phoneNumbers.map { pn -> if (!pn.isValid) return false }
        return true
    }

    private fun nameIsValid(): Boolean {
        return firstName.text.toString().trim().isNotEmpty() || lastName.text.toString().trim().isNotEmpty()
    }

    private fun insertStudent() {
        student.firstName = firstName.text.toString().trim()
        student.lastName = lastName.text.toString().trim()

        val email = findViewById<EditText>(R.id.email)
        student.email = email.text.toString().trim()

        val notes = findViewById<EditText>(R.id.notes)
        student.notes = notes.text.toString().trim()

        val intent = Intent(this, ActivityMain::class.java)
        progressBar.visibility = View.VISIBLE

        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                // Thread.sleep(1_000)
                MusicLessonsApplication.db.studentDao.insertWithPhoneNumbers(student)

                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE

                    intent.putExtra("ADDED_STUDENT_ID", student.studentId)
                    startActivity(intent)
                }
            }
        }
    }

    fun invokeFirstNameTextWatcher() {
        firstNameTextWatcher.afterTextChanged(firstName.text)
    }

    private inner class NameTextWatcher(private val activity: Activity) : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            // do nothing
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            // do nothing
        }

        override fun afterTextChanged(s: Editable) {
            if (pristine && s.toString().isNotEmpty()) {
                pristine = false
                if (student.phoneNumbers.size > 0) adapter.notifyDataSetChanged()
            }
            if (nameIsValid()) {
                firstName.error = null
            } else {
                if (!pristine) firstName.error = activity.resources.getString(R.string.name_error)
            }
            activity.invalidateOptionsMenu()
        }
    }
}
