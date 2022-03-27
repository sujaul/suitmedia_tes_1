package com.test.test_karim.util

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.os.Build
import android.telephony.TelephonyManager
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.test.test_karim.R
import java.util.*

object UserInterfaceUtil {
    fun requestFocus(view: View, window : Window) {
        if (view.requestFocus()) {
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        }
    }

    fun showAlertDialog(context : Context) : AlertDialog.Builder{
        val builder: AlertDialog.Builder
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert)
        } else {
            builder = AlertDialog.Builder(context)
        }

        return builder
    }


    fun isSimReady(context: Context): Boolean{
        val telMgr = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val simState = telMgr.getSimState()
        val con: Boolean = when (simState) {
//            TelephonyManager.SIM_STATE_ABSENT ->{false}
//            TelephonyManager.SIM_STATE_NETWORK_LOCKED ->{false }
//            TelephonyManager.SIM_STATE_PIN_REQUIRED ->{}
//            TelephonyManager.SIM_STATE_PUK_REQUIRED ->{}
//            TelephonyManager.SIM_STATE_CARD_IO_ERROR ->{false}
//            TelephonyManager.SIM_STATE_CARD_RESTRICTED->{false}
//            TelephonyManager.SIM_STATE_NOT_READY ->{false}
//            TelephonyManager.SIM_STATE_PERM_DISABLED ->{false}
            TelephonyManager.SIM_STATE_READY ->{true}
//            TelephonyManager.SIM_STATE_UNKNOWN ->{false}
            else -> false
        }
        return con
    }

    fun getToast(activity: Activity, message: String, duration: Int, drawableId: Int, colorId:Int) {
        val toast = Toast(activity.applicationContext)
        toast.duration = duration

        val customView = activity.layoutInflater.inflate(R.layout.toast_icon_text, null)

        customView.findViewById<TextView>(R.id.message).text = message
        customView.findViewById<ImageView>(R.id.icon).setImageResource(drawableId)
        customView.findViewById<CardView>(R.id.parent_view)
            .setCardBackgroundColor(
                ContextCompat.getColor(
                    activity.applicationContext,
                    colorId
                )
            )
        toast.view = customView
        toast.show()
    }

    fun getSnackbar(
        activity: Activity,
        message: String,
        duration: Int,
        drawableId: Int,
        colorId:Int,
        label:String = "Ok",
        listener: (() -> Unit)?= null
    ) {
        val view: View = activity.findViewById(android.R.id.content)
        val snackbar = Snackbar.make(view, "", duration)
        val customView = activity.layoutInflater.inflate(R.layout.snackbar_icon_text, null)
        snackbar.view.setBackgroundColor(Color.TRANSPARENT)

        val snackbarView = snackbar.view as Snackbar.SnackbarLayout
        snackbarView.setPadding(0, 0, 0, 0)

        customView.findViewById<TextView>(R.id.message).text = message
        customView.findViewById<ImageView>(R.id.icon).setImageResource(drawableId)
        customView.findViewById<CardView>(R.id.parent_view)
            .setCardBackgroundColor(
                ContextCompat.getColor(
                    activity.applicationContext,
                    colorId
                )
            )

        customView.findViewById<TextView>(R.id.messageAction).visible()
        customView.findViewById<TextView>(R.id.messageAction).text = label
        customView.findViewById<LinearLayout>(R.id.action_ll).setOnClickListener {
            listener?.invoke()
            if (snackbar.isShown) snackbar.dismiss()
        }

        snackbarView.addView(customView, 0)
        snackbar.show()
    }
}