package com.mstoyanov.musiclessons;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mstoyanov.musiclessons.model.Lesson;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class LessonsAdapter extends RecyclerView.Adapter<LessonsAdapter.ViewHolder> {
    //this field can not be static:
    private List<Lesson> lessons;

    LessonsAdapter(List<Lesson> lessons) {
        this.lessons = lessons;
    }

    @Override
    public LessonsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View lessonItem = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.lesson_item,
                parent,
                false);
        return new LessonsAdapter.ViewHolder(lessonItem, lessons);
    }

    @Override
    public void onBindViewHolder(LessonsAdapter.ViewHolder holder, int position) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm", Locale.US);
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        String timeFrom = format.format(lessons.get(position).getTimeFrom());
        String timeTo = format.format(lessons.get(position).getTimeTo());
        holder.time.setText(new StringBuilder().
                append(timeFrom).
                append(holder.context.getString(R.string.dash)).
                append(timeTo).toString());

        String firstName = lessons.get(position).getStudent().getFirstName();
        String lastName = lessons.get(position).getStudent().getLastName();
        holder.name.setText(new StringBuilder().
                append(firstName).
                append(holder.context.getString(R.string.space)).
                append(lastName).toString());
    }

    @Override
    public int getItemCount() {
        return lessons.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView time;
        private final TextView name;
        private final List<Lesson> lessons;
        private final Context context;

        ViewHolder(View view, List<Lesson> lessons) {
            super(view);
            view.setOnClickListener(this);
            this.lessons = lessons;
            this.context = view.getContext();
            time = view.findViewById(R.id.time);
            name = view.findViewById(R.id.name);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(v.getContext(), LessonDetailsActivity.class);
            final Lesson lesson = lessons.get(getAdapterPosition());
            intent.putExtra("LESSON", lesson);
            v.getContext().startActivity(intent);
        }
    }
}