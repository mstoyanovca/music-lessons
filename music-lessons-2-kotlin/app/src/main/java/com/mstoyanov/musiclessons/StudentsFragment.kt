package com.mstoyanov.musiclessons

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import com.mstoyanov.musiclessons.model.Student
import java.io.Serializable
import java.lang.ref.WeakReference

class StudentsFragment : Fragment() {
    private lateinit var progressBar: ProgressBar
    private lateinit var adapter: StudentsAdapter
    private var students: MutableList<Student> = mutableListOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_students, container, false)

        val title = rootView.findViewById<TextView>(R.id.heading)
        title.setText(R.string.students_label)

        val recyclerView = rootView.findViewById<RecyclerView>(R.id.students_list)
        adapter = StudentsAdapter(students, this)
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
        }

        val button: FloatingActionButton = rootView.findViewById(R.id.add_student)
        button.setOnClickListener { startActivity(Intent(activity, AddStudentActivity::class.java)) }

        return rootView
    }

    override fun onSaveInstanceState(state: Bundle) {
        super.onSaveInstanceState(state)
        state.putSerializable("STUDENTS", students as Serializable)
    }

    fun startProgressBar() {
        progressBar.visibility = View.VISIBLE
    }

    fun stopProgressBar() {
        progressBar.visibility = View.GONE
    }

    companion object {

        fun create(position: Int): StudentsFragment {
            val fragment = StudentsFragment()
            val args = Bundle()
            args.putInt("POSITION", position)
            fragment.arguments = args
            return fragment
        }

        private class LoadStudents(context: StudentsFragment) : AsyncTask<Long, Int, MutableList<Student>>() {
            private val studentsFragmentWeakReference = WeakReference<StudentsFragment>(context)

            override fun doInBackground(vararg p0: Long?): MutableList<Student> {
                /*try {
                    Thread.sleep(1000)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }*/
                return MusicLessonsApplication.db.studentDao.findAll()
            }

            override fun onPostExecute(result: MutableList<Student>) {
                studentsFragmentWeakReference.get()!!.progressBar.visibility = View.GONE
                result.sort()
                studentsFragmentWeakReference.get()!!.students.addAll(result)
                studentsFragmentWeakReference.get()!!.adapter.notifyDataSetChanged()
            }
        }
    }
}