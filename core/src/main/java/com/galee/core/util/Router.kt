package com.galee.core.util

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.galee.core.Constant

object Router{

    fun navigateModule(activity: Activity, className:String, bundle:Bundle?, isFinish: Boolean){
        try {
            val intent = Intent()
            intent.setClassName(Constant.APP_ID, className)
            bundle?.let { intent.putExtras(it) }
            activity.startActivity(intent)
            if (isFinish) activity.finish()
        } catch (e:Exception){
            println(e)
            Toast.makeText(activity.applicationContext, "Module Not Found", Toast.LENGTH_SHORT).show()
        }
    }

    fun navigateModule(activity: Activity, className: String, isFinish: Boolean){
        navigateModule(activity, className, null, isFinish)
    }
}