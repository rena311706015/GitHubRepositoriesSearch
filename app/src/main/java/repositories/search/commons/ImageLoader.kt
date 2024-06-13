package repositories.search.commons

import android.content.Context
import android.widget.ImageView

interface ImageLoader {
    fun load(
        context: Context,
        view: ImageView,
        url: String,
    )

    class Builder(
        private val context: Context,
        private val url: String,
    ) {
        private fun getImageLoader(): ImageLoader {
            return GlideImageLoader
        }

        fun into(view: ImageView) = getImageLoader().load(context, view, url)
    }
}