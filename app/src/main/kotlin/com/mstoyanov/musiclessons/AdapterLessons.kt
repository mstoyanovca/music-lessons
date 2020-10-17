package com.mstoyanov.musiclessons

import android.content.Context
import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.mstoyanov.musiclessons.model.Lesson
import java.text.SimpleDateFormat
import java.util.*

class AdapterLessons(private val lessons: List<Lesson>) : RecyclerView.Adapter<AdapterLessons.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterLessons.ViewHolder {
        val lessonItem = LayoutInflater.from(parent.context).inflate(
                R.layout.lesson_item,
                parent,
                false)
        return AdapterLessons.ViewHolder(lessonItem, lessons)
    }

    override fun onBindViewHolder(holder: AdapterLessons.ViewHolder, position: Int) {
        val format = SimpleDateFormat("HH:mm", Locale.US)
        format.timeZone = TimeZone.getTimeZone("UTC")
        val timeFrom = format.format(lessons[position].timeFrom)
        val timeTo = format.format(lessons[position].timeTo)
        holder.time.text = StringBuilder().append(timeFrom).append(holder.context.getString(R.string.dash)).append(timeTo).toString()

        val firstName = lessons[position].student.firstName
        val lastName = lessons[position].student.lastName
        holder.name.text = StringBuilder().append(firstName).append(holder.context.getString(R.string.space)).append(lastName).toString()
    }

    override fun getItemCount(): Int {
        return lessons.size
    }

    class ViewHolder(view: View, private val lessons: List<Lesson>) : RecyclerView.ViewHolder(view), View.OnClickListener {
        val time: TextView
        val name: TextView
        val context: Context

        init {
            view.setOnClickListener(this)
            this.context = view.context
            time = view.findViewById(R.id.time)
            name = view.findViewById(R.id.name)
        }

        override fun onClick(v: View) {
            val intent = Intent(v.context, ActivityLessonDetails::class.java)
            val lesson = lessons[adapterPosition]
            intent.putExtra("LESSON", lesson)
            v.context.startActivity(intent)
        }
    }
}