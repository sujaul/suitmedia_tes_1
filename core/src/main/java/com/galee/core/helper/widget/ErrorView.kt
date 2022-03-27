package com.galee.core.helper.widget

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.RelativeLayout
import com.galee.core.R
import com.galee.core.util.getString
import com.galee.core.util.gone
import com.galee.core.util.visible
import kotlinx.android.synthetic.main.error_view.view.*

class ErrorView : RelativeLayout {
    private var errorListener: ErrorListener? = null

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        init()
    }

    private fun init() {
        gravity = Gravity.CENTER
        LayoutInflater.from(context).inflate(R.layout.error_view, this)
    }

    fun setView(isError: Boolean, message: String?, case:Int? = 0, listener:ErrorListener) {
        lyt_disconnect.visible()
        if (isError) {
            txt_connection.text = message
            img_error.visible()
            lyt_offline.visible()
            when(case){
                0 -> {
                    if (message.isNullOrBlank()) txt_connection.text = getString(R.string.base_error_permission)
                    img_error.setImageResource(R.drawable.ic_block)
                }
                1 -> {
                    if (message.isNullOrBlank()) txt_connection.text = getString(R.string.base_error_no_gps)
                    img_error.setImageResource(R.drawable.ic_gps_off)
                }
                2 -> {
                    if (message.isNullOrBlank()) txt_connection.text = getString(R.string.base_error_connection)
                    img_error.setImageResource(R.drawable.ic_signal_wifi_off)
                }
                3 -> {
                    if (message.isNullOrBlank()) txt_connection.text = getString(R.string.base_error_unknown)
                    img_error.setImageResource(R.drawable.ic_bug_report)
                }
                4 -> {
                    if (message.isNullOrBlank()) txt_connection.text = getString(R.string.base_error_no_data)
                    img_error.gone()
                }
                else -> {
                    if (message.isNullOrBlank()) txt_connection.text = getString(R.string.base_error_unknown)
                    img_error.gone()
                }
            }
            lyt_offline.setOnClickListener {
                listener.onReloadData()
            }
        } else {
            lyt_disconnect.gone()
            lyt_offline.gone()
        }
    }

    interface ErrorListener {
        fun onReloadData()
    }
}