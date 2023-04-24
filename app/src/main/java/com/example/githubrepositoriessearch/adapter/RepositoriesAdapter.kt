package com.example.githubrepositoriessearch.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.githubrepositoriessearch.databinding.ItemRepositoryBinding
import com.example.githubrepositoriessearch.model.Repository

//用listAdapter搭配DiffUtil
//一開始是因為發現資料沒變，但notifyDataSetChange會叫超過一次造成列表閃爍
class RepositoriesAdapter(private val onClickListener: OnClickListener) :
    ListAdapter<Repository, RecyclerView.ViewHolder>(RepoDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding =
            ItemRepositoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)

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

    class ViewHolder constructor(private val binding: ItemRepositoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Repository) {
            binding.repository = item
        }
    }

    override fun getItemCount(): Int {
        return currentList.size
    }

    class OnClickListener(val clickListener: (repo: Repository) -> Unit) {
        fun onClick(repo: Repository) = clickListener(repo)
    }
}

@BindingAdapter("repositories")
fun bindRecyclerViewWithDataItemList(
    recyclerView: RecyclerView,
    dataItemList: List<Repository>?
) {
    dataItemList?.let {
        recyclerView.adapter?.apply {
            when (this) {
                is RepositoriesAdapter -> submitList(it)
            }
        }
    }
}

private class RepoDiffCallback : DiffUtil.ItemCallback<Repository>() {
    //只會更新兩個列表之間具有不同值的項目
    override fun areContentsTheSame(oldItem: Repository, newItem: Repository) =
        oldItem.equals(newItem)

    //用來判斷item是否被編輯、移動、或刪除
    override fun areItemsTheSame(oldItem: Repository, newItem: Repository) =
        oldItem.id == newItem.id
}

