package repositories.search.view.commit

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import repositories.search.commons.ImageLoader
import repositories.search.databinding.ItemCommitBinding
import repositories.search.model.response.CommitResponse

class RepoCommitViewHolder(
    private val binding: ItemCommitBinding,
) : RecyclerView.ViewHolder(binding.root) {

    companion object {
        fun create(parent: ViewGroup) = RepoCommitViewHolder(
            ItemCommitBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    fun bind(commitResponse: CommitResponse) {
        binding.apply {
            ImageLoader.Builder(itemView.context, commitResponse.author.avatar_url)
                .into(commitAuthorAvatar)
            commitAuthorName.text = commitResponse.author.login
            commitDateAgo.text = commitResponse.commit.author.getAgo()
            commitMessage.text = commitResponse.commit.message
        }
    }
}