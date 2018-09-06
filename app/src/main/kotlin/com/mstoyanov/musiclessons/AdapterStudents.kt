package com.mstoyanov.musiclessons

import android.content.Intent
import android.os.AsyncTask
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.mstoyanov.musiclessons.model.PhoneNumber
import com.mstoyanov.musiclessons.model.Student

class AdapterStudents(private val students: List<Student>, private val fragment: FragmentStudents) : RecyclerView.Adapter<AdapterStudents.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterStudents.ViewHolder {
        val name = LayoutInflater.from(parent.context).inflate(
                R.layout.student_item,
                parent,
                false) as TextView
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
            FindAllPhoneNumbersByStudentId(students[adapterPosition], fragment).execute()
        }
    }

    companion object {

        private class FindAllPhoneNumbersByStudentId(private val student: Student, private val fragment: FragmentStudents) : AsyncTask<Long, Int, MutableList<PhoneNumber>>() {

            override fun doInBackground(vararg p0: Long?): MutableList<PhoneNumber> {
                // Thread.sleep(1000)
                return MusicLessonsApplication.db.phoneNumberDao.findAllByStudentId(student.studentId)
            }

            override fun onPostExecute(result: MutableList<PhoneNumber>) {
                fragment.stopProgressBar()
                student.phoneNumbers = result
                val intent = Intent(fragment.context, ActivityStudentDetails::class.java)
                intent.putExtra("STUDENT", student)
                fragment.startActivity(intent)
            }
        }
    }
}