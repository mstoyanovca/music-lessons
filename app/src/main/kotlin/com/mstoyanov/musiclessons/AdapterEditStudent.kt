package com.mstoyanov.musiclessons

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import androidx.recyclerview.widget.RecyclerView
import com.mstoyanov.musiclessons.global.Functions.formatPhoneNumber
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
            notifyItemRemoved(position)
            if (phoneNumbers.size == 0) (holder.context as ActivityEditStudent).invalidateOptionsMenu()
        }
    }

    override fun getItemCount(): Int {
        return phoneNumbers.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val number: EditText = view.findViewById(R.id.phone_number)
        val type: Spinner = view.findViewById(R.id.phone_number_type)
        val delete: ImageButton = view.findViewById(R.id.delete)
        val context: Context = view.context

        init {
            number.addTextChangedListener(PhoneNumberTextWatcher())

            val adapter = ArrayAdapter.createFromResource(
                view.context,
                R.array.phone_types,
                R.layout.phone_type_item
            )
            adapter.setDropDownViewResource(R.layout.phone_type_dropdown_item)
            type.adapter = adapter
            type.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                    phoneNumbers[bindingAdapterPosition].type = PhoneNumberType.entries.toTypedArray()[position]
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // do nothing
                }
            }
        }

        private inner class PhoneNumberTextWatcher : TextWatcher {
            private var ignore: Boolean = false

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // do nothing
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // do nothing
            }

            override fun afterTextChanged(s: Editable) {
                if (ignore) return
                if (s.isNotEmpty()) {
                    ignore = true
                    s.replace(0, s.length, formatPhoneNumber(s))
                    ignore = false

                    phoneNumbers[bindingAdapterPosition].number = s.toString()
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
