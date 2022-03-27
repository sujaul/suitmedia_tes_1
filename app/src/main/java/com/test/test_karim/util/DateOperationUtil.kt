package com.test.test_karim.util

import android.app.TimePickerDialog
import android.content.Context
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


object DateOperationUtil {

    fun getCurrentTimeStr(formatStr: String) : String {
        val format = SimpleDateFormat(formatStr, Locale.US)
        val cal = Calendar.getInstance()
        val tgl = format.format(cal.time)

        return tgl
    }

    fun getCurrentTimeInt(formatStr: String) : Int {
        val tgl = getCurrentTimeStr(formatStr)
        return tgl.dateInFormat(formatStr)?.time?.div(1000)?.toInt() ?: 0
    }

    fun getTimeInt(tgl: String, formatStr: String) : Int {
        return tgl.dateInFormat(formatStr)?.time?.div(1000)?.toInt() ?: 0
    }

    fun getDateStrWithPlusMinusDay(formatStr: String, setDay: Int) : String {
        val format = SimpleDateFormat(formatStr, Locale.US)
        val cal1 = Calendar.getInstance()
        cal1.add(Calendar.DATE, setDay)
        return format.format(cal1.time)
    }

    fun minusDayNowWithSpecificDay(setDay: Int) : Int {
        val cal = Calendar.getInstance()
        val cal1 = Calendar.getInstance()
        cal1.set(Calendar.DATE, setDay)
        return cal.get(Calendar.DAY_OF_WEEK) - cal1.get(Calendar.DAY_OF_WEEK)
    }

    fun getCalPlusMinusDayNow(setDay: Int) : Calendar {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DATE, setDay)
        return calendar
    }

    fun getCalInSpecificDayNow(setDay: Int) : Calendar {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, setDay-1)
        return calendar
    }

    fun getCalPlusMinusDayInSpecificDay(date: Date, setDay: Int) : Calendar {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.add(Calendar.DATE, setDay)
        return calendar
    }

    fun getCalPlusMinusDayLastMonthInSpecificDay(setDay: Int) : Calendar {
        val cal = Calendar.getInstance()
        cal.add(Calendar.MONTH, -1)
        cal.set(Calendar.DAY_OF_MONTH, setDay-1)
        return cal
    }

    fun dateStrFormat(formatInput: String, formatOutput: String, date: String) : String {
        val dateFormat = SimpleDateFormat(formatInput, Locale.US)
        val convertedDate: Date
        try {
            convertedDate = dateFormat.parse(date)
            val newformat = SimpleDateFormat(formatOutput, Locale.US)
            val finalDateString = newformat.format(convertedDate)
            return finalDateString
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return ""
    }

    fun getTimeStr(formatStr: String, date: Date) : String {
        val format = SimpleDateFormat(formatStr, Locale.US)
        val tgl = format.format(date)

        return tgl
    }

    fun getTime(dates: String, dateFormat: String?): String? {
        val date: Date? = dates.dateInFormat(dateFormat!!)
        val timeFormat = SimpleDateFormat(
            dateFormat,
            Locale.US
        )
        date?.let {
            return timeFormat.format(it)
        } ?: return null
    }

    fun defaultTimeFormat(includeHours: Boolean): String {
        return if (includeHours) "HH:mm:ss" else "HH:mm"
    }

    fun openTimePicker(context: Context, tittle: String, time: (String, String) -> Unit){
        val mcurrentTime: Calendar = Calendar.getInstance()
        val hour: Int = mcurrentTime.get(Calendar.HOUR_OF_DAY)
        val minute: Int = mcurrentTime.get(Calendar.MINUTE)
        val second: Int = mcurrentTime.get(Calendar.SECOND)
        val mTimePicker: TimePickerDialog
        mTimePicker = TimePickerDialog(context,
            TimePickerDialog.OnTimeSetListener { timePicker, selectedHour, selectedMinute ->
                time("$selectedHour:$selectedMinute:$second", "$selectedHour:$selectedMinute")
            }, hour, minute, true)
        mTimePicker.setTitle(tittle)
        mTimePicker.show()
    }
}