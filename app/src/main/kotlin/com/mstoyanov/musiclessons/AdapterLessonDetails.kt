package com.mstoyanov.musiclessons

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.mstoyanov.musiclessons.model.PhoneNumber

class AdapterLessonDetails(private val phoneNumbers: List<PhoneNumber>, private val context: Context) : RecyclerView.Adapter<AdapterLessonDetails.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterLessonDetails.ViewHolder {
        val phoneNumberItem = LayoutInflater.from(parent.context).inflate(
                R.layout.phone_item_st_details,
                parent,
                false)
        return AdapterLessonDetails.ViewHolder(phoneNumberItem)
    }

    override fun onBindViewHolder(holder: AdapterLessonDetails.ViewHolder, position: Int) {
        holder.number.text = phoneNumbers[position].number
        holder.type.text = phoneNumbers[position].type.displayValue()
        if (phoneNumbers[position].type.displayValue().equals("cell", ignoreCase = true)) {
            holder.sms.visibility = View.VISIBLE
            holder.number.setPadding(0, 0, dpToPx(16), 0)
        }
    }

    override fun getItemCount(): Int {
        return phoneNumbers.size
    }

    private fun dpToPx(dp: Int): Int {
        val displayMetrics = context.resources.displayMetrics
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT))
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val number: TextView = view.findViewById(R.id.phone_number)
        val type: TextView = view.findViewById(R.id.phone_number_type)
        val sms: ImageView = view.findViewById(R.id.sms)

        init {
            number.setOnClickListener { (view.context as ActivityLessonDetails).dial(number.text.toString()) }
            sms.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + number.text.toString()))
                view.context.startActivity(intent)
            }
        }
    }
}