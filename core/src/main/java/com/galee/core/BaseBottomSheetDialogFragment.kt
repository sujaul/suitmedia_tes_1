package com.galee.core

import android.app.Activity
import android.app.Dialog
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.core.app.ActivityCompat
import com.galee.core.helper.widget.SnackBarCustomImpl
import com.galee.core.helper.widget.ToastCustomImpl
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.CompositeMultiplePermissionsListener
import com.karumi.dexter.listener.multi.DialogOnAnyDeniedMultiplePermissionsListener
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.multi.SnackbarOnAnyDeniedMultiplePermissionsListener
import timber.log.Timber

abstract class BaseBottomSheetDialogFragment : BottomSheetDialogFragment(),
    BaseImplDialog, BaseImplSnackBar, BaseImplToast, BaseImplPermission {

    abstract fun getTagName(): String
    @LayoutRes
    abstract fun layoutId(): Int
    abstract fun onCreateUI(bottomSheet: BottomSheetDialog, behavior: BottomSheetBehavior<View>, savedInstanceState: Bundle?)

    lateinit var mView: View
    lateinit var bottomSheet: BottomSheetDialog
    lateinit var behavior: BottomSheetBehavior<View>

    private var pDialog: Dialog? = null
    private var snackbar : Snackbar? = null
    protected val bundle = Bundle()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        bottomSheet = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        Timber.i("${getTagName()} onCreateDialog() called")

        mView = View.inflate(context, layoutId(), null)
        bottomSheet.setContentView(mView)

        val parentView = mView.parent as View
        behavior = BottomSheetBehavior.from(parentView)

        /*bottomSheet.setOnShowListener {
            val frameBottom =
                bottomSheet.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            behavior = BottomSheetBehavior.from(frameBottom)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }*/
        return bottomSheet
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Timber.i("${getTagName()} onCreateView() called")
        mView = inflater.inflate(layoutId(), container, false)
        return mView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.i("${getTagName()} onViewCreated() called")
        onCreateUI(bottomSheet, behavior, savedInstanceState)
    }

    override fun showDialogLoading(message: String?) {
        if (pDialog == null) pDialog = Dialog(mView.context)

        pDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        pDialog?.setContentView(R.layout.dialog_loading)
        pDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//        val progressBar = pDialog?.findViewById(R.id.progressBar) as ProgressBar
        val msg = pDialog?.findViewById(R.id.txt_message) as TextView
        if (message != null) msg.text = message
        else msg.text = getString(R.string.base_txt_loading)
        pDialog?.setCancelable(false)
        pDialog?.show()
    }

    override fun hideDialogLoading() {
        if (pDialog != null && pDialog?.isShowing!!) {
            pDialog?.dismiss(); pDialog = null
        }
    }

    override fun showToastMessage(message: String, duration: Int) {
        activity?.let {
            ToastCustomImpl("").getToast(it, message, duration).show()
        } ?: throw Exception("Activity is Null")
    }

    /**
     * @param type => 'success', 'danger', 'info' or '' as String
     * @param message => message of Toast
     * @param duration => duration of Toast
     **/
    override fun showToastMessage(type: String, message: String, duration: Int) {
        activity?.let {
            ToastCustomImpl(type).getToast(it, message, duration).show()
        } ?: throw Exception("Activity is Null")
    }

    override fun showSnackBarMessage(
        message: String,
        duration: Int,
        label: String,
        listener: (() -> Unit)?
    ): Snackbar? {
        dismissSnackBar()
        activity?.let {
            snackbar = SnackBarCustomImpl("").getSnackbar(it, message, duration, label, listener)
            snackbar?.show()
            return snackbar
        }
        return null
    }

    /**
     * @param type => 'success', 'danger', 'info' or '' as String
     * @param message => message of Snackbar
     * @param duration => duration of Snackbar
     **/
    override fun showSnackBarMessage(
        type: String,
        message: String,
        duration: Int,
        label: String,
        listener: (() -> Unit)?
    ): Snackbar? {
        dismissSnackBar()
        activity?.let {
            snackbar = SnackBarCustomImpl(type).getSnackbar(it, message, duration, label, listener)
            snackbar?.show()
            return snackbar
        }
        return null
    }

    override fun dismissSnackBar() {
        snackbar?.let {
            if (it.isShown) it.dismiss()
        }
    }

    protected fun isHasPermission(permissions: MutableList<String>): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            permissions.all { singlePermission ->
                mView.context.checkSelfPermission(singlePermission) == PackageManager.PERMISSION_GRANTED
            }
        else true
    }

    protected fun askPermission(vararg permissions: String, @androidx.annotation.IntRange(from = 0) requestCode: Int) =
        ActivityCompat.requestPermissions(activity as Activity, permissions, requestCode)

    protected fun onRunPermission(permissions: MutableList<String>, level: Int) {
        Timber.i("${getTagName()} onRunPermission() called")
        activity?.let {
            val view = it.findViewById<View>(android.R.id.content)
            Dexter.withActivity(it)
                .withPermissions(permissions)
                .withListener(
                    CompositeMultiplePermissionsListener(
                        SnackbarOnAnyDeniedMultiplePermissionsListener.Builder
                            .with(view, R.string.base_permission_title)
                            .withOpenSettingsButton(R.string.base_permission_btn_text)
                            .withDuration(Snackbar.LENGTH_LONG)
                            .build(),
                        DialogOnAnyDeniedMultiplePermissionsListener.Builder
                            .withContext(mView.context)
                            .withTitle(R.string.base_permission_title)
                            .withMessage(R.string.base_permission_message)
                            .withButtonText(android.R.string.ok)
//                        .withIcon(R.mipmap.ic_logo)
                            .build(),
                        object : MultiplePermissionsListener {
                            override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                                report?.let {
                                    if (it.areAllPermissionsGranted()) {
                                        onAllPermissionGranted(level)
                                    } else {
                                        onDenyPermission(level)
                                    }
                                }
                            }

                            override fun onPermissionRationaleShouldBeShown(
                                permissions: MutableList<PermissionRequest>?,
                                token: PermissionToken?
                            ) {
                                token?.continuePermissionRequest()
                            }
                        }
                    )
                ).onSameThread().check()
        }
    }

    override fun onAllPermissionGranted(level: Int) {
        Timber.i("${getTagName()} onAllPermissionGranted() called")
    }

    override fun onDenyPermission(level: Int) {
        Timber.i("${getTagName()} onDenyPermission() called")
    }
}