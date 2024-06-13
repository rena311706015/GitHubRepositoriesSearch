package repositories.search.view.file

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import repositories.search.R
import repositories.search.databinding.ItemContentBinding
import repositories.search.model.Content

class RepoContentViewHolder(
    private val binding: ItemContentBinding,
) : RecyclerView.ViewHolder(binding.root) {

    companion object {
        fun create(parent: ViewGroup) = RepoContentViewHolder(
            ItemContentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    fun bind(content: Content) {
        binding.apply {
            contentName.text = content.name
            val imageResource = if (content.isDir()) {
                R.drawable.round_folder
            } else {
                R.drawable.round_file
            }
            contentType.setImageResource(imageResource)
        }
    }
}