package com.galee.core.util

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Point
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.InputFilter
import android.text.TextPaint
import android.text.TextWatcher
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.google.android.material.snackbar.Snackbar
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/** BEGIN TYPE EXTENSION */
/**
 * Extension method to cast a char with a decimal value to an [Int].
 */
fun Char.decimalValue(): Int {
    if (!isDigit())
        throw IllegalArgumentException("Out of range")
    return this.toInt() - '0'.toInt()
}

fun String.ucWords(): String {
    return this.toLowerCase(Locale.getDefault()).split(' ').joinToString(" ") {
        it.capitalize()
    }
}

fun String.dateInFormat(format: String): Date? {
    val dateFormat = SimpleDateFormat(format, Locale.US)
    var parsedDate: Date? = null
    try {
        parsedDate = dateFormat.parse(this)
    } catch (ignored: ParseException) {
        ignored.printStackTrace()
    }
    return parsedDate
}

fun Long.getFormattedTimeEvent(): String {
    val newFormat = SimpleDateFormat("kk:mm", Locale.US)
    return newFormat.format(Date(this))
}

fun Long.getFormattedDateEvent(): String {
    val newFormat = SimpleDateFormat("EEE, dd MMM yyyy", Locale.US)
    return newFormat.format(Date(this))
}

/**
 * To Reduce Decimal Format
 **/
fun Double.format(fracDigits: Int): String {
    val symbol = DecimalFormatSymbols()
    symbol.decimalSeparator = '.'
    val df = DecimalFormat("0.0000000", symbol)
    df.maximumFractionDigits = fracDigits
    return df.format(this)
}

/**
 * Extension method to int time to 2 digit String
 */
fun Int.twoDigitTime() = if (this < 10) "0" + toString() else toString()

/**
 * Extension method used to return the value of the specified float raised to the power
 * of the specified [exponent].
 */
fun Float.pow(exponent: Float) = Math.pow(this.toDouble(), exponent.toDouble()).toFloat()

/**
 * Convert a [Boolean] value to a view visibility [Int].
 */
fun Boolean.toViewVisibility(valueForFalse: Int = View.GONE): Int {
    return if (this) {
        View.VISIBLE
    } else {
        valueForFalse
    }
}
/** END TYPE EXTENSION */

/** BEGIN VIEW EXTENSION */

/**
 * Extension method to provide simpler access to {@link View#getResources()#getString(int)}.
 */
fun View.getString(stringResId: Int): String = resources.getString(stringResId)

/**
 * Extension method to provide show keyboard for View.
 */
fun View.showKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    this.requestFocus()
    imm.showSoftInput(this, 0)
}

/**
 * Extension method to provide hide keyboard for [View].
 */
fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}

/**
 * Extension method to provide quicker access to the [LayoutInflater] from a [View].
 */
fun View.getLayoutInflater() = context.getLayoutInflater()

/**
 * Extension method use to display a [Snackbar] message to the user.
 */
fun View.displaySnakbar(message: String, duration: Int = Snackbar.LENGTH_SHORT): Snackbar {
    val snakbar = Snackbar.make(this, message, duration)
    snakbar.show()
    return snakbar
}

/**
 * Extension method use to display a [Snackbar] message to the user.
 */
fun View.displaySnakbar(messageResId: Int, duration: Int = Snackbar.LENGTH_SHORT): Snackbar {
    val snakbar = Snackbar.make(this, messageResId, duration)
    snakbar.show()
    return snakbar
}

/**
 * Extension method to return the view location on screen as a [Point].
 */
fun View.locationOnScreen(): Point {
    val location = IntArray(2)
    getLocationOnScreen(location)
    return Point(location[0], location[1])
}

/**
 * Extension method to return the view location in window as a [Point].
 */
fun View.locationInWindow(): Point {
    val location = IntArray(2)
    getLocationInWindow(location)
    return Point(location[0], location[1])
}

fun View.gone() {
    if (visibility != View.GONE) {
        visibility = View.GONE
    }
}

fun View.visible() {
    if (visibility != View.VISIBLE) {
        visibility = View.VISIBLE
    }
}

fun View.invisible() {
    if (visibility != View.INVISIBLE) {
        visibility = View.INVISIBLE
    }
}

/**
 * Method used to easily retrieve display size from [View].
 */
fun View.getDisplaySize() = context.getDisplaySize()

/**
 * Extension method to set width for View.
 */
fun View.setWidth(value: Int) {
    val lp = layoutParams
    lp?.let {
        lp.width = value
        layoutParams = lp
    }
}

fun getScreenWidth(): Int {
    return Resources.getSystem().displayMetrics.widthPixels
}

fun getScreenHeight(): Int {
    return Resources.getSystem().displayMetrics.heightPixels
}

fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object: TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            afterTextChanged.invoke(s.toString())
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
    })
}

fun EditText.validate(message: String, validator: (String) -> Boolean) {
    this.afterTextChanged {
        this.error = if (validator(it)) null else message
    }
    this.error = if (validator(this.text.toString())) null else message
}

/**
 * Created by shivam on 8/7/17.
 */
fun EditText.autocapitalize() {
    filters += InputFilter.AllCaps()
}

/**
 * Extension method to replace all text inside an [Editable] with the specified [newValue].
 */
fun Editable.replaceAll(newValue: String) {
    replace(0, length, newValue)
}

/**
 * Extension method to replace all text inside an [Editable] with the specified [newValue] while
 * ignoring any [android.text.InputFilter] set on the [Editable].
 */
