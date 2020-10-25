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
import kotlinx.coroutines.launch
import java.io.Serializable

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
        progressBar.visibility = View.VISIBLE

        if (savedInstanceState == null) {
            lifecycleScope.launch {
                val result: MutableList<LessonWithStudent> = MusicLessonsApplication.db.lessonDao.findAllWithStudentByWeekday(ActivityMain.sectionTitles[position])
                result.forEach { it.lesson.student = it.student }
                val lessonList: MutableList<Lesson> = result.map { it.lesson }.toMutableList()
                lessonList.sort()
                onResult(lessonList)
            }
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

    private fun onResult(lessonList: MutableList<Lesson>) {
        this.view!!.findViewById<ProgressBar>(R.id.progress_bar).visibility = View.GONE

        this.lessons.addAll(lessonList)
        this.adapter.notifyDataSetChanged()
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
