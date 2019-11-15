package com.maa.calendarapp.Lib3

import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.model.CalendarMonth
import com.kizitonwose.calendarview.model.DayOwner
import com.kizitonwose.calendarview.ui.DayBinder
import com.kizitonwose.calendarview.ui.MonthHeaderFooterBinder
import com.kizitonwose.calendarview.ui.MonthScrollListener
import com.maa.calendarapp.Lib2.Adapter.DataRow
import com.maa.calendarapp.Lib2.Adapter.RecyclerAdapter
import com.maa.calendarapp.Lib2.Adapter.RowEvent
import com.maa.calendarapp.Lib2.DayViewContainer
import com.maa.calendarapp.Lib2.OnClicked
import com.maa.calendarapp.R
import g9.android.orca.com.g9mobile.agenda.ViewContainer.MonthHeaderView
import kotlinx.android.synthetic.main.activity_lib2.*
import kotlinx.android.synthetic.main.activity_lib3.*
import kotlinx.android.synthetic.main.activity_lib3.addEvents
import kotlinx.android.synthetic.main.activity_lib3.interval
import kotlinx.android.synthetic.main.activity_lib3.intervalOK
import kotlinx.android.synthetic.main.activity_lib3.monthName
import kotlinx.android.synthetic.main.activity_lib3.recycler
import org.threeten.bp.LocalDate
import org.threeten.bp.YearMonth
import org.threeten.bp.temporal.WeekFields
import java.text.SimpleDateFormat
import java.util.*

class LibActivity3 : AppCompatActivity(),OnClicked {

