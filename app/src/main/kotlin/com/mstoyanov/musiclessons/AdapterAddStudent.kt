package com.mstoyanov.musiclessons

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.telephony.PhoneNumberFormattingTextWatcher
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.mstoyanov.musiclessons.model.PhoneNumber
import com.mstoyanov.musiclessons.model.PhoneNumberType

class AdapterAddStudent(var phoneNumbers: MutableList<PhoneNumber>) : RecyclerView.Adapter<AdapterAddStudent.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterAddStudent.ViewHolder {
        val phoneNumberItem = LayoutInflater.from(parent.context).inflate(R.layout.phone_item_add_st, parent, false)
        return ViewHolder(phoneNumberItem)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.number.setText(phoneNumbers[position].number.trim())
        holder.type.setSelection(phoneNumbers[position].type.ordinal)
        holder.delete.setOnClickListener {
            phoneNumbers.remove(phoneNumbers[position])
            notifyDataSetChanged()
            if (phoneNumbers.size == 0) (holder.context as ActivityAddStudent).invalidateOptionsMenu()
        }
    }

    override fun getItemCount(): Int {
        return phoneNumbers.size
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val number: EditText = view.findViewById(R.id.phone_number)
        val type: Spinner = view.findViewById(R.id.phone_number_type)
        val delete: ImageButton = view.findViewById(R.id.delete)
        val context: Context = view.context

        init {
            number.addTextChangedListener(PhoneNumberFormattingTextWatcher())
            number.addTextChangedListener(PhoneNumberTextWatcher())

            val adapter = ArrayAdapter.createFromResource(view.context, R.array.phone_types, R.layout.phone_type_item)
            adapter.setDropDownViewResource(R.layout.phone_type_dropdown_item)
            type.adapter = adapter
            type.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                    phoneNumbers[adapterPosition].type = PhoneNumberType.values()[position]
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // do nothing
                }
            }
        }

        private inner class PhoneNumberTextWatcher : TextWatcher {

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // do nothing
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // do nothing
            }

            override fun afterTextChanged(s: Editable) {
                if ((context as ActivityAddStudent).pristine && s.toString().isNotEmpty()) {
                    context.pristine = false
                    context.invokeFirstNameTextWatcher()
                }
                if (s.toString().trim().isNotEmpty()) {
                    phoneNumbers[adapterPosition].number = s.toString().trim()
                    phoneNumbers[adapterPosition].isValid = true
                    number.error = null
                } else {
                    phoneNumbers[adapterPosition].number = ""
                    phoneNumbers[adapterPosition].isValid = false
                    if (!context.pristine) number.error = context.getResources().getString(R.string.phone_number_error)
                }
                context.invalidateOptionsMenu()
            }
        }
    }
}