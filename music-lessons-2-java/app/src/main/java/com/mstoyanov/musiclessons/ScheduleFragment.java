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

import com.mstoyanov.musiclessons.model.Lesson;
import com.mstoyanov.musiclessons.model.LessonWithStudent;
import com.mstoyanov.musiclessons.model.Weekday;
import com.mstoyanov.musiclessons.repository.AppDatabase;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ScheduleFragment extends Fragment {
    // these fields can not be static:
    private RecyclerView.Adapter adapter;
    private ProgressBar progressBar;
    private List<Lesson> lessons;

    public static ScheduleFragment create(int position) {
        ScheduleFragment fragment = new ScheduleFragment();
        Bundle args = new Bundle();
        args.putInt("POSITION", position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_schedule, container, false);

        final TextView title = rootView.findViewById(R.id.weekday);
        final int position = getArguments().getInt("POSITION");
        title.setText(MainActivity.sectionTitles.get(position));

        final RecyclerView recyclerView = rootView.findViewById(R.id.lessons);
        lessons = new ArrayList<>();
        adapter = new LessonsAdapter(lessons);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        progressBar = rootView.findViewById(R.id.progress_bar);
        progressBar.setIndeterminate(true);

        if (savedInstanceState == null) {
            new FindAllLessonsWithStudentByWeekday(
                    lessons,
                    MainActivity.sectionTitles.get(position),  // weekday
                    adapter,
                    progressBar).execute();
        } else {
            progressBar.setVisibility(View.GONE);
            lessons.addAll((List<Lesson>) savedInstanceState.getSerializable("LESSONS"));
            adapter.notifyDataSetChanged();
        }

        final FloatingActionButton button = rootView.findViewById(R.id.add_lesson);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddLessonActivity.class);
                intent.putExtra("WEEKDAY", Weekday.values()[position]);
                startActivity(intent);
            }
        });

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        state.putSerializable("LESSONS", (Serializable) lessons);
    }

    // this class can not be static; a separate instance for all six ScheduleFragment instances is needed:
    private class FindAllLessonsWithStudentByWeekday extends AsyncTask<Long, Integer, List<LessonWithStudent>> {
        private final List<Lesson> lessons;
        private final String weekday;
        private final WeakReference<RecyclerView.Adapter> adapterWeakReference;
        private final WeakReference<ProgressBar> progressBarWeakReference;
        private final AppDatabase DB = MusicLessonsApplication.getDB();

        FindAllLessonsWithStudentByWeekday(List<Lesson> lessons, String weekday, RecyclerView.Adapter adapter, ProgressBar progressBar) {
            this.lessons = lessons;
            this.weekday = weekday;
            this.adapterWeakReference = new WeakReference<>(adapter);
            this.progressBarWeakReference = new WeakReference<>(progressBar);
        }

        @Override
        protected List<LessonWithStudent> doInBackground(Long... params) {
            /*try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/
            return DB.getLessonDao().findAllWithStudentByWeekday(weekday);
        }

        @Override
        protected void onPostExecute(List<LessonWithStudent> result) {
            final ProgressBar progressBar = progressBarWeakReference.get();
            if (progressBar != null) progressBar.setVisibility(View.GONE);
            List<Lesson> lessonList = new ArrayList<>();
            for (int i = 0; i < result.size(); i++) {
                LessonWithStudent lessonWithStudent = result.get(i);
                lessonList.add(lessonWithStudent.lesson);
                lessonList.get(i).setStudent(lessonWithStudent.student);
            }
            Collections.sort(lessonList);
            lessons.addAll(lessonList);
            final RecyclerView.Adapter adapter = adapterWeakReference.get();
            if (adapter != null) adapter.notifyDataSetChanged();
        }
    }
}