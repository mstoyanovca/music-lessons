package com.mstoyanov.musiclessons.model;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mstoyanov.musiclessons.R;

import java.util.List;

public class ActionsAdapter extends ArrayAdapter<Actions> {
    private Context context;
    private List<Actions> actions;

    public ActionsAdapter(Context context, List<Actions> actions) {
        super(context, R.layout.action_list_item, actions);
        this.context = context;
        this.actions = actions;
    }

    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        Actions action = actions.get(position);
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        View view = inflater.inflate(R.layout.action_list_item, parent, false);
        TextView label = view.findViewById(R.id.label);
        label.setText(action.getLabel());
        TextView data = view.findViewById(R.id.data);
        data.setText(action.getData());
        return view;
    }
}