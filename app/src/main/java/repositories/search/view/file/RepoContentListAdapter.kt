package repositories.search.view.file

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import repositories.search.model.Content

class RepoContentListAdapter(private val interaction: Interaction) :
    RecyclerView.Adapter<RepoContentViewHolder>() {
    interface Interaction {
        fun onContentClicked(content: Content)
    }

    var items: List<Content> = emptyList()
        set(newItems) {
            val oldItems = field
            field = newItems
            DiffUtil.calculateDiff(object : DiffUtil.Callback() {
                override fun getOldListSize() = oldItems.size
                override fun getNewListSize() = newItems.size
                override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
                    oldItems[oldItemPosition].name == newItems[newItemPosition].name

                override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
                    oldItems[oldItemPosition] == newItems[newItemPosition]
            }).dispatchUpdatesTo(this)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepoContentViewHolder {
        return RepoContentViewHolder.create(parent)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: RepoContentViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
        holder.itemView.setOnClickListener {
            interaction.onContentClicked(item)
        }
    }
}