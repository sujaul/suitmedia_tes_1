package com.galee.core.helper.widget

import android.app.Activity
import android.graphics.Color
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.galee.core.R
import com.galee.core.util.visible
import com.google.android.material.snackbar.Snackbar

interface SnackbarToastCustom: BaseFieldSnackbarAndToast {
    override val drawableId: Int
        get() = 0
    override val colorId: Int
        get() = 0

    fun getSnackbar(
        activity: Activity,
        message: String,
        duration: Int = Snackbar.LENGTH_LONG,
        label:String,
        listener: (() -> Unit)?= null
    ): Snackbar {
        val view: View = activity.findViewById(android.R.id.content)
        val snackbar = Snackbar.make(view, "", duration)
        val customView = activity.layoutInflater.inflate(R.layout.snackbar_icon_text, null)
        snackbar.view.setBackgroundColor(Color.TRANSPARENT)

        val snackbarView = snackbar.view as Snackbar.SnackbarLayout
        snackbarView.setPadding(0, 0, 0, 0)

        customView.findViewById<TextView>(R.id.message).text = message
        customView.findViewById<ImageView>(R.id.icon).setImageResource(this.drawableId)
        customView.findViewById<CardView>(R.id.parent_view)
            .setCardBackgroundColor(
                ContextCompat.getColor(
                    activity.applicationContext,
                    this.colorId
                )
            )

        label.let {
            customView.findViewById<TextView>(R.id.messageAction).visible()
            customView.findViewById<TextView>(R.id.messageAction).text = it
            customView.findViewById<TextView>(R.id.messageAction).setOnClickListener {
                listener?.invoke()
                if (snackbar.isShown) snackbar.dismiss()
            }
        }

        snackbarView.addView(customView, 0)
        return snackbar
    }

    fun getToast(activity: Activity, message: String, duration: Int = Toast.LENGTH_LONG): Toast {
        val toast = Toast(activity.applicationContext)
        toast.duration = duration

        val customView = activity.layoutInflater.inflate(R.layout.toast_icon_text, null)

        customView.findViewById<TextView>(R.id.message).text = message
        customView.findViewById<ImageView>(R.id.icon).setImageResource(this.drawableId)
        customView.findViewById<CardView>(R.id.parent_view)
            .setCardBackgroundColor(
                ContextCompat.getColor(
                    activity.applicationContext,
                    this.colorId
                )
            )
        toast.view = customView
        return toast
    }
}

interface BaseFieldSnackbarAndToast {
    val drawableId: Int
    val colorId: Int
}