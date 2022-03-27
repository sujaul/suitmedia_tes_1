package com.galee.core.helper.widget

import android.app.Activity
import android.view.View
import com.galee.core.R
import com.galee.core.util.displaySnakbar
import com.google.android.material.snackbar.Snackbar

class SnackBarCustomImpl(pType:String) : SnackbarToastCustom {
    private val type = pType

    override fun getSnackbar(
        activity: Activity,
        message: String,
        duration: Int,
        label: String,
        listener: (() -> Unit)?
    ): Snackbar {
        when (type) {
            "success" -> return SnackBarSuccess.getSnackbar(activity, message, duration, label, listener)
            "danger" -> return SnackBarError.getSnackbar(activity, message, duration, label, listener)
            "info" -> return SnackBarInfo.getSnackbar(activity, message, duration, label, listener)
        }
        return activity.findViewById<View>(android.R.id.content).displaySnakbar(message, Snackbar.LENGTH_LONG)
    }
}

object SnackBarError : SnackbarToastCustom {
    override val drawableId: Int
        get() = R.drawable.ic_close
    override val colorId: Int
        get() = R.color.red_600
}

object SnackBarInfo : SnackbarToastCustom {
    override val drawableId: Int
        get() = R.drawable.ic_error_outline
    override val colorId: Int
        get() = R.color.blue_500
}

object SnackBarSuccess : SnackbarToastCustom {
    override val drawableId: Int
        get() = R.drawable.ic_done
    override val colorId: Int
        get() = R.color.green_500
}