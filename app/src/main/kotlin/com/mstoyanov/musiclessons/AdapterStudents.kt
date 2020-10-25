package com.mstoyanov.musiclessons

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.mstoyanov.musiclessons.model.PhoneNumber
import com.mstoyanov.musiclessons.model.Student
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AdapterStudents(private val students: List<Student>, private val fragment: FragmentStudents) : RecyclerView.Adapter<AdapterStudents.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterStudents.ViewHolder {
        val name = LayoutInflater.from(parent.context).inflate(R.layout.student_item, parent, false) as TextView
        return ViewHolder(name, fragment)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.name.text = StringBuilder().append(students[position].firstName).append(fragment.getString(R.string.space)).append(students[position].lastName).toString()
    }

    override fun getItemCount(): Int {
        return students.size
    }

    inner class ViewHolder(val name: TextView, private val fragment: FragmentStudents) : RecyclerView.ViewHolder(name), View.OnClickListener {
        init {
            name.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            fragment.startProgressBar()
            val student = students[adapterPosition]
            fragment.lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    // Thread.sleep(1_000)
                    val phoneNumbers: MutableList<PhoneNumber> = MusicLessonsApplication.db.phoneNumberDao.findAllByStudentId2(student.studentId)
                    withContext(Dispatchers.Main) {
                        fragment.stopProgressBar()
                        student.phoneNumbers = phoneNumbers
                        val intent = Intent(fragment.context, ActivityStudentDetails::class.java)
                        intent.putExtra("STUDENT", student)
                        fragment.startActivity(intent)
                    }
                }
            }
        }
    }
}
