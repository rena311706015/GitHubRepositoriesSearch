package com.example.githubrepositoriessearch.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.githubrepositoriessearch.databinding.ItemCommitBinding
import com.example.githubrepositoriessearch.model.CommitResult

class CommitsAdapter(private val onClickListener: OnClickListener) :
    ListAdapter<CommitResult, RecyclerView.ViewHolder>(CommitDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding =
            ItemCommitBinding.inflate(LayoutInflater.from(parent.context), parent, false)

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

    class ViewHolder constructor(private val binding: ItemCommitBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CommitResult) {
            binding.commit = item
        }
    }

    override fun getItemCount(): Int {
        return currentList.size
    }

    class OnClickListener(val clickListener: (commit: CommitResult) -> Unit) {
        fun onClick(commit: CommitResult) = clickListener(commit)
    }
}

@BindingAdapter("commits")
fun bindRecyclerViewWithDataItemList(
    recyclerView: RecyclerView,
    dataItemList: List<CommitResult>?
) {
    dataItemList?.let {
        recyclerView.adapter?.apply {
            when (this) {
                is CommitsAdapter -> submitList(it)
            }
        }
    }
}


private class CommitDiffCallback : DiffUtil.ItemCallback<CommitResult>() {
    override fun areContentsTheSame(oldItem: CommitResult, newItem: CommitResult) =
        oldItem.equals(newItem)

    override fun areItemsTheSame(oldItem: CommitResult, newItem: CommitResult) =
        oldItem.commit == newItem.commit
}