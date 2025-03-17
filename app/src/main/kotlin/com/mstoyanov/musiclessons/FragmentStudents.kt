package com.mstoyanov.musiclessons

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.mstoyanov.musiclessons.model.Student
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedWriter
import java.io.OutputStreamWriter
import java.io.Serializable
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class FragmentStudents : Fragment(), MenuProvider {
    private lateinit var progressBar: ProgressBar
    private lateinit var adapter: AdapterStudents
    private lateinit var students: MutableList<Student>
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_students, container, false)

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        val title = rootView.findViewById<TextView>(R.id.heading)
        title.setText(R.string.students_label)

        students = mutableListOf()
        val recyclerView = rootView.findViewById<RecyclerView>(R.id.students_list)

        progressBar = rootView.findViewById(R.id.progress_bar)
        progressBar.isIndeterminate = true
        progressBar.visibility = View.VISIBLE

        if (savedInstanceState == null) {
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    students.addAll(MusicLessonsApplication.db.studentDao.findAll())
                    withContext(Dispatchers.Main) {
                        students.sort()
                        progressBar.visibility = View.GONE
                        adapter.notifyItemRangeChanged(0, students.size)
                        requireActivity().invalidateOptionsMenu()
                    }
                }
            }
        } else {
            students.addAll(savedInstanceState.getSerializable("STUDENTS", ArrayList<Student>()::class.java)!!)
            progressBar.visibility = View.GONE
        }

        adapter = AdapterStudents(students, this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(activity)

        val button: FloatingActionButton = rootView.findViewById(R.id.add_student)
        button.setOnClickListener { startActivity(Intent(activity, ActivityAddStudent::class.java)) }

        initResultLauncher()

        return rootView
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putSerializable("STUDENTS", students as Serializable)
        super.onSaveInstanceState(outState)
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.menu_export_students, menu)
        if (students.isEmpty()) {
            menu.findItem(R.id.action_export_students).isEnabled = false
            menu.findItem(R.id.action_export_students).icon?.alpha = 127
        } else {
            menu.findItem(R.id.action_export_students).isEnabled = true
            menu.findItem(R.id.action_export_students).icon?.alpha = 255
        }
    }

    override fun onMenuItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_export_students -> {
                progressBar.visibility = View.VISIBLE
                val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                    addCategory(Intent.CATEGORY_OPENABLE)
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TITLE, "student_list_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy_MM_dd-HH_mm_ss")) + ".txt")
                    putExtra(DocumentsContract.EXTRA_INITIAL_URI, Environment.DIRECTORY_DOWNLOADS)
                }
                resultLauncher.launch(intent)
                true
            }
            else -> false
        }
    }

    private fun initResultLauncher() {
        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        val studentsWithPhoneNumbers: List<Student> = MusicLessonsApplication.db.studentDao.findAllWithPhoneNumbers()
                        withContext(Dispatchers.Main) {
                            progressBar.visibility = View.GONE
                            result.data?.data?.also { uri ->
                                onFindAllWithPhoneNumbersResult(studentsWithPhoneNumbers, uri)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun onFindAllWithPhoneNumbersResult(studentList: List<Student>, uri: Uri) {
        if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
            val outputStream = activity?.contentResolver?.openOutputStream(uri)
            val bufferedWriter = BufferedWriter(OutputStreamWriter(outputStream))

            studentList.map { s ->
                bufferedWriter.write(s.firstName + " " + s.lastName)
                bufferedWriter.newLine()
                s.phoneNumbers.map { pn ->
                    bufferedWriter.write(pn.number + " " + pn.type.displayValue())
                    bufferedWriter.newLine()
                }
                if (s.notes.isNotEmpty()) {
                    bufferedWriter.write(s.notes)
                    bufferedWriter.newLine()
                }
                bufferedWriter.newLine()
            }

            bufferedWriter.close()
            outputStream?.close()

            Toast.makeText(activity, "Exported student list", Toast.LENGTH_LONG).show()

            val intent = Intent(activity, ActivityMain::class.java)
            intent.putExtra(resources.getString(R.string.export_students), true)
            requireActivity().startActivity(intent)
        } else {
            Toast.makeText(activity, "External storage is not writable.", Toast.LENGTH_LONG).show()
        }
    }

    fun startProgressBar() {
        progressBar.visibility = View.VISIBLE
    }

    fun stopProgressBar() {
        progressBar.visibility = View.GONE
    }

    companion object {
        fun create(position: Int): FragmentStudents {
            val fragment = FragmentStudents()
            val args = Bundle()
            args.putInt("POSITION", position)
            fragment.arguments = args
            return fragment
        }
    }
}
