package com.mstoyanov.musiclessons

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.app.NavUtils
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.mstoyanov.musiclessons.global.Functions.serializable
import com.mstoyanov.musiclessons.model.Student

class ActivityStudentDetails : AppCompatActivity() {
    private lateinit var student: Student
    private var number: String = ""
    private var updatedStudentId: Long = 0L

    companion object {
        const val PERMISSION_REQUEST_CALL_PHONE = 123
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_details)

        if (intent.serializable<Student>("STUDENT") != null) {
            // coming from AdapterStudents:
            student = intent.serializable("STUDENT")!!
            updatedStudentId = 0L
        } else if (intent.serializable<Student>("UPDATED_STUDENT") != null) {
            // coming from ActivityEditStudent:
            student = intent.serializable("UPDATED_STUDENT")!!
            updatedStudentId = student.studentId
        }

        setSupportActionBar(findViewById<View>(R.id.toolbar) as Toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val name = findViewById<TextView>(R.id.name)
        name.text = getString(R.string.full_name, student.firstName, student.lastName)

        val phoneNumbers = findViewById<RecyclerView>(R.id.phone_numbers_list)
        phoneNumbers.layoutManager = LinearLayoutManager(this)
        val adapter = AdapterStudentDetails(student.phoneNumbers, this)
        phoneNumbers.adapter = adapter
        val divider = DividerItemDecoration(phoneNumbers.context, LinearLayoutManager(this).orientation)
        phoneNumbers.addItemDecoration(divider)

        val email = findViewById<TextView>(R.id.email)
        if (student.email.isNotEmpty()) {
            email.text = student.email
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
        if (student.notes.isNotEmpty()) {
            notes.text = student.notes
        } else {
            notes.visibility = View.GONE
        }

        val edit = findViewById<FloatingActionButton>(R.id.edit)
        edit.setOnClickListener {
            val intent = Intent(this@ActivityStudentDetails, ActivityEditStudent::class.java)
            intent.putExtra("STUDENT", student)
            startActivity(intent)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                if (updatedStudentId == 0L) {
                    NavUtils.navigateUpFromSameTask(this)
                } else {
                    val intent = Intent(this@ActivityStudentDetails, ActivityMain::class.java)
                    intent.putExtra("UPDATED_STUDENT_ID", updatedStudentId)
                    startActivity(intent)
                }
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
                Toast.makeText(this, "Permission CALL_PHONE denied.", Toast.LENGTH_SHORT).show()
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
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
        AlertDialog.Builder(this)
            .setMessage("You need to provide CALL_PHONE permission.")
            .setPositiveButton("OK", okListener)
            .setNegativeButton("Cancel", null)
            .create()
            .show()
    }
}
