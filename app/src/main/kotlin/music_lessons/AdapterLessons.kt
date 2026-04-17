package music_lessons

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import music_lessons.model.Lesson
import music_lessons.global.Functions

class AdapterLessons(private val lessons: List<Lesson>) : RecyclerView.Adapter<AdapterLessons.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val lessonItem = LayoutInflater.from(parent.context).inflate(R.layout.lesson_item, parent, false)
        return ViewHolder(lessonItem, lessons)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val timeFrom = Functions.dateTimeFormatter.format(lessons[position].timeFrom)
        val timeTo = Functions.dateTimeFormatter.format(lessons[position].timeTo)

        holder.time.text = StringBuilder().append(timeFrom).append(holder.context.getString(R.string.dash)).append(timeTo).toString()

        val firstName = lessons[position].student.firstName
        val lastName = lessons[position].student.lastName

        holder.name.text = StringBuilder().append(firstName).append(holder.context.getString(R.string.space)).append(lastName).toString()
    }

    override fun getItemCount(): Int {
        return lessons.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    inner class ViewHolder(view: View, private val lessons: List<Lesson>) : RecyclerView.ViewHolder(view), View.OnClickListener {
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
            intent.putExtra("LESSON", lessons[bindingAdapterPosition])
            v.context.startActivity(intent)
        }
    }
}
