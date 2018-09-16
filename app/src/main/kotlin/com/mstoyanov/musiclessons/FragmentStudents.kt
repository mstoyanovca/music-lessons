package com.mstoyanov.musiclessons

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.os.Bundle
import android.os.Environment
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.mstoyanov.musiclessons.model.Student
import com.mstoyanov.musiclessons.model.StudentWithPhoneNumbers
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.Serializable
import java.lang.ref.WeakReference

class FragmentStudents : Fragment() {
    private lateinit var progressBar: ProgressBar
    private lateinit var adapter: AdapterStudents
    private lateinit var students: MutableList<Student>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_students, container, false)
        setHasOptionsMenu(true)

        val title = rootView.findViewById<TextView>(R.id.heading)
        title.setText(R.string.students_label)

        val recyclerView = rootView.findViewById<RecyclerView>(R.id.students_list)
        students = mutableListOf()
        adapter = AdapterStudents(students, this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(activity)

        progressBar = rootView.findViewById(R.id.progress_bar)
        progressBar.isIndeterminate = true

        if (savedInstanceState == null) {
            LoadStudents(this).execute()
        } else {
            progressBar.visibility = View.GONE
            students.addAll(savedInstanceState.getSerializable("STUDENTS") as MutableList<Student>)
            adapter.notifyDataSetChanged()
            activity!!.invalidateOptionsMenu()
        }

        val button: FloatingActionButton = rootView.findViewById(R.id.add_student)
        button.setOnClickListener { startActivity(Intent(activity, ActivityAddStudent::class.java)) }

        return rootView
    }

    override fun onSaveInstanceState(state: Bundle) {
        super.onSaveInstanceState(state)
        state.putSerializable("STUDENTS", students as Serializable)
    }

    override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.menu_export_students, menu)
        super.onCreateOptionsMenu(menu, menuInflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_export_students -> {
                exportStudents()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        if (students.isEmpty()) {
            menu.findItem(R.id.action_export_students).isEnabled = false
            menu.findItem(R.id.action_export_students).icon.alpha = 127
        } else {
            menu.findItem(R.id.action_export_students).isEnabled = true
            menu.findItem(R.id.action_export_students).icon.alpha = 255
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                exportStudents()
            } else {
                Toast.makeText(this.context, "Permission WRITE_EXTERNAL_STORAGE denied.", Toast.LENGTH_SHORT).show()
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    fun startProgressBar() {
        progressBar.visibility = View.VISIBLE
    }

    fun stopProgressBar() {
        progressBar.visibility = View.GONE
    }

    private fun exportStudents() {
        val hasPermission = ContextCompat.checkSelfPermission(this.context!!, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (hasPermission != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this.activity!!, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                showMessageOKCancel("You need to provide WRITE_EXTERNAL_STORAGE permission.",
                        DialogInterface.OnClickListener { dialog,
                                                          which ->
                            ActivityCompat.requestPermissions(this.activity!!, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE)
                        })
                return
            }
            ActivityCompat.requestPermissions(this.activity!!, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE)
            return
        }
        ExportStudents(this).execute()
    }

    private fun showMessageOKCancel(message: String, okListener: DialogInterface.OnClickListener) {
        AlertDialog.Builder(this.context!!)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show()
    }

    companion object {
        const val PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE = 456

        fun create(position: Int): FragmentStudents {
            val fragment = FragmentStudents()
            val args = Bundle()
            args.putInt("POSITION", position)
            fragment.arguments = args
            return fragment
        }

        private class LoadStudents(context: FragmentStudents) : AsyncTask<Long, Int, MutableList<Student>>() {
            private val studentsFragmentWeakReference = WeakReference<FragmentStudents>(context)

            override fun doInBackground(vararg p0: Long?): MutableList<Student> {
                // Thread.sleep(1000)
                return MusicLessonsApplication.db.studentDao.findAll()
            }

            override fun onPostExecute(result: MutableList<Student>) {
                val studentsFragment = studentsFragmentWeakReference.get()!!
                studentsFragment.progressBar.visibility = View.GONE
                result.sort()
                studentsFragment.students.addAll(result)
                studentsFragment.adapter.notifyDataSetChanged()
                studentsFragment.activity!!.invalidateOptionsMenu()
            }
        }

        class ExportStudents(context: FragmentStudents) : AsyncTask<Long, Int, List<StudentWithPhoneNumbers>>() {
            private val studentsFragmentWeakReference = WeakReference<FragmentStudents>(context)

            override fun doInBackground(vararg p0: Long?): List<StudentWithPhoneNumbers> {
                // Thread.sleep(1000)
                return MusicLessonsApplication.db.studentDao.findAllWithPhoneNumbers()
            }

            override fun onPostExecute(result: List<StudentWithPhoneNumbers>) {
                val studentsFragment = studentsFragmentWeakReference.get()!!
                studentsFragment.progressBar.visibility = View.GONE

                result.forEach { it.student.phoneNumbers = it.phoneNumbers.toMutableList() }
                val students: List<Student> = result.map { it.student }.sorted()

                if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
                    val folder = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "student_lists")
                    if (!folder.exists() && !folder.mkdirs()) Toast.makeText(studentsFragment.activity, "Students lists folder could not be created.", Toast.LENGTH_SHORT).show()
                    val file = File(folder, "student_list_" + System.currentTimeMillis().toString() + ".txt")
                    BufferedWriter(FileWriter(file)).use { w ->
                        students.map { s ->
                            w.write(s.firstName + " " + s.lastName)
                            w.newLine()
                            s.phoneNumbers.map { pn ->
                                w.write(pn.number + " " + pn.type.displayValue())
                                w.newLine()
                            }
                            if (s.email.isNotEmpty()) {
                                w.write(s.email)
                                w.newLine()
                            }
                            if (s.notes.isNotEmpty()) {
                                w.write(s.notes)
                                w.newLine()
                            }
                            w.newLine()
                        }
                    }
                    Toast.makeText(studentsFragment.activity, "Exported student list to the Downloads folder", Toast.LENGTH_SHORT).show()

                    val intent = Intent(studentsFragment.activity, ActivityMain::class.java)
                    intent.putExtra("EXPORTED_STUDENTS", true)
                    studentsFragment.activity!!.startActivity(intent)
                } else {
                    Toast.makeText(studentsFragment.activity, "External storage is not writable.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}