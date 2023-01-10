package com.mstoyanov.musiclessons

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.mstoyanov.musiclessons.ActivityStudentDetails.Companion.PERMISSION_REQUEST_CALL_PHONE
import com.mstoyanov.musiclessons.global.Functions.formatter
import com.mstoyanov.musiclessons.global.Functions.serializable
import com.mstoyanov.musiclessons.model.Lesson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ActivityLessonDetails : AppCompatActivity() {
    private lateinit var phoneNumbers: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var lesson: Lesson
    private var number: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lesson_details)

        setSupportActionBar(findViewById<View>(R.id.toolbar) as Toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        progressBar = findViewById(R.id.progress_bar)
        progressBar.isIndeterminate = true
        progressBar.visibility = View.VISIBLE

        phoneNumbers = findViewById(R.id.phone_numbers)
        val layoutManager = LinearLayoutManager(this)
        phoneNumbers.layoutManager = layoutManager
        val divider = DividerItemDecoration(phoneNumbers.context, layoutManager.orientation)
        phoneNumbers.addItemDecoration(divider)

        if (savedInstanceState == null && intent.serializable<Lesson>("LESSON") != null) {
            // coming from AdapterLessons:
            lesson = intent.serializable("LESSON")!!
            findPhoneNumbersByStudentId()
        } else if (savedInstanceState == null && intent.serializable<Lesson>("UPDATED_LESSON") != null) {
            // coming from ActivityEditLesson:
            lesson = intent.serializable("UPDATED_LESSON")!!
            findPhoneNumbersByStudentId()
        } else if (savedInstanceState != null) {
            // after screen rotation:
            progressBar.visibility = View.GONE
            lesson = savedInstanceState.serializable("SAVED_LESSON")!!
            val adapter = AdapterLessonDetails(lesson.student.phoneNumbers, this)
            phoneNumbers.adapter = adapter
        }

        val weekday = findViewById<TextView>(R.id.weekday)
        weekday.text = lesson.weekday.displayValue()

        val time = findViewById<TextView>(R.id.time)
        val timeFrom = formatter.format(lesson.timeFrom)
        val timeTo = formatter.format(lesson.timeTo)
        time.text = StringBuilder().append(timeFrom).append(getString(R.string.dash)).append(timeTo).toString()

        val name = findViewById<TextView>(R.id.name)
        name.text = StringBuilder()
            .append(lesson.student.firstName)
            .append(getString(R.string.space))
            .append(lesson.student.lastName)
            .toString()

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

    override fun onSaveInstanceState(state: Bundle) {
        super.onSaveInstanceState(state)
        state.putSerializable("SAVED_LESSON", lesson)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                val intent = Intent(this, ActivityMain::class.java)
                intent.putExtra("WEEKDAY", lesson.weekday)
                this.startActivity(intent)
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

    private fun findPhoneNumbersByStudentId() {
        val context = this

        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                // Thread.sleep(1_000)
                val phoneNumberList = MusicLessonsApplication.db.phoneNumberDao.findByStudentId(lesson.studentId)

                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE

                    lesson.student.phoneNumbers = phoneNumberList
                    phoneNumbers.adapter = AdapterLessonDetails(phoneNumberList, context)
                }
            }
        }
    }

    fun dial(number: String) {
        this.number = number
        val hasPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
        if (hasPermission != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CALL_PHONE)) {
                showMessageOKCancel { _, _ ->
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.CALL_PHONE),
                        PERMISSION_REQUEST_CALL_PHONE
                    )
                }
                return
            }
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CALL_PHONE), PERMISSION_REQUEST_CALL_PHONE)
            return
        }
        val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$number"))
        startActivity(intent)
    }

    private fun showMessageOKCancel(okListener: DialogInterface.OnClickListener) {
        AlertDialog.Builder(this@ActivityLessonDetails)
            .setMessage("You need to provide CALL_PHONE permission")
            .setPositiveButton("OK", okListener)
            .setNegativeButton("Cancel", null)
            .create()
            .show()
    }
}
