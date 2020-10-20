package com.mstoyanov.musiclessons

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.mstoyanov.musiclessons.model.Lesson
import com.mstoyanov.musiclessons.model.LessonWithStudent
import com.mstoyanov.musiclessons.model.Weekday
import java.io.Serializable
import java.lang.ref.WeakReference

class FragmentSchedule : Fragment() {
    // this field can not be static:
    private lateinit var lessons: MutableList<Lesson>
    private lateinit var adapter: AdapterLessons


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_schedule, container, false)

        val title = rootView.findViewById<TextView>(R.id.weekday)
        val position = arguments!!.getInt("POSITION")
        title.text = ActivityMain.sectionTitles[position]
        lessons = mutableListOf()
        adapter = AdapterLessons(lessons)
        val recyclerView = rootView.findViewById<RecyclerView>(R.id.lessons)

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(activity)

        val progressBar: ProgressBar = rootView.findViewById(R.id.progress_bar)
        progressBar.isIndeterminate = true

        if (savedInstanceState == null) {
            FindAllLessonsWithStudentByWeekday(this, ActivityMain.sectionTitles[position]).execute()
        } else {
            progressBar.visibility = View.GONE
            @Suppress("UNCHECKED_CAST")
            lessons.addAll(savedInstanceState.getSerializable("LESSONS") as MutableList<Lesson>)
            adapter.notifyDataSetChanged()
        }

        val button = rootView.findViewById<FloatingActionButton>(R.id.add_lesson)
        button.setOnClickListener {
            val intent = Intent(activity, ActivityAddLesson::class.java)
            intent.putExtra("WEEKDAY", Weekday.values()[position])
            startActivity(intent)
        }

        return rootView
    }

    override fun onSaveInstanceState(state: Bundle) {
        super.onSaveInstanceState(state)
        state.putSerializable("LESSONS", lessons as Serializable)
    }

    companion object {

        fun create(position: Int): FragmentSchedule {
            val fragment = FragmentSchedule()
            val args = Bundle()
            args.putInt("POSITION", position)
            fragment.arguments = args
            return fragment
        }

        private class FindAllLessonsWithStudentByWeekday(context: FragmentSchedule, private val weekday: String) : AsyncTask<Long, Int, List<LessonWithStudent>>() {
            private val scheduleFragmentWeakReference: WeakReference<FragmentSchedule> = WeakReference(context)

            override fun doInBackground(vararg p0: Long?): List<LessonWithStudent> {
                // Thread.sleep(1000)
                return MusicLessonsApplication.db.lessonDao.findAllWithStudentByWeekday(weekday)
            }

            override fun onPostExecute(result: List<LessonWithStudent>) {
                val scheduleFragment = scheduleFragmentWeakReference.get()
                scheduleFragment!!.view!!.findViewById<ProgressBar>(R.id.progress_bar).visibility = View.GONE

                result.forEach { it.lesson.student = it.student }
                val lessonList: MutableList<Lesson> = result.map { it.lesson }.toMutableList()
                lessonList.sort()

                scheduleFragment.lessons.addAll(lessonList)
                scheduleFragment.adapter.notifyDataSetChanged()
            }
        }
    }
}
