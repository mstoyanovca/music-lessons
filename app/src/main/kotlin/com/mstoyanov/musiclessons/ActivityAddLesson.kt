package com.mstoyanov.musiclessons

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.NumberPicker
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.NavUtils
import androidx.lifecycle.lifecycleScope
import com.mstoyanov.musiclessons.global.Functions.dateTimeFormatter
import com.mstoyanov.musiclessons.global.Functions.serializable
import com.mstoyanov.musiclessons.model.Lesson
import com.mstoyanov.musiclessons.model.Student
import com.mstoyanov.musiclessons.model.Weekday
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.Serializable
import java.time.LocalTime

class ActivityAddLesson : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    private lateinit var hourFrom: NumberPicker
    private lateinit var minuteFrom: NumberPicker
    private lateinit var hourTo: NumberPicker
    private lateinit var minuteTo: NumberPicker

    private lateinit var progressBar: ProgressBar

    private lateinit var adapter: StudentsAdapter

    private lateinit var weekday: Weekday
    private lateinit var lesson: Lesson
    private lateinit var studentList: MutableList<Student>
    private var studentListIsEmpty: Boolean = true
    private val minutes = arrayOf("00", "15", "30", "45")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_lesson)

        weekday = intent.serializable("WEEKDAY")!!
        lesson = Lesson()
        studentList = mutableListOf()

        setSupportActionBar(findViewById<View>(R.id.toolbar) as Toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        progressBar = findViewById(R.id.progress_bar)
        progressBar.isIndeterminate = true
        progressBar.visibility = View.VISIBLE

        val weekdayTextView = findViewById<TextView>(R.id.weekday)
        weekdayTextView.text = weekday.displayValue()

        val students: Spinner = findViewById(R.id.students)
        students.onItemSelectedListener = this

        hourFrom = findViewById(R.id.hour_from)
        hourFrom.minValue = 8
        hourFrom.maxValue = 21
        hourFrom.wrapSelectorWheel = false
        hourFrom.setOnValueChangedListener(hourFromOnValueChangedListener)

        minuteFrom = findViewById(R.id.minute_from)
        minuteFrom.displayedValues = minutes
        minuteFrom.maxValue = 3
        minuteFrom.wrapSelectorWheel = true
        minuteFrom.setOnValueChangedListener(minuteFromOnValueChangedListener)

        hourTo = findViewById(R.id.hour_to)
        hourTo.minValue = 8
        hourTo.maxValue = 22
        hourTo.wrapSelectorWheel = false
        hourTo.setOnValueChangedListener(hourToOnValueChangedListener)

        minuteTo = findViewById(R.id.minute_to)
        minuteTo.displayedValues = minutes
        minuteTo.maxValue = 3
        minuteTo.wrapSelectorWheel = true
        minuteTo.setOnValueChangedListener(minuteToOnValueChangedListener)

        if (savedInstanceState == null) {
            // coming from FragmentSchedule:
            lesson.weekday = weekday
            initializeTime()

            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    val result: MutableList<Student> = MusicLessonsApplication.db.studentDao.findAll()
                    result.sort()
                    withContext(Dispatchers.Main) {
                        progressBar.visibility = View.GONE

                        studentList = result
                        studentListIsEmpty = studentList.isEmpty()

                        adapter.addAll(studentList)
                        invalidateOptionsMenu()
                    }
                }
            }
        } else {
            // after screen rotation:
            progressBar.visibility = View.GONE

            lesson = savedInstanceState.serializable("LESSON")!!

            studentList = savedInstanceState.serializable("STUDENTS")!!
            studentListIsEmpty = studentList.isEmpty()

            hourFrom.value = savedInstanceState.getInt("HOUR_FROM")
            minuteFrom.value = savedInstanceState.getInt("MINUTE_FROM")
            hourTo.value = savedInstanceState.getInt("HOUR_TO")
            minuteTo.value = savedInstanceState.getInt("MINUTE_TO")
        }

        adapter = StudentsAdapter(this, studentList)
        students.adapter = adapter
        invalidateOptionsMenu()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putSerializable("LESSON", lesson as Serializable)
        outState.putSerializable("STUDENTS", studentList as Serializable)

        outState.putInt("HOUR_FROM", hourFrom.value)
        outState.putInt("MINUTE_FROM", minuteFrom.value)
        outState.putInt("HOUR_TO", hourTo.value)
        outState.putInt("MINUTE_TO", minuteTo.value)

        super.onSaveInstanceState(outState)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_insert_lesson, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                NavUtils.navigateUpFromSameTask(this)
                true
            }

            R.id.action_insert -> {
                setTime()
                progressBar.visibility = View.VISIBLE

                lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        MusicLessonsApplication.db.lessonDao.insert(lesson)
                        withContext(Dispatchers.Main) {
                            progressBar.visibility = View.GONE

                            val intent = Intent(this@ActivityAddLesson, ActivityMain::class.java)
                            intent.putExtra("WEEKDAY", lesson.weekday)
                            startActivity(intent)
                        }
                    }
                }

                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        if (studentListIsEmpty) {
            menu.findItem(R.id.action_insert).isEnabled = false
            menu.findItem(R.id.action_insert).icon?.alpha = 127
        } else {
            menu.findItem(R.id.action_insert).isEnabled = true
            menu.findItem(R.id.action_insert).icon?.alpha = 255
        }
        return true
    }

    override fun onItemSelected(adapterView: AdapterView<*>, view: View?, i: Int, l: Long) {
        lesson.studentId = studentList[i].studentId
    }

    override fun onNothingSelected(adapterView: AdapterView<*>) {
        // do nothing
    }

    private val hourFromOnValueChangedListener = NumberPicker.OnValueChangeListener { _, _, newValue ->
        // 21:30 is the maximum value:
        if (newValue == 21 && minuteFrom.value == 3) minuteFrom.value = 2
        synchronizeTimeToWithTimeFrom()
    }

    private val minuteFromOnValueChangedListener = NumberPicker.OnValueChangeListener { _, oldValue, newValue ->
        // overflow:
        if (oldValue == 3 && newValue == 0) hourFrom.value += 1
        if (oldValue == 0 && newValue == 3) hourFrom.value -= 1
        // max value:
        if (newValue == 3 && hourFrom.value == 21) minuteFrom.value = 2
        synchronizeTimeToWithTimeFrom()
    }

    private val hourToOnValueChangedListener = NumberPicker.OnValueChangeListener { _, _, newValue ->
        // 8:30 is the minimum value:
        if (newValue == 8 && (minuteTo.value == 0 || minuteTo.value == 1))
            minuteTo.value = 2
        // 22:00 is the maximum value:
        if (newValue == 22 && minuteTo.value != 0) minuteTo.value = 0
        synchronizeTimeFromWithTimeTo()
    }

    private val minuteToOnValueChangedListener = NumberPicker.OnValueChangeListener { _, oldValue, newValue ->
        // overflow:
        if (oldValue == 3 && newValue == 0) hourTo.value += 1
        if (oldValue == 0 && newValue == 3) hourTo.value -= 1
        // 8:30 is the minimum value:
        if (hourTo.value == 8 && (newValue == 0 || newValue == 1)) minuteTo.value = 2
        // 22:00 is the maximum value:
        if (hourTo.value == 22) minuteTo.value = 0
        synchronizeTimeFromWithTimeTo()
    }

    private fun initializeTime() {
        if (weekday == Weekday.SATURDAY || weekday == Weekday.SUNDAY) {
            hourFrom.value = 9
            hourTo.value = 9
        } else {
            hourFrom.value = 16
            hourTo.value = 16
        }
        minuteFrom.value = 0
        minuteTo.value = 2
    }

    private fun synchronizeTimeToWithTimeFrom() {
        val lessonLength = (hourTo.value - hourFrom.value) * 4 + minuteTo.value - minuteFrom.value
        if (lessonLength < 2) {
            if (minuteFrom.value < 2) {
                hourTo.value = hourFrom.value
                minuteTo.value = minuteFrom.value + 2
            } else {
                hourTo.value = hourFrom.value + 1
                minuteTo.value = minuteFrom.value - 2
            }
        }
    }

    private fun synchronizeTimeFromWithTimeTo() {
        val lessonLength = (hourTo.value - hourFrom.value) * 4 + minuteTo.value - minuteFrom.value
        if (lessonLength < 2) {
            if (minuteTo.value >= 2) {
                hourFrom.value = hourTo.value
                minuteFrom.value = minuteTo.value - 2
            } else {
                hourFrom.value = hourTo.value - 1
                minuteFrom.value = minuteTo.value + 2
            }
        }
    }

    private fun setTime() {
        val timeFromString = hourFrom.value.toString() + ":" + minutes[minuteFrom.value]
        val timeToString = hourTo.value.toString() + ":" + minutes[minuteTo.value]

        val timeFrom = LocalTime.parse(timeFromString, dateTimeFormatter)
        val timeTo = LocalTime.parse(timeToString, dateTimeFormatter)

        lesson.timeFrom = timeFrom
        lesson.timeTo = timeTo
    }

    private class StudentsAdapter(context: Context, studentList: List<Student>) : ArrayAdapter<Student>(context, 0, studentList) {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            var view = convertView
            val student = getItem(position)
            if (view == null) {
                view = LayoutInflater.from(context).inflate(R.layout.student_item, parent, false)
            }
            val name = view!!.findViewById<TextView>(R.id.name)
            name.text = StringBuilder().append(student!!.firstName)
                .append(view.context.getString(R.string.space)).append(student.lastName).toString()
            return view
        }

        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
            var view = convertView
            val student = getItem(position)
            if (view == null) {
                view = LayoutInflater.from(context)
                    .inflate(R.layout.student_dropdown_item, parent, false)
            }
            val dropDownName = view!!.findViewById<TextView>(R.id.name)
            dropDownName.text = StringBuilder().append(student!!.firstName)
                .append(view.context.getString(R.string.space)).append(student.lastName).toString()
            return view
        }
    }
}
