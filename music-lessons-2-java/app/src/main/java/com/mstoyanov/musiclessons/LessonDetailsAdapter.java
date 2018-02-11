package com.mstoyanov.musiclessons;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mstoyanov.musiclessons.model.PhoneNumber;

import java.util.List;

public class LessonDetailsAdapter extends RecyclerView.Adapter<LessonDetailsAdapter.ViewHolder> {
    private List<PhoneNumber> phoneNumbers;
    private Context context;

    LessonDetailsAdapter(List<PhoneNumber> phoneNumbers, Context context) {
        this.phoneNumbers = phoneNumbers;
        this.context = context;
    }

    @Override
    public LessonDetailsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View phoneNumberItem = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.phone_item_st_details,
                parent,
                false);
        return new LessonDetailsAdapter.ViewHolder(phoneNumberItem);
    }

    @Override
    public void onBindViewHolder(LessonDetailsAdapter.ViewHolder holder, int position) {
        holder.number.setText(phoneNumbers.get(position).getNumber());
        holder.type.setText(phoneNumbers.get(position).getType().getDisplayValue());
        if (phoneNumbers.get(position).getType().getDisplayValue().equalsIgnoreCase("cell")) {
            holder.sms.setVisibility(View.VISIBLE);
            holder.number.setPadding(0, 0, dpToPx(16), 0);
        }
    }

    @Override
    public int getItemCount() {
        return phoneNumbers.size();
    }

    private int dpToPx(int dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView number;
        private final TextView type;
        private final ImageView sms;

        ViewHolder(final View view) {
            super(view);

            type = view.findViewById(R.id.phone_number_type);
            number = view.findViewById(R.id.phone_number);
            sms = view.findViewById(R.id.sms);

            number.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((LessonDetailsActivity) view.getContext()).dial(number.getText().toString());
                }
            });

            sms.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + number.getText().toString()));
                    view.getContext().startActivity(intent);
                }
            });
        }
    }
}