fun Editable.replaceAllIgnoreFilters(newValue: String) {
    val currentFilters = filters
    filters = emptyArray()
    replaceAll(newValue)
    filters = currentFilters
}

/**
 * Extension method to provide show Menu Item for BottomNavigationView.
 */
fun MenuItem.visible() {
    if (!isVisible) {
        isVisible = true
    }
}

/**
 * Extension method to provide gone Menu Item for BottomNavigationView.
 */
fun MenuItem.gone() {
    if (!isVisible) {
        isVisible = false
    }
}
/** END VIEW EXTENSION */

/** BEGIN CONTEXT EXTENSION */
/**
 * Extension method to provide simpler access to {@link ContextCompat#getColor(int)}.
 */
fun Context.getColorCompat(color: Int) = ContextCompat.getColor(this, color)

/**
 * Extension method to provide simpler access to {@link ContextCompat#getDrawableCompat(int)}.
 */
fun Context.getDrawableCompat(drawableResId: Int): Drawable? = ContextCompat
    .getDrawable(this, drawableResId)

/**
 * Extension method to provide quicker access to the [LayoutInflater] from [Context].
 */
fun Context.getLayoutInflater() = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

/**
 * Method used to easily retrieve [WindowManager] from [Context].
 */
fun Context.getWindowManager() = getSystemService(Context.WINDOW_SERVICE) as WindowManager

/**
 * Method used to easily retrieve display size from [Context].
 */
fun Context.getDisplaySize() = Point().apply {
    getWindowManager().defaultDisplay.getSize(this)
}

/**
 * Extension method to display Width for Context.
 */
fun Context.displayWidth(): Int = getDisplaySize().x

/**
 * Retrieve a decoded bitmap from resources, or null if the image could not be decoded.
 */
fun Context.decodeBitmap(resId: Int): Bitmap? = BitmapFactory.decodeResource(resources, resId)
/** END CONTEXT EXTENSION */

/** BEGIN ACTIVITY EXTENSION */
/**
 * Extension method to provide hide keyboard for [Activity].
 */
fun Activity.hideSoftKeyboard() {
    if (currentFocus != null) {
        val inputMethodManager = getSystemService(
            Context
                .INPUT_METHOD_SERVICE
        ) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
    } else{
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
    }
}

fun Activity.hideKeyboard() {
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(window.decorView.windowToken, 0)
}

/**
 * Return whether Keyboard is currently visible on screen or not.
 *
 * @return true if keyboard is visible.
 */
fun Activity.isKeyboardVisible(): Boolean {
    val r = Rect()

    //r will be populated with the coordinates of your view that area still visible.
    window.decorView.getWindowVisibleDisplayFrame(r)

    //get screen height and calculate the difference with the usable area from the r
    val height = getDisplaySize().y
    val diff = height - r.bottom

    // If the difference is not 0 we assume that the keyboard is currently visible.
    return diff != 0
}
/** END ACTIVITY EXTENSION */

/** BEGIN FRAGMENT EXTENSION */
/**
 * Extension method to provide hide keyboard for [Fragment].
 */
fun Fragment.hideSoftKeyboard() {
    activity?.hideSoftKeyboard()
}

/**
 *  Extension method to provide simpler access to {@link ContextCompat#getColor(int)}
 *  from a [Fragment].
 */
fun Fragment.getColorCompat(color: Int) = context?.getColorCompat(color)

/**
 * Extension method to provide simpler access to {@link ContextCompat#getDrawableCompat(int)}
 * from a [Fragment].
 */
fun Fragment.getDrawableCompat(drawableResId: Int) = context?.getDrawableCompat(drawableResId)!!

/**
 * Extension method to be used as the body for functions that are not yet implemented, which will
 * display a [Toast] with the specified [message].
 */
fun Fragment.NOT_IMPL(message: String = "This action is not implemented yet!") {
    TOAST(message)
}

/**
 * Extension method used to display a [Toast] message to the user.
 */
fun Fragment.TOAST(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(context, message, duration).show()
}

/**
 * Extension method used to display a [Toast] message to the user.
 */
fun Fragment.TOAST(messageResId: Int, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(context, messageResId, duration).show()
}

/**
 * Provides simpler access to the [ViewTreeObserver] inside a fragment.
 *
 * @return the [ViewTreeObserver] of the [Activity] this fragment currently attached to, or null
 * if the fragment is detached.
 */
fun Fragment.getViewTreeObserver() = activity?.window?.decorView?.viewTreeObserver
/** END FRAGMENT EXTENSION */

fun getClickableSpan(color: Int, action: (view: View) -> Unit): ClickableSpan {
    return object : ClickableSpan() {
        override fun onClick(view: View) {
            action(view)
        }
        override fun updateDrawState(ds: TextPaint) {
            super.updateDrawState(ds)
            ds.color = color
        }
    }
}

/**
 * Provide the ability to snap to a specified [position] in the specified [recyclerView]
 * using [SnapHelper].
 */
fun SnapHelper.snapToPosition(recyclerView: RecyclerView, position: Int) {
    recyclerView.apply {
        val view = findViewHolderForAdapterPosition(position)?.itemView
        val snapPositions = view?.let {
            layoutManager?.let { it1 -> calculateDistanceToFinalSnap(it1, it) }
        }

        snapPositions?.let { smoothScrollBy(it[0], it[1]) }
    }
}