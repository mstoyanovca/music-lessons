package com.mstoyanov.musiclessons;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mstoyanov.musiclessons.model.PhoneNumber;
import com.mstoyanov.musiclessons.model.Student;
import com.mstoyanov.musiclessons.repository.AppDatabase;

import java.util.List;

public class StudentsAdapter extends RecyclerView.Adapter<StudentsAdapter.ViewHolder> {
    private static List<Student> students;
    private StudentsFragment fragment;

    StudentsAdapter(List<Student> students, StudentsFragment fragment) {
        StudentsAdapter.students = students;
        this.fragment = fragment;
    }

    @Override
    public StudentsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        TextView name = (TextView) LayoutInflater.from(parent.getContext()).inflate(
                R.layout.student_item,
                parent,
                false);
        return new ViewHolder(name, fragment);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.name.setText(
                new StringBuilder().
                        append(students.get(position).getFirstName()).
                        append(fragment.getString(R.string.space)).
                        append(students.get(position).getLastName()).toString());
    }

    @Override
    public int getItemCount() {
        return students == null ? 0 : students.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView name;
        private final StudentsFragment fragment;

        ViewHolder(TextView name, StudentsFragment fragment) {
            super(name);
            this.name = name;
            name.setOnClickListener(this);
            this.fragment = fragment;
        }

        @Override
        public void onClick(View view) {
            fragment.startProgressBar();
            new FindAllPhoneNumbersByStudentId(students.get(getAdapterPosition()), fragment).execute();
        }
    }

    private static class FindAllPhoneNumbersByStudentId extends AsyncTask<Long, Integer, List<PhoneNumber>> {
        private final Student student;
        private final StudentsFragment fragment;
        private static final AppDatabase DB = MusicLessonsApplication.getDB();

        FindAllPhoneNumbersByStudentId(Student student, StudentsFragment fragment) {
            this.fragment = fragment;
            this.student = student;
        }

        @Override
        protected List<PhoneNumber> doInBackground(Long... params) {
            /*try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/
            return DB.getPhoneNumberDao().findAllByStudentId(student.getStudentId());
        }

        @Override
        protected void onPostExecute(List<PhoneNumber> result) {
            fragment.stopProgressBar();
            student.setPhoneNumbers(result);
            Intent intent = new Intent(fragment.getContext(), StudentDetailsActivity.class);
            intent.putExtra("STUDENT", student);
            fragment.startActivity(intent);
        }
    }
}