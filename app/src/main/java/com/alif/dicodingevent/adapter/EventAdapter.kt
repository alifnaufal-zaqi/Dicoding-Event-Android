package com.alif.dicodingevent.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.alif.dicodingevent.R
import com.alif.dicodingevent.data.response.ListEventsItem
import com.alif.dicodingevent.databinding.ItemEventBinding
import com.bumptech.glide.Glide

class EventAdapter : ListAdapter<ListEventsItem, EventAdapter.MyViewHolder>(DIFF_CALLBACK) {

    private lateinit var onClickDetailCallback: OnClickDetailCallback

    fun setOnClickDetailCallback(onClickDetailCallback: OnClickDetailCallback) {
        this.onClickDetailCallback = onClickDetailCallback
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val binding = ItemEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: MyViewHolder,
        position: Int
    ) {
        val events = getItem(position)
        holder.bind(events)
    }

    inner class MyViewHolder(val binding: ItemEventBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(events: ListEventsItem) {
            Glide.with(binding.root.context)
                .load(events.mediaCover)
                .into(binding.imgCoverEvent)

            binding.apply {
                chipCategory.text = events.category
                tvEventTitle.text = events.name
                tvSummary.text = events.summary
                tvOwnerName.text = root.context.getString(R.string.event_owner, events.ownerName)

                cardEventItem.setOnClickListener {
                    onClickDetailCallback.onClickDetail(events.id)
                }
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListEventsItem>() {
            override fun areItemsTheSame(
                oldItem: ListEventsItem,
                newItem: ListEventsItem
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: ListEventsItem,
                newItem: ListEventsItem
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

    interface OnClickDetailCallback {
        fun onClickDetail(idEvent: Int)
    }
}