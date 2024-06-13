package repositories.search.commons

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide

object GlideImageLoader : ImageLoader {
    override fun load(
        context: Context,
        view: ImageView,
        url: String,
    ) {
        Glide.with(context).load(url).into(view)
    }
}