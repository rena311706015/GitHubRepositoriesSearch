package repositories.search.view.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import repositories.search.R
import repositories.search.commons.ColorMapper
import repositories.search.commons.ImageLoader
import repositories.search.databinding.ItemRepositoryBinding
import repositories.search.model.Repository

class RepoViewHolder(
    private val binding: ItemRepositoryBinding,
) : RecyclerView.ViewHolder(binding.root) {

    companion object {
        fun create(parent: ViewGroup) = RepoViewHolder(
            ItemRepositoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    fun bind(repo: Repository) {
        binding.apply {
            ImageLoader.Builder(itemView.context, repo.owner.avatar_url).into(ownerAvatar)
            repoFullName.text = repo.full_name
            repoDescription.text = repo.description
            repoStar.text = repo.getStar()
            repoLanguage.text = repo.language
            if (repo.language != null) {
                val drawables = repoLanguage.compoundDrawables
                val drawableWithLanguageColor =
                    ContextCompat.getDrawable(itemView.context, R.drawable.round_circle)?.apply {
                        setTint(ColorMapper.getColorForLanguage(repo.language))
                    }
                repoLanguage.setCompoundDrawablesWithIntrinsicBounds(
                    drawableWithLanguageColor,
                    drawables[1],
                    drawables[2],
                    drawables[3]
                )
            } else repoLanguage.isVisible = false
            repoUpdateDate.text = repo.getUpdateAt()
        }
    }
}