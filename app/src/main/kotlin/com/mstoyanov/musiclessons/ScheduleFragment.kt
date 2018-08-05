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
import com.mstoyanov.musiclessons.model.Lesson
import com.mstoyanov.musiclessons.model.LessonStudent
import com.mstoyanov.musiclessons.model.Weekday
import java.io.Serializable
import java.lang.ref.WeakReference

class ScheduleFragment : Fragment() {
    // this field can not be static:
    private var lessons: MutableList<Lesson> = mutableListOf()
    private var adapter: LessonsAdapter = LessonsAdapter(lessons)


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_schedule, container, false)

        val title = rootView.findViewById<TextView>(R.id.weekday)
        val position = arguments!!.getInt("POSITION")
        title.text = MainActivity.sectionTitles[position]

        val recyclerView = rootView.findViewById<RecyclerView>(R.id.lessons)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(activity)

        val progressBar: ProgressBar = rootView.findViewById(R.id.progress_bar)
        progressBar.isIndeterminate = true

        if (savedInstanceState == null) {
            FindAllLessonsWithStudentByWeekday(this, MainActivity.sectionTitles[position]).execute()
        } else {
            progressBar.visibility = View.GONE
            lessons.addAll(savedInstanceState.getSerializable("LESSONS") as MutableList<Lesson>)
            adapter.notifyDataSetChanged()
        }

        val button = rootView.findViewById<FloatingActionButton>(R.id.add_lesson)
        button.setOnClickListener {
            val intent = Intent(activity, AddLessonActivity::class.java)
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

        fun create(position: Int): ScheduleFragment {
            val fragment = ScheduleFragment()
            val args = Bundle()
            args.putInt("POSITION", position)
            fragment.arguments = args
            return fragment
        }

        private class FindAllLessonsWithStudentByWeekday(context: ScheduleFragment, private val weekday: String) : AsyncTask<Long, Int, List<LessonStudent>>() {
            private val scheduleFragmentWeakReference: WeakReference<ScheduleFragment> = WeakReference(context)

            override fun doInBackground(vararg p0: Long?): List<LessonStudent> {
                /*try {
                    Thread.sleep(1000)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }*/
                return MusicLessonsApplication.db.lessonDao.findAllWithStudentByWeekday(weekday)
            }

            override fun onPostExecute(result: List<LessonStudent>) {
                val scheduleFragment = scheduleFragmentWeakReference.get()
                scheduleFragment!!.view!!.findViewById<ProgressBar>(R.id.progress_bar).visibility = View.GONE

                val lessonList: MutableList<Lesson> = mutableListOf()
                result.map { lessonWithStudent ->
                    val lesson: Lesson = lessonWithStudent.lesson
                    lesson.student = lessonWithStudent.student
                    lessonList.add(lesson)
                }
                lessonList.sort()

                scheduleFragment.lessons.addAll(lessonList)
                scheduleFragment.adapter.notifyDataSetChanged()
            }
        }
    }
}