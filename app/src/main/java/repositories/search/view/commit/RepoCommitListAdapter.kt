package repositories.search.view.commit

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import repositories.search.model.response.CommitResponse

class RepoCommitListAdapter : RecyclerView.Adapter<RepoCommitViewHolder>() {

    var items: List<CommitResponse> = emptyList()
        set(newItems) {
            val oldItems = field
            field = newItems
            DiffUtil.calculateDiff(object : DiffUtil.Callback() {
                override fun getOldListSize() = oldItems.size
                override fun getNewListSize() = newItems.size
                override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
                    oldItems[oldItemPosition].commit.message == newItems[newItemPosition].commit.message

                override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
                    oldItems[oldItemPosition] == newItems[newItemPosition]
            }).dispatchUpdatesTo(this)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepoCommitViewHolder {
        return RepoCommitViewHolder.create(parent)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: RepoCommitViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }
}