package com.mstoyanov.musiclessons

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.ActivityCompat
import android.support.v4.app.NavUtils
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.mstoyanov.musiclessons.ActivityStudentDetails.Companion.PERMISSION_REQUEST_CALL_PHONE
import com.mstoyanov.musiclessons.model.Lesson
import com.mstoyanov.musiclessons.model.PhoneNumber
import java.lang.ref.WeakReference
import java.text.SimpleDateFormat
import java.util.*

class ActivityLessonDetails : AppCompatActivity() {
    private lateinit var phoneNumbers: RecyclerView
    private lateinit var progressBar: ProgressBar
    private var number: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lesson_details)

        setSupportActionBar(findViewById<View>(R.id.toolbar) as Toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        progressBar = findViewById(R.id.progress_bar)
        progressBar.isIndeterminate = true

        phoneNumbers = findViewById(R.id.phone_numbers)
        val layoutManager = LinearLayoutManager(this)
        phoneNumbers.layoutManager = layoutManager
        val divider = DividerItemDecoration(phoneNumbers.context, layoutManager.orientation)
        phoneNumbers.addItemDecoration(divider)

        if (savedInstanceState == null && intent.getSerializableExtra("LESSON") != null) {
            // coming from AdapterLessons:
            lesson = intent.getSerializableExtra("LESSON") as Lesson
            FindAllPhoneNumbersByStudentId(this).execute()
        } else if (savedInstanceState == null && intent.getSerializableExtra("UPDATED_LESSON") != null) {
            // coming from ActivityEditLesson:
            progressBar.visibility = View.GONE
            lesson = intent.getSerializableExtra("UPDATED_LESSON") as Lesson
            val adapter = AdapterLessonDetails(lesson.student.phoneNumbers, this)
            phoneNumbers.adapter = adapter
        } else if (savedInstanceState != null) {
            // after screen rotation:
            progressBar.visibility = View.GONE
            lesson = savedInstanceState.getSerializable("LESSON") as Lesson
            val adapter = AdapterLessonDetails(lesson.student.phoneNumbers, this)
            phoneNumbers.adapter = adapter
        }

        val weekday = findViewById<TextView>(R.id.weekday)
        weekday.text = lesson.weekday.displayValue()

        val time = findViewById<TextView>(R.id.time)
        val format = SimpleDateFormat("HH:mm", Locale.US)
        format.timeZone = TimeZone.getTimeZone("UTC")
        val timeFrom = format.format(lesson.timeFrom)
        val timeTo = format.format(lesson.timeTo)
        time.text = StringBuilder().append(timeFrom).append(getString(R.string.dash)).append(timeTo).toString()

        val name = findViewById<TextView>(R.id.name)
        name.text = StringBuilder().append(lesson.student.firstName).append(getString(R.string.space)).append(lesson.student.lastName).toString()

        val email = findViewById<TextView>(R.id.email)
        if (lesson.student.email.isNotEmpty()) {
            email.text = lesson.student.email
            email.setOnClickListener {
                val intent = Intent(Intent.ACTION_SENDTO)
                intent.data = Uri.parse("mailto:" + email.text.toString())
                intent.putExtra(Intent.EXTRA_SUBJECT, "Music Lessons")
                if (intent.resolveActivity(packageManager) != null) {
                    startActivity(intent)
                }
            }
        } else {
            email.visibility = View.GONE
        }

        val notes = findViewById<TextView>(R.id.notes)
        if (lesson.student.notes.isNotEmpty()) {
            notes.text = lesson.student.notes
        } else {
            notes.visibility = View.GONE
        }

        val edit = findViewById<FloatingActionButton>(R.id.edit)
        edit.setOnClickListener {
            val intent = Intent(this@ActivityLessonDetails, ActivityEditLesson::class.java)
            intent.putExtra("LESSON", lesson)
            startActivity(intent)
        }
    }

    override fun onSaveInstanceState(state: Bundle?) {
        super.onSaveInstanceState(state)
        state!!.putSerializable("LESSON", lesson)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                NavUtils.navigateUpFromSameTask(this)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_REQUEST_CALL_PHONE -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dial(number)
            } else {
                Toast.makeText(this@ActivityLessonDetails, "Permission CALL_PHONE denied", Toast.LENGTH_SHORT).show()
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    fun dial(number: String) {
        this.number = number
        val hasPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
        if (hasPermission != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CALL_PHONE)) {
                showMessageOKCancel("You need to provide CALL_PHONE permission",
                        DialogInterface.OnClickListener { dialog, which -> ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CALL_PHONE), PERMISSION_REQUEST_CALL_PHONE) })
                return
            }
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CALL_PHONE), PERMISSION_REQUEST_CALL_PHONE)
            return
        }
        val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number))
        startActivity(intent)
    }

    private fun showMessageOKCancel(message: String, okListener: DialogInterface.OnClickListener) {
        AlertDialog.Builder(this@ActivityLessonDetails)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show()
    }

    companion object {
        private lateinit var lesson: Lesson

        private class FindAllPhoneNumbersByStudentId(context: ActivityLessonDetails) : AsyncTask<Long, Int, MutableList<PhoneNumber>>() {
            private val lessonDetailsActivityWeakReference: WeakReference<ActivityLessonDetails> = WeakReference(context)

            override fun doInBackground(vararg p0: Long?): MutableList<PhoneNumber> {
                // Thread.sleep(1000)
                return MusicLessonsApplication.db.phoneNumberDao.findAllByStudentId(lesson.studentId)
            }

            override fun onPostExecute(result: MutableList<PhoneNumber>) {
                val lessonDetailsActivity: ActivityLessonDetails = lessonDetailsActivityWeakReference.get()!!

                lessonDetailsActivity.progressBar.visibility = View.GONE

                lesson.student.phoneNumbers = result
                val adapter = AdapterLessonDetails(lesson.student.phoneNumbers, lessonDetailsActivity)
                lessonDetailsActivity.phoneNumbers.adapter = adapter
            }
        }
    }
}