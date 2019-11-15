package com.maa.calendarapp.Lib3

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.maa.calendarapp.Lib2.Adapter.DataRow
import com.maa.calendarapp.Lib2.Adapter.RowEvent
import com.maa.calendarapp.R
import kotlinx.android.synthetic.main.event_layout.view.*
import kotlinx.android.synthetic.main.item_data_row.view.*
import kotlinx.android.synthetic.main.item_data_row2.view.*
import java.text.SimpleDateFormat
import java.util.*

class RecyclerAdapter2(var context: Context, var list : MutableList<DataRow>, var interval : Int) : RecyclerView.Adapter<RecyclerAdapter2.Holder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val v = LayoutInflater.from(context).inflate(R.layout.item_data_row2,parent,false)
        return Holder(v)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.Hdebut.text = list.get(position).debut
//        if(list.get(position).events.size>0){
//            val nbSegment = interval / 5
//            list.get(position).events.sortBy { it.h_debut }
//            for(event in list.get(position).events){
//                val layout = LayoutInflater.from(context).inflate(R.layout.event_layout,null,false)
//                layout.eventName.text = event.text
//                layout.eventBackground.setBackgroundColor(Color.parseColor(event.color))
//                layout.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,0,1f)
//                holder.eventContainer.weightSum = nbSegment.toFloat()
//                holder.eventContainer.addView(layout)
//            }
//        }
    }

    fun updateHourEvents(events : MutableList<RowEvent>){
        for(event in events){
//            list.find { it.debut.equals(event.h_debut) ||
//                    (SimpleDateFormat("HH:mm").parse(event.h_debut).after(SimpleDateFormat("HH:mm").parse(it.debut))
//                            &&
//                            SimpleDateFormat("HH:mm").parse(event.h_debut).before(SimpleDateFormat("HH:mm").parse(it.fin)))
//            }?.events?.add(event)

            for(row in list){
                if(row.debut.equals(event.h_debut) ||
                    (SimpleDateFormat("HH:mm").parse(event.h_debut).after(SimpleDateFormat("HH:mm").parse(row.debut)) &&
                            SimpleDateFormat("HH:mm").parse(event.h_debut).before(SimpleDateFormat("HH:mm").parse(row.fin)))){
                    row.events.add(event)
                }
            }
        }
        notifyDataSetChanged()
    }


    inner class Holder(view : View) : RecyclerView.ViewHolder(view){
        val Hdebut : TextView
        init {
            Hdebut = view.Hdebut2
        }
    }
}