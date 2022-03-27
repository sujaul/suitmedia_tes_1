package com.galee.core

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import timber.log.Timber

//abstract class BaseVM(context: Context) : ViewModel() {
//    protected var mContext: Context = context

abstract class BaseVM: ViewModel() {
    abstract fun getTagName(): String
    override fun onCleared() {
        super.onCleared()
        Timber.i("${getTagName()} onCleared() called")
    }
}
