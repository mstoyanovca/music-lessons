package com.mstoyanov.musiclessons

import android.content.Context
import android.telephony.PhoneNumberFormattingTextWatcher
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.mstoyanov.musiclessons.model.PhoneNumber
import com.mstoyanov.musiclessons.model.PhoneNumberType

class AdapterEditStudent(var phoneNumbers: MutableList<PhoneNumber>) : RecyclerView.Adapter<AdapterEditStudent.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterEditStudent.ViewHolder {
        val phoneNumberItem = LayoutInflater.from(parent.context).inflate(R.layout.phone_item_add_st, parent, false)
        return ViewHolder(phoneNumberItem)
    }

    override fun onBindViewHolder(holder: AdapterEditStudent.ViewHolder, position: Int) {
        holder.number.setText(phoneNumbers[position].number.trim())
        holder.type.setSelection(phoneNumbers[position].type.ordinal)
        holder.delete.setOnClickListener {
            phoneNumbers.remove(phoneNumbers[position])
            notifyDataSetChanged()
            if (phoneNumbers.size == 0) (holder.context as ActivityEditStudent).invalidateOptionsMenu()
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
            number.addTextChangedListener(NumberTextWatcher())

            val adapter = ArrayAdapter.createFromResource(
                    view.context,
                    R.array.phone_types,
                    R.layout.phone_type_item)
            adapter.setDropDownViewResource(R.layout.phone_type_dropdown_item)
            type.adapter = adapter
            type.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                    phoneNumbers[bindingAdapterPosition].type = PhoneNumberType.values()[position]
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // do nothing
                }
            }
        }

        private inner class NumberTextWatcher : TextWatcher {

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // do nothing
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // do nothing
            }

            override fun afterTextChanged(s: Editable) {
                if (s.toString().trim().isNotEmpty()) {
                    phoneNumbers[bindingAdapterPosition].number = s.toString().trim()
                    phoneNumbers[bindingAdapterPosition].isValid = true
                    number.error = null
                } else {
                    phoneNumbers[bindingAdapterPosition].number = ""
                    phoneNumbers[bindingAdapterPosition].isValid = false
                    number.error = context.resources.getString(R.string.phone_number_error)
                }
                (context as ActivityEditStudent).invalidateOptionsMenu()
            }
        }
    }
}
