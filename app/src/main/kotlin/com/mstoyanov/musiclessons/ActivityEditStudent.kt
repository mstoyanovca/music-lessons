package com.mstoyanov.musiclessons

import android.app.Activity
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.NavUtils
import android.support.v7.app.AlertDialog
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
import com.mstoyanov.musiclessons.model.Student
import java.lang.ref.WeakReference

class ActivityEditStudent : AppCompatActivity() {
    private lateinit var firstName: EditText
    private lateinit var lastName: EditText
    private lateinit var email: EditText
    private lateinit var notes: EditText
    private lateinit var progressBar: ProgressBar
    private lateinit var adapter: AdapterEditStudent
    private lateinit var student: Student
    private lateinit var phoneNumbersBeforeEditing: List<PhoneNumber>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_student)

        student = intent.getSerializableExtra("STUDENT") as Student
        phoneNumbersBeforeEditing = student.phoneNumbers.toList()

        setSupportActionBar(findViewById<View>(R.id.toolbar) as Toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        firstName = findViewById(R.id.first_name)
        firstName.setText(student.firstName)
        firstName.addTextChangedListener(EditStudentTextWatcher(this))

        lastName = findViewById(R.id.last_name)
        lastName.setText(student.lastName)
        lastName.addTextChangedListener(EditStudentTextWatcher(this))

        email = findViewById(R.id.email)
        email.setText(student.email)

        notes = findViewById(R.id.notes)
        notes.setText(student.notes)

        progressBar = findViewById(R.id.progress_bar)
        progressBar.isIndeterminate = true
        progressBar.visibility = View.GONE

        val recyclerView = findViewById<RecyclerView>(R.id.phone_numbers_list)
        adapter = AdapterEditStudent(student.phoneNumbers)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        val button = findViewById<FloatingActionButton>(R.id.add_phone_number)
        button.setOnClickListener {
            student.phoneNumbers.add(PhoneNumber().copy(studentId = student.studentId))
            adapter.notifyDataSetChanged()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_edit_student, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                NavUtils.navigateUpFromSameTask(this)
                true
            }
            R.id.action_update -> {
                updateStudent()
                true
            }
            R.id.action_delete -> {
                createAlertDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        if (studentIsValid()) {
            menu.findItem(R.id.action_update).isEnabled = true
            menu.findItem(R.id.action_update).icon.alpha = 255
        } else {
            menu.findItem(R.id.action_update).isEnabled = false
            menu.findItem(R.id.action_update).icon.alpha = 127
        }
        return true
    }

    private inner class EditStudentTextWatcher(private val activity: Activity) : TextWatcher {

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            // do nothing
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            // do nothing
        }

        override fun afterTextChanged(s: Editable) {
            if (nameIsValid()) {
                firstName.error = null
            } else {
                firstName.error = activity.resources.getString(R.string.name_error)
            }
            activity.invalidateOptionsMenu()
        }
    }

    private fun updateStudent() {
        student.firstName = firstName.text.toString().trim()
        student.lastName = lastName.text.toString().trim()
        student.email = email.text.toString().trim()
        student.notes = notes.text.toString().trim()

        progressBar.visibility = View.VISIBLE
        UpdateStudent(this).execute()
    }

    private fun createAlertDialog() {
        val builder = AlertDialog.Builder(this)
        val message: String = if (student.firstName.trim().isNotEmpty() && student.lastName.trim().isEmpty()) {
            "Delete student " + student.firstName.trim() + "?"
        } else if (student.firstName.trim().isEmpty() && student.lastName.trim().isNotEmpty()) {
            "Delete student " + student.lastName.trim() + "?"
        } else {
            "Delete student " + getString(R.string.full_name, student.firstName.trim(), student.lastName.trim()) + "?"
        }
        builder.setMessage(message)
        builder.setPositiveButton("OK") { dialogInterface, i -> deleteStudent() }
        builder.setNegativeButton("Cancel") { dialogInterface, i ->
            // do nothing
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun deleteStudent() {
        progressBar.visibility = View.VISIBLE
        DeleteStudent(this).execute()
    }

    private fun studentIsValid(): Boolean {
        if (!nameIsValid()) return false
        student.phoneNumbers.map { pn -> if (!pn.isValid) return false }
        return true
    }

    private fun nameIsValid(): Boolean {
        return firstName.text.toString().trim().isNotEmpty() || lastName.text.toString().trim().isNotEmpty()
    }

    companion object {

        private class UpdateStudent(context: ActivityEditStudent) : AsyncTask<Student, Int, Student>() {
            private val editStudentActivityWeakReference: WeakReference<ActivityEditStudent> = WeakReference(context)

            override fun doInBackground(vararg params: Student): Student {
                // Thread.sleep(1000)
                val editStudentActivity = editStudentActivityWeakReference.get()!!

                val student = editStudentActivity.student
                MusicLessonsApplication.db.studentDao.update(student)

                editStudentActivity.phoneNumbersBeforeEditing.filterNot { student.phoneNumbers.contains(it) }.map { MusicLessonsApplication.db.phoneNumberDao.delete(it) }
                student.phoneNumbers.map { it.phoneNumberId = MusicLessonsApplication.db.phoneNumberDao.insert(it) }

                return student
            }

            override fun onPostExecute(result: Student) {
                val editStudentActivity: ActivityEditStudent = editStudentActivityWeakReference.get()!!
                editStudentActivity.progressBar.visibility = View.GONE

                val intent = Intent(editStudentActivity, ActivityStudentDetails::class.java)
                intent.putExtra("UPDATED_STUDENT", result)
                editStudentActivity.startActivity(intent)
            }
        }

        private class DeleteStudent(context: ActivityEditStudent) : AsyncTask<Void, Int, Student>() {
            private val editStudentActivityWeakReference: WeakReference<ActivityEditStudent> = WeakReference(context)

            override fun doInBackground(vararg params: Void): Student {
                // Thread.sleep(1000)
                val editStudentActivity = editStudentActivityWeakReference.get()!!
                val student = editStudentActivity.student

                MusicLessonsApplication.db.studentDao.delete(student)
                return student
            }

            override fun onPostExecute(student: Student) {
                val editStudentActivity: ActivityEditStudent = editStudentActivityWeakReference.get()!!
                editStudentActivity.progressBar.visibility = View.GONE

                val intent = Intent(editStudentActivity, ActivityMain::class.java)
                intent.putExtra("DELETED_STUDENT_ID", student.studentId)
                editStudentActivity.startActivity(intent)
            }
        }
    }
}