package com.galee.core.helper.widget

import android.app.Activity
import android.widget.Toast
import com.galee.core.R

class ToastCustomImpl(pType: String): SnackbarToastCustom {
    private var type = pType
    override fun getToast(activity: Activity, message: String, duration: Int): Toast {
        when (type) {
            "success" -> return ToastCustomSuccess.getToast(activity, message, duration)
            "danger" -> return ToastCustomError.getToast(activity, message, duration)
            "info" -> return ToastCustomInfo.getToast(activity, message, duration)
        }
        return Toast.makeText(activity.applicationContext, message, duration)
    }
}

object ToastCustomError : SnackbarToastCustom {
    override val drawableId: Int
        get() = R.drawable.ic_close
    override val colorId: Int
        get() = R.color.red_600
}

object ToastCustomInfo : SnackbarToastCustom {
    override val drawableId: Int
        get() = R.drawable.ic_error_outline
    override val colorId: Int
        get() = R.color.blue_600
}

object ToastCustomSuccess : SnackbarToastCustom {
    override val drawableId: Int
        get() = R.drawable.ic_done
    override val colorId: Int
        get() = R.color.green_500
}