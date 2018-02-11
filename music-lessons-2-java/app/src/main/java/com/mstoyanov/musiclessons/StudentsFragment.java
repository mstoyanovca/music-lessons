package com.mstoyanov.musiclessons;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mstoyanov.musiclessons.model.Student;
import com.mstoyanov.musiclessons.repository.AppDatabase;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StudentsFragment extends Fragment {
    private RecyclerView.Adapter adapter;
    private ProgressBar progressBar;
    private static List<Student> studentList;

    public static StudentsFragment create(int position) {
        StudentsFragment fragment = new StudentsFragment();
        Bundle args = new Bundle();
        args.putInt("POSITION", position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_students, container, false);

        final TextView title = rootView.findViewById(R.id.heading);
        title.setText(R.string.students_label);

        final RecyclerView students = rootView.findViewById(R.id.students_list);
        studentList = new ArrayList<>();
        adapter = new StudentsAdapter(studentList, this);
        students.setAdapter(adapter);
        students.setLayoutManager(new LinearLayoutManager(getActivity()));

        progressBar = rootView.findViewById(R.id.progress_bar);
        progressBar.setIndeterminate(true);

        if (savedInstanceState == null) {
            new LoadStudents(progressBar, adapter).execute();
        } else {
            progressBar.setVisibility(View.GONE);
            studentList.addAll((List<Student>) savedInstanceState.getSerializable("STUDENTS"));
            adapter.notifyDataSetChanged();
        }

        FloatingActionButton button = rootView.findViewById(R.id.add_student);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), AddStudentActivity.class));
            }
        });

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        state.putSerializable("STUDENTS", (Serializable) studentList);
    }

    public void startProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    public void stopProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

    private static class LoadStudents extends AsyncTask<Long, Integer, List<Student>> {
        private final WeakReference<ProgressBar> progressBarWeakReference;
        private final WeakReference<RecyclerView.Adapter> adapterWeakReference;
        private static final AppDatabase DB = MusicLessonsApplication.getDB();

        LoadStudents(ProgressBar progressBar, RecyclerView.Adapter adapter) {
            progressBarWeakReference = new WeakReference<>(progressBar);
            adapterWeakReference = new WeakReference<>(adapter);
        }

        @Override
        protected List<Student> doInBackground(Long... params) {
            /*try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/
            return DB.getStudentDao().findAll();
        }

        @Override
        protected void onPostExecute(List<Student> result) {
            final ProgressBar progressBar = progressBarWeakReference.get();
            if (progressBar != null) progressBar.setVisibility(View.GONE);
            Collections.sort(result);
            studentList.addAll(result);
            final RecyclerView.Adapter adapter = adapterWeakReference.get();
            if (adapter != null) adapter.notifyDataSetChanged();
        }
    }
}