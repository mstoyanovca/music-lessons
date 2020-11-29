package com.mstoyanov.musiclessons

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.NavUtils
import androidx.lifecycle.lifecycleScope
import com.mstoyanov.musiclessons.model.Lesson
import com.mstoyanov.musiclessons.model.Student
import com.mstoyanov.musiclessons.model.Weekday
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.Serializable
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*

class ActivityEditLesson : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    private lateinit var hourFrom: NumberPicker
    private lateinit var minuteFrom: NumberPicker
    private lateinit var hourTo: NumberPicker
    private lateinit var minuteTo: NumberPicker
    private lateinit var progressBar: ProgressBar

    private lateinit var adapter: StudentsAdapter

    private lateinit var lesson: Lesson
    private lateinit var studentList: MutableList<Student>
    private var studentListIsEmpty: Boolean = false
    private val minutes = arrayOf("00", "15", "30", "45")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_lesson)

        lesson = Lesson()
        studentList = mutableListOf()

        setSupportActionBar(findViewById<View>(R.id.toolbar) as Toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        progressBar = findViewById(R.id.progress_bar)
        progressBar.isIndeterminate = true
        progressBar.visibility = View.VISIBLE

        val weekday = findViewById<Spinner>(R.id.weekday)
        val arrayAdapter = ArrayAdapter.createFromResource(this, R.array.weekdays, R.layout.weekday_item)
        arrayAdapter.setDropDownViewResource(R.layout.phone_type_dropdown_item)
        weekday.adapter = arrayAdapter
        weekday.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                lesson.weekday = Weekday.values()[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // do nothing
            }
        }

        val students: Spinner = findViewById(R.id.students)
        adapter = StudentsAdapter(this, studentList)
        students.adapter = adapter
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
            // coming from LessonDetails:
            lesson = intent.getSerializableExtra("LESSON") as Lesson
            initializeTime()
            studentListIsEmpty = true
            loadStudents()
        } else {
            // after screen rotation:
            progressBar.visibility = View.GONE

            lesson = savedInstanceState.getSerializable("LESSON") as Lesson
            @Suppress("UNCHECKED_CAST")
            studentList = savedInstanceState.getSerializable("STUDENTS") as MutableList<Student>
            studentListIsEmpty = studentList.isEmpty()
            adapter.addAll(studentList)
            students.setSelection(studentList.indexOf(studentList.filter { it.studentId == lesson.student.studentId }[0]))

            hourFrom.value = savedInstanceState.getInt("HOUR_FROM")
            minuteFrom.value = savedInstanceState.getInt("MINUTE_FROM")
            hourTo.value = savedInstanceState.getInt("HOUR_TO")
            minuteTo.value = savedInstanceState.getInt("MINUTE_TO")

            invalidateOptionsMenu()
        }

        weekday.setSelection(lesson.weekday.ordinal)
    }

    override fun onSaveInstanceState(state: Bundle) {
        super.onSaveInstanceState(state)

        state.putSerializable("LESSON", lesson)
        state.putSerializable("STUDENTS", studentList as Serializable)

        state.putInt("HOUR_FROM", hourFrom.value)
        state.putInt("MINUTE_FROM", minuteFrom.value)
        state.putInt("HOUR_TO", hourTo.value)
        state.putInt("MINUTE_TO", minuteTo.value)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_edit_lesson, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                NavUtils.navigateUpFromSameTask(this)
                true
            }
            R.id.action_update -> {
                setTime()
                progressBar.visibility = View.VISIBLE
                updateLesson()
                true
            }
            R.id.action_delete -> {
                createAlertDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        if (studentListIsEmpty) {
            menu.findItem(R.id.action_update).isEnabled = false
            menu.findItem(R.id.action_update).icon.alpha = 127
            menu.findItem(R.id.action_delete).isEnabled = false
            menu.findItem(R.id.action_delete).icon.alpha = 127
        } else {
            menu.findItem(R.id.action_update).isEnabled = true
            menu.findItem(R.id.action_update).icon.alpha = 255
            menu.findItem(R.id.action_delete).isEnabled = true
            menu.findItem(R.id.action_delete).icon.alpha = 255
        }
        return true
    }

    override fun onItemSelected(adapterView: AdapterView<*>, view: View?, i: Int, l: Long) {
        lesson.studentId = studentList[i].studentId
        lesson.student = studentList[i]
    }

    override fun onNothingSelected(adapterView: AdapterView<*>) {
        // do nothing
    }

    private val hourFromOnValueChangedListener = NumberPicker.OnValueChangeListener { _, _, newValue ->
        // 21:30 is maximum value:
        if (newValue == 21 && minuteFrom.value == 3) minuteFrom.value = 2
        synchronizeTimeToWithTimeFrom()
    }

    private val minuteFromOnValueChangedListener = NumberPicker.OnValueChangeListener { _, oldValue, newValue ->
        // overflow:
        if (oldValue == 3 && newValue == 0) hourFrom.value = hourFrom.value + 1
        if (oldValue == 0 && newValue == 3) hourFrom.value = hourFrom.value - 1
        // max value:
        if (newValue == 3 && hourFrom.value == 21) minuteFrom.value = 2
        synchronizeTimeToWithTimeFrom()
    }

    private val hourToOnValueChangedListener = NumberPicker.OnValueChangeListener { _, _, newValue ->
        // 8:30 is minimum value:
        if (newValue == 8 && (minuteTo.value == 0 || minuteTo.value == 1))
            minuteTo.value = 2
        // 22:00 is maximum value:
        if (newValue == 22 && minuteTo.value != 0) minuteTo.value = 0
        synchronizeTimeFromWithTimeTo()
    }

    private val minuteToOnValueChangedListener = NumberPicker.OnValueChangeListener { _, oldValue, newValue ->
        // overflow:
        if (oldValue == 3 && newValue == 0) hourTo.value = hourTo.value + 1
        if (oldValue == 0 && newValue == 3) hourTo.value = hourTo.value - 1
        // 8:30 is minimum value:
        if (hourTo.value == 8 && (newValue == 0 || newValue == 1)) minuteTo.value = 2
        // 22:00 is maximum value:
        if (hourTo.value == 22) minuteTo.value = 0
        synchronizeTimeFromWithTimeTo()
    }

    private fun initializeTime() {
        var formatter = DateTimeFormatter.ofPattern("HH")
        val hrFrom = formatter.format(lesson.timeFrom)
        val hrTo = formatter.format(lesson.timeTo)

        formatter = DateTimeFormatter.ofPattern("mm")
        val mntFrom = formatter.format(lesson.timeFrom)
        val mntTo = formatter.format(lesson.timeTo)

        hourFrom.value = Integer.parseInt(hrFrom)
        hourTo.value = Integer.parseInt(hrTo)
        minuteFrom.value = Arrays.binarySearch(minutes, mntFrom)
        minuteTo.value = Arrays.binarySearch(minutes, mntTo)
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

        val formatter = DateTimeFormatter.ofPattern("HH:mm")

        val dateFrom = LocalTime.parse(timeFromString, formatter)
        val dateTo = LocalTime.parse(timeToString, formatter)

        lesson.timeFrom = dateFrom
        lesson.timeTo = dateTo
    }

    private fun createAlertDialog() {
        val builder = AlertDialog.Builder(this)
        val message = if (lesson.student.firstName.trim().isEmpty() && lesson.student.lastName.trim().isNotEmpty()) {
            "Delete lesson with " + lesson.student.lastName + "?"
        } else if (lesson.student.firstName.trim().isNotEmpty() && lesson.student.lastName.trim().isEmpty()) {
            "Delete lesson with " + lesson.student.firstName + "?"
        } else {
            "Delete lesson with " + getString(R.string.full_name, lesson.student.firstName, lesson.student.lastName) + "?"
        }
        builder.setMessage(message)
        builder.setPositiveButton("OK") { _, _ -> delete() }
        builder.setNegativeButton("Cancel") { _, _ ->
            // do nothing
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun delete() {
        progressBar.visibility = View.VISIBLE
        deleteLesson()
    }

    private class StudentsAdapter(context: Context, studentList: List<Student>) : ArrayAdapter<Student>(context, 0, studentList) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            var view = convertView
            val student = getItem(position)
            if (view == null) {
                view = LayoutInflater.from(context).inflate(R.layout.student_item, parent, false)
            }
            val name = view!!.findViewById<TextView>(R.id.name)
            name.text = StringBuilder().append(student!!.firstName).append(view.context.getString(R.string.space)).append(student.lastName).toString()
            return view
        }

        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
            var view = convertView
            val student = getItem(position)
            if (view == null) {
                view = LayoutInflater.from(context).inflate(R.layout.student_dropdown_item, parent, false)
            }
            val dropDownName = view!!.findViewById<TextView>(R.id.name)
            dropDownName.text = StringBuilder().append(student!!.firstName).append(view.context.getString(R.string.space)).append(student.lastName).toString()
            return view
        }
    }

    private fun loadStudents() {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                // Thread.sleep(1_000)
                val result = MusicLessonsApplication.db.studentDao.findAll()

                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE

                    result.sort()
                    studentList = result
                    studentListIsEmpty = studentList.isEmpty()
                    adapter.addAll(studentList)

                    val students: Spinner = findViewById(R.id.students)
                    students.setSelection(studentList.indexOf(studentList.filter { it.studentId == lesson.student.studentId }[0]))

                    invalidateOptionsMenu()
                }
            }
        }
    }

    private fun updateLesson() {
        val intent = Intent(this, ActivityLessonDetails::class.java)

        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                // Thread.sleep(1_000)
                MusicLessonsApplication.db.lessonDao.update(lesson)

                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE

                    intent.putExtra("UPDATED_LESSON", lesson)
                    startActivity(intent)
                }
            }
        }
    }

    private fun deleteLesson() {
        val intent = Intent(this, ActivityMain::class.java)

        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                // Thread.sleep(1_000)
                MusicLessonsApplication.db.lessonDao.delete(lesson)

                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE

                    intent.putExtra("WEEKDAY", lesson.weekday)
                    startActivity(intent)
                }
            }
        }
    }
}
