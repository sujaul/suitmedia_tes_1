package com.test.test_karim.feature.main.first

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.galee.core.BaseFragment
import com.test.test_karim.R
import com.test.test_karim.feature.main.MainActivity
import kotlinx.android.synthetic.main.fragment_first.*


class FirstFragment : BaseFragment() {

    companion object {
        const val TAG = "FirstFragment"
        val user_name = "user_name"
    }

    private val navController by lazy { findNavController() }

    override fun onResume() {
        super.onResume()
        val activity = requireActivity() as MainActivity
        activity.setTittel("First Screen", false)
    }

    override fun getTagName(): String = TAG
    override fun layoutId(): Int = R.layout.fragment_first
    override fun onCreateUI(view: View, savedInstanceState: Bundle?) {
        init()
    }

    private fun init(){
        btn_next.setOnClickListener {
            openSecondScreen()
        }
    }

    private fun openSecondScreen(){
        bundle.putString(user_name, edt_name.text?.trim().toString())
        navController.navigate(R.id.nav_second,bundle)
    }
}
