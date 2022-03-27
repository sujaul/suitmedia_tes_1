package com.test.test_karim.feature.main.fourth

import android.os.Bundle
import android.view.View
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.galee.core.BaseFragment
import com.galee.core.data.BaseResponse
import com.test.test_karim.R
import com.test.test_karim.feature.ItemGuests
import com.test.test_karim.feature.main.MainActivity
import com.test.test_karim.util.NetworkUtil
import com.test.test_karim.util.gone
import com.test.test_karim.util.visible
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.fragment_fourth.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class FourthFragment : BaseFragment() {

    companion object {
        val REQUEST_KEY = "FourthFragment_REQUEST_KEY"
        val GUEST_NAME_KEY = "guest_name"
        val GUEST_ID_KEY = "guest_id"
        const val TAG = "UserDetailFragment"
    }
    private val guestsGroupAdapter = GroupAdapter<GroupieViewHolder>()
    private val navController by lazy { findNavController() }

    private val vmFourth: FourthVM by viewModel()

    override fun onResume() {
        super.onResume()
        val activity = requireActivity() as MainActivity
        activity.setTittel("Guests")
    }

    override fun getTagName(): String = TAG
    override fun layoutId(): Int = R.layout.fragment_fourth
    override fun onCreateUI(view: View, savedInstanceState: Bundle?) {
        init()
    }

    private fun init(){
        setupRecycler()
        observVM()
        if (!NetworkUtil.isNetworkConnected(requireActivity())){
            showToastMessage("info", getString(R.string.error_connection))
            navController.navigateUp()
            return
        }
        vmFourth.getGuestToApi()
    }

    private fun setupRecycler(){

        val recycler = recycler_fourth
        recycler.apply {
            val gridLayout = GridLayoutManager(requireContext(), 2)
            layoutManager = gridLayout
            adapter = guestsGroupAdapter
        }
    }

    private fun showEmptyView(status: Boolean){
        if (isRemoving) return
        if (status) {
            rec_fourth_ll.gone()
            empty_content.visible()
        } else {
            rec_fourth_ll.visible()
            empty_content.gone()
        }
    }

    private fun observVM(){

        vmFourth.getGuests.observe(this, Observer { response ->
            when(response){
                is BaseResponse.Loading-> {
                    showDialogLoading("Get guests data, please wait...")
                }
                is BaseResponse.Success -> {
                    hideDialogLoading()
                    if (guestsGroupAdapter.itemCount > 0)
                        guestsGroupAdapter.clear()
                    response.data.map {
                        guestsGroupAdapter.add(ItemGuests(it){guest, pos ->
                            bundle.putString(GUEST_NAME_KEY, guest.first_name)
                            bundle.putInt(GUEST_ID_KEY, guest.id)
                            setFragmentResult(REQUEST_KEY, bundle)
                            navController.navigateUp()
                        })
                    }
                    if (response.data.isNotEmpty()){
                        showEmptyView(false)
                    } else {
                        showEmptyView(true)
                    }
                }
                is BaseResponse.Error->{
                    hideDialogLoading()
                    showSnackBarMessage("danger", response.message)
                }
            }
        })
    }
}
