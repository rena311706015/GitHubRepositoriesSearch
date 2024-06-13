package repositories.search.view.branch

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import repositories.search.model.Branch

class RepoBranchListAdapter(private val interaction: Interaction) :
    RecyclerView.Adapter<RepoBranchViewHolder>() {
    interface Interaction {
        fun onBranchClicked(branch: Branch)
    }

    var items: List<Branch> = emptyList()
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepoBranchViewHolder {
        return RepoBranchViewHolder.create(parent)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: RepoBranchViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
        holder.itemView.setOnClickListener {
            interaction.onBranchClicked(item)
        }
    }
}