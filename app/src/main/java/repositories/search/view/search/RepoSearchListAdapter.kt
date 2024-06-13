package repositories.search.view.search

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import repositories.search.model.Repository

class RepoSearchListAdapter(private val interaction: Interaction) :
    RecyclerView.Adapter<RepoViewHolder>() {
    interface Interaction {
        fun onRepoClicked(ownerName: String, repoName: String)
    }

    var items: List<Repository> = emptyList()
        set(newItems) {
            val oldItems = field
            field = newItems
            DiffUtil.calculateDiff(object : DiffUtil.Callback() {
                override fun getOldListSize() = oldItems.size
                override fun getNewListSize() = newItems.size
                override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
                    oldItems[oldItemPosition].id == newItems[newItemPosition].id

                override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
                    oldItems[oldItemPosition] == newItems[newItemPosition]
            }).dispatchUpdatesTo(this)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepoViewHolder {
        return RepoViewHolder.create(parent)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: RepoViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
        holder.itemView.setOnClickListener {
            interaction.onRepoClicked(item.owner.login, item.name)
        }
    }
}