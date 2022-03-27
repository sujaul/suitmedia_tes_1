package com.test.test_karim.feature

import android.annotation.SuppressLint
import android.app.Activity
import android.util.DisplayMetrics
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.test.test_karim.R
import com.test.test_karim.data.model.Events
import com.test.test_karim.data.model.Guests
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.item_event.view.*
import kotlinx.android.synthetic.main.item_guests.view.*


class ItemEvent( val event: Events, val viewListener: (Events, Int) -> Unit): Item(){
    @SuppressLint("ResourceAsColor")
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.txt_event_name.text = event.name
        viewHolder.itemView.txt_date.text = event.date

        viewHolder.itemView.setOnClickListener {
            viewListener(event, position)
        }

        val options: RequestOptions = RequestOptions()
            .placeholder(R.drawable.ic_baseline_groups_24)
            .error(R.drawable.ic_baseline_groups_24)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .priority(Priority.HIGH)
        Glide.with(viewHolder.itemView.context)
            .load(event.image)
            .apply(options)
            .into(viewHolder.itemView.img_event)

    }

    override fun getLayout(): Int = R.layout.item_event

}

class ItemGuests(val guest: Guests, val viewListener: (Guests, Int) -> Unit): Item(){
    @SuppressLint("ResourceAsColor")
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.txt_guest_name.text = guest.first_name
        viewHolder.itemView.setOnClickListener {
            viewListener(guest, position)
        }
        val ratio = 0.4f
        val metrics = DisplayMetrics()
        (viewHolder.itemView.context as Activity).windowManager.defaultDisplay.getMetrics(metrics)
        var screenWidth = metrics.widthPixels
        screenWidth = (screenWidth.toFloat()*ratio).toInt()
        viewHolder.itemView.img_guest.getLayoutParams().width = screenWidth
        viewHolder.itemView.img_guest.getLayoutParams().height = screenWidth
        val options: RequestOptions = RequestOptions()
            .placeholder(R.drawable.ic_baseline_groups_24)
            .error(R.drawable.ic_baseline_groups_24)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .priority(Priority.HIGH)
        Glide.with(viewHolder.itemView.context)
            .load(guest.avatar)
            .apply(options)
            .into(viewHolder.itemView.img_guest)
    }

    override fun getLayout(): Int = R.layout.item_guests

}

