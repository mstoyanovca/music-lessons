package com.mstoyanov.musiclessons

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.mstoyanov.musiclessons.model.Lesson
import com.mstoyanov.musiclessons.model.LessonWithStudent
import com.mstoyanov.musiclessons.model.Weekday
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.Serializable

class FragmentSchedule : Fragment() {
    // this field can not be static:
    private lateinit var lessons: MutableList<Lesson>
    private lateinit var adapter: AdapterLessons

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_schedule, container, false)

        val title = rootView.findViewById<TextView>(R.id.weekday)
        val position = requireArguments().getInt("POSITION")
        title.text = ActivityMain.sectionTitles[position]

        lessons = mutableListOf()
        val recyclerView = rootView.findViewById<RecyclerView>(R.id.lessons)

        val progressBar: ProgressBar = rootView.findViewById(R.id.progress_bar)
        progressBar.isIndeterminate = true
        progressBar.visibility = View.VISIBLE

        if (savedInstanceState == null) {
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    val result: List<LessonWithStudent> = MusicLessonsApplication.db.lessonDao.findWithStudentByWeekday(ActivityMain.sectionTitles[position])
                    withContext(Dispatchers.Main) {
                        result.forEach { it.lesson.student = it.student }
                        val lessonList: MutableList<Lesson> = result.map { it.lesson }.toMutableList()
                        lessonList.sort()
                        progressBar.visibility = View.GONE

                        lessons.addAll(lessonList)
                        adapter.notifyItemRangeInserted(0, lessonList.size)
                    }
                }
            }
        } else {
            lessons.addAll(savedInstanceState.getSerializable("LESSONS", ArrayList<Lesson>()::class.java)!!)
            progressBar.visibility = View.GONE
        }

        adapter = AdapterLessons(lessons)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(activity)

        val button = rootView.findViewById<FloatingActionButton>(R.id.add_lesson)
        button.setOnClickListener {
            val intent = Intent(activity, ActivityAddLesson::class.java)
            intent.putExtra("WEEKDAY", Weekday.entries[position])
            startActivity(intent)
        }

        return rootView
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putSerializable("LESSONS", lessons as Serializable)
        super.onSaveInstanceState(outState)
    }

    companion object {
        fun create(position: Int): FragmentSchedule {
            val fragment = FragmentSchedule()
            val args = Bundle()
            args.putInt("POSITION", position)
            fragment.arguments = args
            return fragment
        }
    }
}
