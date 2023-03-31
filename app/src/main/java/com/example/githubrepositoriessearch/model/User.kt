package com.example.githubrepositoriessearch.model

import android.text.format.DateUtils
import java.text.SimpleDateFormat
import java.util.*


class User(
    var id: Int = 0,
    var login: String = "",
    var avatar_url: String = "",
    var date: String = ""
) {
    fun getAgo(): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        val time: Long = inputFormat.parse(date).getTime()
        val now = System.currentTimeMillis()
        val ago = DateUtils.getRelativeTimeSpanString(time, now, DateUtils.MINUTE_IN_MILLIS)
        return ago.toString()
    }
}