    var selectedDate : LocalDate? = null
    var adapter : RecyclerAdapter2? =null
    var loopResetCount = 0
    var INTERVAL : Int = 10
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lib3)



        calendarLib3.isNestedScrollingEnabled = true
        recycler.isNestedScrollingEnabled=true
        calendarLib3.dayBinder = object : DayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view,this@LibActivity3)

            override fun bind(container: DayViewContainer, day: CalendarDay) {
                container.day = day
                container.dayNb.text = day.date.dayOfMonth.toString()
                container.dayName.text = day.date.dayOfWeek.name
                container.month.text = day.date.month.name.substring(0,2)

                container.dayContainer.setBackgroundResource(if (selectedDate == day.date) R.drawable.background_selected else
                    0)
            }
        }
        calendarLib3.monthScrollListener = object : MonthScrollListener{
            override fun invoke(p1: CalendarMonth) {
                monthName.text = p1.yearMonth.month.name + " " + p1.yearMonth.year
                generateGridViewItems()
            }
        }
        calendarLib3.monthHeaderBinder = object : MonthHeaderFooterBinder<MonthHeaderView> {
            override fun create(view: View): MonthHeaderView {return MonthHeaderView(view)
            }
            override fun bind(header: MonthHeaderView, month: CalendarMonth) {}
        }
        calendarLib3.monthFooterBinder = object : MonthHeaderFooterBinder<MonthHeaderView> {
            override fun create(view: View): MonthHeaderView { return MonthHeaderView(view)
            }
            override fun bind(header: MonthHeaderView, month: CalendarMonth) {}
        }

        val currentMonth = YearMonth.now()
        val firstMonth = currentMonth.minusMonths(10)
        val lastMonth = currentMonth.plusMonths(10)
        val firstDayOfWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek


        calendarLib3.setup(firstMonth, lastMonth, firstDayOfWeek)
        calendarLib3.scrollToMonth(currentMonth)

        generateHours(10)

        recycler.layoutManager = LinearLayoutManager(this@LibActivity3)
        adapter = RecyclerAdapter2(this@LibActivity3,generateHours(10),INTERVAL)
        recycler.adapter = adapter

        intervalOK.setOnClickListener {
            INTERVAL = interval.text.toString().toInt()
            adapter = RecyclerAdapter2(this@LibActivity3,generateHours(interval.text.toString().toInt()),
                INTERVAL)
            recycler.adapter = adapter
        }

        addEvents.setOnClickListener {
            adapter?.updateHourEvents(generateEvents())
        }


       gridView.layoutManager = GridLayoutManager(this@LibActivity3,7)


    }

    fun setGridView(list : MutableList<RowEvent>){
        gridView.adapter = GridViewAdp(this@LibActivity3,list,INTERVAL,adapter)
    }

    fun generateGridViewItems() {
        var result = mutableListOf<RowEvent>()
        if(adapter!=null){
            for(row in adapter!!.list){
                val h_debut = row.debut
                val h_fin = row.fin
                var i=0
                var firstDay = calendarLib3.findFirstVisibleDay()
                var day = firstDay?.date
                while(i<7){
                    if(day!=null) {
                        val ev = getEventWithHourMinuteDate(h_debut, h_fin, day)
                        if(ev.size==0){
                            result.add(RowEvent(h_debut,h_fin,day.toString(),"","#ffffff"))
                        }else{
                            result.addAll(ev)
                        }
                        day = day.plusDays(1)
                        i++
                    }
                    else if(loopResetCount<2) {
                        Handler(Looper.getMainLooper()).postDelayed(
                            {
                                generateGridViewItems()
                            }, 2000
                        )
                        loopResetCount++
                        return
                    }
                }
            }
        }
        setGridView(result)
    }

    fun getEventWithHourMinuteDate(h_debut : String,h_fin : String,day : LocalDate) : MutableList<RowEvent>{
        val result = mutableListOf<RowEvent>()
        for(event in generateEvents()){
            val calendar = Calendar.getInstance()
            calendar.time = SimpleDateFormat("dd/MM/yyyy").parse(event.date)
            val comp1 = calendar.get(Calendar.MONTH)
            val comp2 = calendar.get(Calendar.DAY_OF_MONTH)
            val comp3 = day.month.value
            val comp4 = day.dayOfMonth
            if(
                (event.h_debut.equals(h_debut) ||
                        (SimpleDateFormat("HH:mm").parse(event.h_debut).after(SimpleDateFormat("HH:mm").parse(h_debut)) &&
                SimpleDateFormat("HH:mm").parse(event.h_debut).before(SimpleDateFormat("HH:mm").parse(h_fin))
                        )) &&
                (calendar.get(Calendar.MONTH)+1 == day.month.value
                        && calendar.get(Calendar.DAY_OF_MONTH) == day.dayOfMonth)
                    ){
                result.add(event)
            }
        }
        return result
    }


    override fun onViewClicked(view: View, day: CalendarDay) {
        if(day.owner == DayOwner.THIS_MONTH){
            val oldDate = selectedDate
            selectedDate = day.date
            calendarLib3.notifyDayChanged(day)
            oldDate?.let { calendarLib3.notifyDateChanged(it) }
        }
    }

    fun generateHours(interval : Int) : MutableList<DataRow> {
        val hours = mutableListOf<DataRow>()

        for(hour in 0..23) {
            for (minute in 0..59 step interval) {
                val h = if (hour <= 9) "0" + hour.toString() else hour.toString()
                val m = if (minute <= 9) "0" + minute.toString() else minute.toString()

                var fin = minute+interval
                if(fin>=60)
                    fin = fin - (fin-60+1)
                val mFin = if (fin <= 9) "0" + fin.toString() else fin.toString()
                hours.add(DataRow(h + ":" + m,h+":"+mFin,mutableListOf<RowEvent>()))
            }
        }

        return hours
    }

    fun generateEvents() : MutableList<RowEvent>{
        val events = mutableListOf<RowEvent>()
        events.add(RowEvent("00:05","00:10","02/11/2019","Réservation 2","#ff00ff"))
        events.add(RowEvent("00:05","00:10","03/11/2019","Réservation 2","#ff00ff"))

        events.add(RowEvent("00:20","00:20","05/11/2019","Réservation 2","#ff00ff"))
        events.add(RowEvent("00:30","00:30","07/11/2019","Réservation 2","#ff00ff"))
//        events.add(RowEvent("00:00","00:05","Réservation 1","#ffff00"))
//
//        events.add(RowEvent("01:00","01:10","Réservation 3","#ff4444"))
//        events.add(RowEvent("02:00","02:10","Réservation 4","#884854"))
//
//        events.add(RowEvent("01:00","01:10","Réservation 3","#ff4444"))
//        events.add(RowEvent("02:00","02:10","Réservation 4","#884854"))
//
//        events.add(RowEvent("01:00","01:10","Réservation 3","#ff4444"))
//        events.add(RowEvent("02:00","02:10","Réservation 4","#884854"))
//        events.add(RowEvent("01:00","01:10","Réservation 3","#ff4444"))
//        events.add(RowEvent("02:00","02:10","Réservation 4","#884854"))
//        events.add(RowEvent("01:00","01:10","Réservation 3","#ff4444"))
//        events.add(RowEvent("02:00","02:10","Réservation 4","#884854"))
//        events.add(RowEvent("01:00","01:10","Réservation 3","#ff4444"))
//        events.add(RowEvent("02:00","02:10","Réservation 4","#884854"))
//        events.add(RowEvent("01:00","01:10","Réservation 3","#ff4444"))
//        events.add(RowEvent("02:00","02:10","Réservation 4","#884854"))
        return events
    }

    override fun onResume() {
        super.onResume()
        generateGridViewItems()
    }
}
