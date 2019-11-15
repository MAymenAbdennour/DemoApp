package com.maa.calendarapp.Lib2.Adapter

data class DataRow(
    var debut : String,
    var fin : String,
    var events : MutableList<RowEvent>
)