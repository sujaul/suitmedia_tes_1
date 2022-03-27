package com.test.test_karim.feature.main.third

import android.os.Bundle
import android.view.View
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.galee.core.BaseFragment
import com.test.test_karim.R
import com.test.test_karim.data.model.Events
import com.test.test_karim.feature.ItemEvent
import com.test.test_karim.feature.main.MainActivity
import com.test.test_karim.util.DateOperationUtil
import com.test.test_karim.util.gone
import com.test.test_karim.util.visible
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.fragment_third.*


class ThirdFragment : BaseFragment() {

    companion object {
        val REQUEST_KEY = "ThirdFragment_REQUEST_KEY"
        val EVENT_NAME_KEY = "event_name"
        const val TAG = "ThirdFragment"
    }

    private val repoGroupAdapter = GroupAdapter<GroupieViewHolder>()
    private val navController by lazy { findNavController() }
    private val imgList = arrayListOf(
        "https://image.shutterstock.com/image-vector/upcoming-events-isolated-on-white-260nw-1538520572.jpg",
        "https://image.shutterstock.com/image-vector/events-colorful-typography-banner-260nw-1356206768.jpg",
        "https://thumbs.dreamstime.com/b/events-calendar-icon-premium-red-tag-sign-isolated-abstract-illustration-127882704.jpg",
        "https://mbcparks-rec.org/wp-content/uploads/2019/05/events.jpg")

    override fun onResume() {
        super.onResume()
        val activity = requireActivity() as MainActivity
        activity.setTittel("Events")
    }

    override fun getTagName(): String = TAG
    override fun layoutId(): Int = R.layout.fragment_third
    override fun onCreateUI(view: View, savedInstanceState: Bundle?) {
        init()
    }

    private fun init(){
        setupRecycler()
        val listEvent = ArrayList<Events>()
        for (i in 1..4){
            val events = Events()
            events.id = i
            events.name = "Dummy Event $i"
            events.date = DateOperationUtil.getDateStrWithPlusMinusDay("dd MMMM, yyyy", i)
            events.image = imgList[i-1]
            listEvent.add(events)
            repoGroupAdapter.add(ItemEvent(events){event, pos ->
                bundle.putString(EVENT_NAME_KEY, event.name)
                setFragmentResult(REQUEST_KEY, bundle)
                navController.navigateUp()
            })
        }
        showEmptyView(listEvent.isEmpty())
    }

    private fun setupRecycler(){
        val recycler = recycler_third
        recycler.apply {
            val linearLayout = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.VERTICAL,
                false
            )
            layoutManager = linearLayout
            adapter = repoGroupAdapter
        }
    }

    private fun showEmptyView(status: Boolean){
        if (isRemoving) return
        if (status) {
            rec_third_ll.gone()
            empty_content.visible()
        } else {
            rec_third_ll.visible()
            empty_content.gone()
        }
    }
}
