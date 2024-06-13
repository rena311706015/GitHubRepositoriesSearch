package repositories.search.view.branch

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import repositories.search.databinding.ItemBranchBinding
import repositories.search.model.Branch

class RepoBranchViewHolder(
    private val binding: ItemBranchBinding,
) : RecyclerView.ViewHolder(binding.root) {

    companion object {
        fun create(parent: ViewGroup) = RepoBranchViewHolder(
            ItemBranchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    fun bind(branch: Branch) {
        binding.branchName.text = branch.name
    }
}