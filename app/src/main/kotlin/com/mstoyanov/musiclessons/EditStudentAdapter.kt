package com.mstoyanov.musiclessons

import android.content.Context
import android.os.AsyncTask
import android.support.v7.widget.RecyclerView
import android.telephony.PhoneNumberFormattingTextWatcher
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.mstoyanov.musiclessons.model.PhoneNumber
import com.mstoyanov.musiclessons.model.PhoneNumberType
import java.lang.ref.WeakReference

class EditStudentAdapter(var phoneNumbers: List<PhoneNumber>) : RecyclerView.Adapter<EditStudentAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EditStudentAdapter.ViewHolder {
        val phoneNumberItem = LayoutInflater.from(parent.context).inflate(
                R.layout.phone_item_add_st,
                parent,
                false)
        return ViewHolder(phoneNumberItem)
    }

    override fun onBindViewHolder(holder: EditStudentAdapter.ViewHolder, position: Int) {
        holder.number.setText(phoneNumbers[position].number!!.trim { it <= ' ' })
        holder.type.setSelection(phoneNumbers[position].type!!.ordinal)
        holder.delete.setOnClickListener {
            if (phoneNumbers[position].phoneNumberId > 0) {
                (holder.context as EditStudentActivity).stopProgressBar()
                DeletePhoneNumber(phoneNumbers[position], holder.context).execute()
            }
            phoneNumbers -= phoneNumbers[position]
            if (phoneNumbers.size == 0)
                (holder.context as EditStudentActivity).invalidateOptionsMenu()
            notifyDataSetChanged()
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
                    phoneNumbers[adapterPosition].type = PhoneNumberType.values()[position]
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
                if (s.toString().replace("\\s+".toRegex(), "").trim { it <= ' ' }.isNotEmpty()) {
                    phoneNumbers[adapterPosition].number = s.toString().trim { it <= ' ' }
                    phoneNumbers[adapterPosition].isValid = true
                    number.error = null
                } else {
                    phoneNumbers[adapterPosition].number = ""
                    phoneNumbers[adapterPosition].isValid = false
                    number.error = context.resources.getString(R.string.phone_number_error)
                }
                (context as EditStudentActivity).invalidateOptionsMenu()
            }
        }
    }

    companion object {

        private class DeletePhoneNumber(private val phoneNumber: PhoneNumber, context: Context) : AsyncTask<Void, Int, PhoneNumber>() {
            private val contextWeakReference: WeakReference<Context> = WeakReference(context)

            override fun doInBackground(vararg params: Void): PhoneNumber {
                /*try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }*/
                MusicLessonsApplication.db.phoneNumberDao.delete(phoneNumber)
                return phoneNumber
            }

            override fun onPostExecute(phoneNumber: PhoneNumber) {
                val context = contextWeakReference.get()
                (context as EditStudentActivity).stopProgressBar()
            }
        }
    }
}