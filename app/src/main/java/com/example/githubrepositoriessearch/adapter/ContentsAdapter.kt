package com.example.githubrepositoriessearch.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.githubrepositoriessearch.databinding.ItemContentBinding
import com.example.githubrepositoriessearch.model.Content

class ContentsAdapter(private val onClickListener: OnClickListener) :
    ListAdapter<Content, RecyclerView.ViewHolder>(ContentDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding =
            ItemContentBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolder -> {
                holder.bind(getItem(position))
                holder.itemView.setOnClickListener {
                    onClickListener.onClick(getItem(position))
                }
            }
        }
    }

    class ViewHolder constructor(private val binding: ItemContentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Content) {
            binding.content = item
        }
    }

    override fun getItemCount(): Int {
        return currentList.size
    }

    class OnClickListener(val clickListener: (content: Content) -> Unit) {
        fun onClick(content: Content) = clickListener(content)
    }
}

@BindingAdapter("contents")
fun bindRecyclerViewWithDataItemList(
    recyclerView: RecyclerView,
    dataItemList: List<Content>?
) {
    dataItemList?.let {
        recyclerView.adapter?.apply {
            when (this) {
                is ContentsAdapter -> submitList(it.sortedBy { it.type })
            }
        }
    }
}


private class ContentDiffCallback : DiffUtil.ItemCallback<Content>() {
    override fun areContentsTheSame(oldItem: Content, newItem: Content) =
        oldItem.equals(newItem)

    override fun areItemsTheSame(oldItem: Content, newItem: Content) =
        oldItem.name == newItem.name
}