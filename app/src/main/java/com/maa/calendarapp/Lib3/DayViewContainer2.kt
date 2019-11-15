package com.maa.calendarapp.Lib3

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.ui.ViewContainer
import com.maa.calendarapp.Lib2.OnClicked
import kotlinx.android.synthetic.main.calendar_day_2.view.*
import kotlinx.android.synthetic.main.calendar_day_layout.view.*
import kotlinx.android.synthetic.main.calendar_day_layout.view.calendarDay
import kotlinx.android.synthetic.main.calendar_day_layout.view.calendarDayMonth
import kotlinx.android.synthetic.main.calendar_day_layout.view.calendarDayName
import kotlinx.android.synthetic.main.calendar_day_layout.view.dayContainer

class DayViewContainer2(view: View, onClicked: OnClicked) : ViewContainer(view) {
    val dayNb = view.calendarDay
    //val month = view.calendarDayMonth
    val dayName = view.calendarDayName
    val dayContainer = view.dayContainer
    val dayRecycler = view.dayRV

    lateinit var day : CalendarDay


    init {
        dayRecycler.layoutManager = LinearLayoutManager(view.context)
        view.setOnClickListener {
            onClicked.onViewClicked(it,day)
        }
    }
}