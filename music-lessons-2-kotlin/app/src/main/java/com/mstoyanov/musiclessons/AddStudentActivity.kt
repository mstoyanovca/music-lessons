package com.mstoyanov.musiclessons

import android.app.Activity
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.NavUtils
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import com.mstoyanov.musiclessons.model.PhoneNumber
import com.mstoyanov.musiclessons.model.PhoneNumberType
import com.mstoyanov.musiclessons.model.Student
import java.lang.ref.WeakReference

class AddStudentActivity : AppCompatActivity() {
    private lateinit var firstName: EditText
    private lateinit var firstNameTextWatcher: TextWatcher
    private lateinit var lastName: EditText
    private lateinit var progressBar: ProgressBar
    private lateinit var adapter: AddStudentAdapter
    private var student = Student()
    var pristine = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_student)

        if (savedInstanceState == null) {
            val phoneNumbers = mutableListOf<PhoneNumber>()
            phoneNumbers.add(PhoneNumber("", PhoneNumberType.HOME))
            student.phoneNumbers = phoneNumbers
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
        adapter = AddStudentAdapter(student.phoneNumbers!!)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        val button = findViewById<FloatingActionButton>(R.id.add_phone_number)
        button.setOnClickListener {
            student.phoneNumbers!!.add(PhoneNumber("", PhoneNumberType.HOME))
            adapter.notifyDataSetChanged()
        }
    }

    override fun onSaveInstanceState(state: Bundle?) {
        super.onSaveInstanceState(state)
        state!!.putSerializable("STUDENT", student)
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
            menu.findItem(R.id.action_insert).icon.alpha = 255
        } else {
            menu.findItem(R.id.action_insert).isEnabled = false
            menu.findItem(R.id.action_insert).icon.alpha = 127
        }
        return true
    }

    private fun studentIsValid(): Boolean {
        if (!nameIsValid()) return false
        student.phoneNumbers!!.map { phoneNumber -> if (!phoneNumber.isValid) return false }
        return true
    }

    private fun nameIsValid(): Boolean {
        return !firstName.text.toString().replace("\\s+".toRegex(), "").isEmpty() || !lastName.text.toString().replace("\\s+".toRegex(), "").isEmpty()
    }

    private fun insertStudent() {
        student.firstName = stripString(firstName.text.toString())
        student.lastName = stripString(lastName.text.toString())

        student.phoneNumbers = adapter.phoneNumbers

        val email = findViewById<EditText>(R.id.email)
        student.email = stripString(email.text.toString())

        val notes = findViewById<EditText>(R.id.notes)
        student.notes = stripString(notes.text.toString())

        progressBar.visibility = View.VISIBLE
        AddStudent(this).execute(student)
    }

    private fun stripString(string: String): String {
        return if (string.replace("\\s+".toRegex(), "").isNotEmpty()) {
            string.trim { it <= ' ' }
        } else {
            ""
        }
    }

    fun invokeTextChanged() {
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
            if (pristine && s.isNotEmpty()) {
                pristine = false
                adapter.notifyDataSetChanged()
            }
            if (nameIsValid()) {
                firstName.error = null
            } else {
                if (!pristine)
                    firstName.error = activity.resources.getString(R.string.name_error)
            }
            activity.invalidateOptionsMenu()
        }
    }

    companion object {

        private class AddStudent(context: AddStudentActivity) : AsyncTask<Student, Int, Student>() {
            private val addStudentActivityWeakReference: WeakReference<AddStudentActivity> = WeakReference(context)

            override fun doInBackground(vararg params: Student): Student {
                // Thread.sleep(1000)
                val addStudentActivity = addStudentActivityWeakReference.get()!!
                val student = addStudentActivity.student

                val id = MusicLessonsApplication.db.studentDao.insert(student)
                student.studentId = id

                student.phoneNumbers!!.map { phoneNumber -> phoneNumber.studentId = id }
                MusicLessonsApplication.db.phoneNumberDao.insertAll(student.phoneNumbers!!)
                return student
            }

            override fun onPostExecute(result: Student) {
                val addStudentActivity = addStudentActivityWeakReference.get()!!
                addStudentActivity.progressBar.visibility = View.GONE

                val intent = Intent(addStudentActivity, MainActivity::class.java)
                intent.putExtra("ADDED_STUDENT_ID", result.studentId)
                addStudentActivity.startActivity(intent)
            }
        }
    }
}