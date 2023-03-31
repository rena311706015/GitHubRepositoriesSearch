package com.example.githubrepositoriessearch.model

import android.util.Base64

class Readme(
    var name: String = "",
    private var content: String = "",
) {
    fun getContent(): String {
        return String(Base64.decode(content, Base64.DEFAULT))
    }
}