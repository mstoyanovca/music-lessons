package com.mstoyanov.musiclessons

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.view.*
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.mstoyanov.musiclessons.model.Student
import com.mstoyanov.musiclessons.model.StudentWithPhoneNumbers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedWriter
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.io.Serializable

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

        progressBar = rootView.findViewById(R.id.progress_bar)
        progressBar.isIndeterminate = true
        progressBar.visibility = View.VISIBLE

        if (savedInstanceState == null) {
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    // Thread.sleep(1_000)
                    students.addAll(MusicLessonsApplication.db.studentDao.findAll2())
                    withContext(Dispatchers.Main) {
                        students.sort()
                        progressBar.visibility = View.GONE
                        adapter.notifyDataSetChanged()
                        activity!!.invalidateOptionsMenu()
                    }
                }
            }
        } else {
            @Suppress("UNCHECKED_CAST")
            students.addAll(savedInstanceState.getSerializable("STUDENTS") as MutableList<Student>)
            progressBar.visibility = View.GONE
        }

        adapter = AdapterStudents(students, this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(activity)

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

    fun startProgressBar() {
        progressBar.visibility = View.VISIBLE
    }

    fun stopProgressBar() {
        progressBar.visibility = View.GONE
    }

    private fun exportStudents() {
        lifecycleScope.launch {
            val studentsWithPhoneNumbers: List<StudentWithPhoneNumbers> = MusicLessonsApplication.db.studentDao.findAllWithPhoneNumbers()
            onFindAllWithPhoneNumbersResult(studentsWithPhoneNumbers)
        }
    }

    private fun onFindAllWithPhoneNumbersResult(result: List<StudentWithPhoneNumbers>) {
        this.progressBar.visibility = View.GONE

        result.forEach { it.student.phoneNumbers = it.phoneNumbers.toMutableList() }
        val studentList: List<Student> = result.map { it.student }.sorted()

        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "text/plain"
            putExtra(Intent.EXTRA_TITLE, "student_list_" + System.currentTimeMillis().toString() + ".txt")
            putExtra(DocumentsContract.EXTRA_INITIAL_URI, Environment.DIRECTORY_DOWNLOADS)
            putExtra("STUDENTS", ArrayList(studentList))
        }
        startActivityForResult(intent, WRITE_REQUEST_CODE)
    }

    @Suppress("UNCHECKED_CAST")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == WRITE_REQUEST_CODE) {
            when (resultCode) {
                Activity.RESULT_OK -> if (data?.data != null) {
                    val outputStream: OutputStream? = activity?.contentResolver?.openOutputStream(data.data!!)
                    val w = BufferedWriter(OutputStreamWriter(outputStream))
                    (data.getSerializableExtra("students") as List<Student>).map { s ->
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
                        w.close()
                        w.flush()
                    }
                    Toast.makeText(this.activity, "Exported student list to the Downloads folder", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this.activity, ActivityMain::class.java)
                    intent.putExtra("EXPORTED_STUDENTS", true)
                    this.activity!!.startActivity(intent)
                }
                Activity.RESULT_CANCELED -> {
                }
            }
        }
    }

    companion object {
        const val WRITE_REQUEST_CODE = 101

        fun create(position: Int): FragmentStudents {
            val fragment = FragmentStudents()
            val args = Bundle()
            args.putInt("POSITION", position)
            fragment.arguments = args
            return fragment
        }
    }
}
