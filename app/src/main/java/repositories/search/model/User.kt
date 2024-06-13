package repositories.search.model

import android.text.format.DateUtils
import android.util.Log
import java.text.SimpleDateFormat
import java.util.Locale

class User(
    val id: Int = 0,
    val login: String = "", // name
    val avatar_url: String = "",
    val date: String = ""
) {
    fun getAgo(): String {
        Log.e("Rena", "date: $date")
        if (date.isBlank()) return ""

        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        val time: Long = inputFormat.parse(date)?.time ?: 0L
        val now = System.currentTimeMillis()
        val ago = DateUtils.getRelativeTimeSpanString(time, now, DateUtils.MINUTE_IN_MILLIS)
        return ago.toString()
    }
}