package com.mstoyanov.musiclessons;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.mstoyanov.musiclessons.model.PhoneNumber;
import com.mstoyanov.musiclessons.model.PhoneNumberType;

import java.util.List;

public class AddStudentAdapter extends RecyclerView.Adapter<AddStudentAdapter.ViewHolder> {
    private static List<PhoneNumber> phoneNumbers;

    AddStudentAdapter(List<PhoneNumber> phoneNumbers) {
        AddStudentAdapter.phoneNumbers = phoneNumbers;
    }

    @Override
    public AddStudentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View phoneNumberItem = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.phone_item_add_st,
                parent,
                false);
        return new ViewHolder(phoneNumberItem);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.number.setText(phoneNumbers.get(position).getNumber().trim());
        holder.type.setSelection(phoneNumbers.get(position).getType().ordinal());
        holder.delete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                phoneNumbers.remove(position);
                if (phoneNumbers.size() == 0)
                    ((AddStudentActivity) holder.context).invalidateOptionsMenu();
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return phoneNumbers.size();
    }

    @Override
    public long getItemId(int position) {
        // needed for the item animator bug
        return phoneNumbers.get(position).getStudentId();
    }

    public List<PhoneNumber> getPhoneNumbers() {
        return phoneNumbers;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private EditText number;
        private Spinner type;
        private ImageButton delete;
        private Context context;

        ViewHolder(final View view) {
            super(view);
            context = view.getContext();

            number = view.findViewById(R.id.phone_number);
            number.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
            number.addTextChangedListener(new PhoneNumberTextWatcher());

            delete = view.findViewById(R.id.delete);

            type = view.findViewById(R.id.phone_number_type);
            final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                    view.getContext(),
                    R.array.phone_types,
                    R.layout.phone_type_item);
            adapter.setDropDownViewResource(R.layout.phone_type_dropdown_item);
            type.setAdapter(adapter);
            type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    phoneNumbers.get(getAdapterPosition()).setType(PhoneNumberType.values()[position]);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    // do nothing
                }
            });
        }

        private class PhoneNumberTextWatcher implements TextWatcher {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // do nothing
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // do nothing
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (((AddStudentActivity) context).isPristine() && s.length() > 0) {
                    ((AddStudentActivity) context).setPristine(false);
                    ((AddStudentActivity) context).invokeTextChanged();
                }
                if (s.toString().replaceAll("\\s+", "").length() > 0) {
                    phoneNumbers.get(getAdapterPosition()).setNumber(s.toString().trim());
                    phoneNumbers.get(getAdapterPosition()).setValid(true);
                    number.setError(null);
                } else {
                    phoneNumbers.get(getAdapterPosition()).setNumber("");
                    phoneNumbers.get(getAdapterPosition()).setValid(false);
                    if (!((AddStudentActivity) context).isPristine())
                        number.setError(context.getResources().getString(R.string.phone_number_error));
                }
                ((AddStudentActivity) context).invalidateOptionsMenu();
            }
        }
    }
}