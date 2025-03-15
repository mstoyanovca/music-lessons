package com.mstoyanov.musiclessons

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mstoyanov.musiclessons.model.PhoneNumber
import kotlin.math.roundToInt

class AdapterStudentDetails(private val phoneNumbers: List<PhoneNumber>, private val context: Context) : RecyclerView.Adapter<AdapterStudentDetails.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val phoneNumberItem = LayoutInflater.from(parent.context).inflate(
            R.layout.phone_item_st_details,
            parent,
            false
        )
        return ViewHolder(phoneNumberItem)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.number.text = phoneNumbers[position].number
        holder.type.text = phoneNumbers[position].type.displayValue()
        if (phoneNumbers[position].type.displayValue().equals("cell", ignoreCase = true)) {
            holder.sms.visibility = View.VISIBLE
            holder.number.setPadding(0, 0, dpToPx(), 0)
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

    private fun dpToPx(): Int {
        val dp = 16
        val displayMetrics = context.resources.displayMetrics
        return (dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)).roundToInt()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val number: TextView = view.findViewById(R.id.phone_number)
        val type: TextView = view.findViewById(R.id.phone_number_type)
        val sms: ImageView = view.findViewById(R.id.sms)
        val context: Context = view.context

        init {
            number.setOnClickListener { (context as ActivityStudentDetails).dial(number.text.toString()) }
            sms.setOnClickListener {
                val intent = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("smsto:" + number.text.toString())
                    putExtra("type", "text/plain")
                }
                view.context.startActivity(intent)
            }
        }
    }
}
