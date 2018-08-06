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
import com.mstoyanov.musiclessons.model.PhoneNumberType
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_student)

        student = intent.getSerializableExtra("STUDENT") as Student

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
        adapter = AdapterEditStudent(student.phoneNumbers!!)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        val button = findViewById<FloatingActionButton>(R.id.add_phone_number)
        button.setOnClickListener {
            student.phoneNumbers += PhoneNumber("", PhoneNumberType.HOME)
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

    fun stopProgressBar() {
        progressBar.visibility = View.GONE
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
        student.firstName = stripString(firstName.text.toString())
        student.lastName = stripString(lastName.text.toString())
        student.phoneNumbers = adapter.phoneNumbers
        student.email = stripString(email.text.toString())
        student.notes = stripString(notes.text.toString())

        progressBar.visibility = View.VISIBLE
        UpdateStudent(this).execute()
    }

    private fun createAlertDialog() {
        val builder = AlertDialog.Builder(this)
        val message: String
        if (!(student.firstName!!.replace("\\s+".toRegex(), "").isNotEmpty() || !student.lastName!!.replace(" ", "").isNotEmpty())) {
            message = "Delete student " + student.lastName + "?"
        } else if (!(!student.firstName!!.replace(" ", "").isNotEmpty() || student.lastName!!.replace(" ", "").isNotEmpty())) {
            message = "Delete student " + student.firstName + "?"
        } else {
            message = "Delete student " + student.firstName + " " + student.lastName + "?"
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
        student.phoneNumbers!!.map { phoneNumber -> if (!phoneNumber.isValid) return false }
        return true
    }

    private fun nameIsValid(): Boolean {
        return !firstName.text.toString().replace("\\s+".toRegex(), "").isEmpty() || !lastName.text.toString().replace("\\s+".toRegex(), "").isEmpty()
    }

    private fun stripString(string: String): String {
        return if (string.replace("\\s+".toRegex(), "").isNotEmpty()) {
            string.trim { it <= ' ' }
        } else {
            ""
        }
    }

    companion object {

        private class UpdateStudent(context: ActivityEditStudent) : AsyncTask<Student, Int, Student>() {
            private val editStudentActivityWeakReference: WeakReference<ActivityEditStudent> = WeakReference(context)

            override fun doInBackground(vararg params: Student): Student {
                /*try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }*/
                val editStudentActivity = editStudentActivityWeakReference.get()!!
                val student = editStudentActivity.student

                MusicLessonsApplication.db.studentDao.update(student)

                student.phoneNumbers!!.map { phoneNumber -> phoneNumber.studentId = student.studentId }
                MusicLessonsApplication.db.phoneNumberDao.insertAll(student.phoneNumbers!!)
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
                /*try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }*/
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