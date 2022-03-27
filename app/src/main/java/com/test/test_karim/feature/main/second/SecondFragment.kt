package com.test.test_karim.feature.main.second

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import com.galee.core.BaseFragment
import com.test.test_karim.R
import com.test.test_karim.feature.main.MainActivity
import com.test.test_karim.feature.main.first.FirstFragment
import com.test.test_karim.feature.main.fourth.FourthFragment
import com.test.test_karim.feature.main.third.ThirdFragment
import kotlinx.android.synthetic.main.fragment_second.*


class SecondFragment : BaseFragment() {

    companion object {
        const val TAG = "SecondFragment"
    }

    private val navController by lazy { findNavController() }

    override fun onResume() {
        super.onResume()
        val activity = requireActivity() as MainActivity
        activity.setTittel("Second Screen", false)
    }

    override fun getTagName(): String = TAG
    override fun layoutId(): Int = R.layout.fragment_second
    override fun onCreateUI(view: View, savedInstanceState: Bundle?) {
        init()
    }

    private fun init(){
        val user_name = requireArguments().getString(FirstFragment.user_name, "")
        txt_name?.text = user_name
        btn_choose_event.setOnClickListener {
            openEventScreen()
        }
        btn_choose_guest.setOnClickListener {
            openGuestScreen()
        }
    }

    private fun openEventScreen(){
        setFragmentResultListener(ThirdFragment.REQUEST_KEY) { key, bundle ->
            val event_name = bundle.getString(ThirdFragment.EVENT_NAME_KEY, "")
            btn_choose_event?.text = event_name
        }
        navController.navigate(R.id.nav_third)
    }

    private fun openGuestScreen(){
        setFragmentResultListener(FourthFragment.REQUEST_KEY) { key, bundle ->
            val guest_name = bundle.getString(FourthFragment.GUEST_NAME_KEY, "")
            btn_choose_guest?.text = guest_name
            val guest_id = bundle.getInt(FourthFragment.GUEST_ID_KEY, 0)
            if (guest_id == 2*3){
                showToastMessage("info", "iOS", Toast.LENGTH_SHORT)
            } else if (guest_id%2 == 0){
                showToastMessage("info", "blackberry", Toast.LENGTH_SHORT)
            } else if (guest_id%3 == 0){
                showToastMessage("info", "android", Toast.LENGTH_SHORT)
            } else {
                showToastMessage("info", "feature phones", Toast.LENGTH_SHORT)
            }
        }
        navController.navigate(R.id.nav_fourth)
    }
}
