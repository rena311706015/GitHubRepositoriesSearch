package com.example.githubrepositoriessearch.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.githubrepositoriessearch.databinding.ItemBranchBinding
import com.example.githubrepositoriessearch.model.Branch

class BranchesAdapter(private val onClickListener: OnClickListener) :
    ListAdapter<Branch, RecyclerView.ViewHolder>(BrancheDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding =
            ItemBranchBinding.inflate(LayoutInflater.from(parent.context), parent, false)

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

    class ViewHolder constructor(private val binding: ItemBranchBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Branch) {
            binding.branch = item
        }
    }

    override fun getItemCount(): Int {
        return currentList.size
    }

    class OnClickListener(val clickListener: (branch: Branch) -> Unit) {
        fun onClick(branch: Branch) = clickListener(branch)
    }
}

@BindingAdapter("branches")
fun bindRecyclerViewWithDataItemList(
    recyclerView: RecyclerView,
    dataItemList: List<Branch>?
) {
    dataItemList?.let {
        recyclerView.adapter?.apply {
            when (this) {
                is BranchesAdapter -> submitList(it)
            }
        }
    }
}


private class BrancheDiffCallback : DiffUtil.ItemCallback<Branch>() {
    override fun areContentsTheSame(oldItem: Branch, newItem: Branch) =
        oldItem.equals(newItem)

    override fun areItemsTheSame(oldItem: Branch, newItem: Branch) =
        oldItem.name == newItem.name
}
