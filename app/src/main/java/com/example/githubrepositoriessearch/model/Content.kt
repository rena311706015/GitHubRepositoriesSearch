package com.example.githubrepositoriessearch.model

class Content(
    val name: String,
    val path: String,
    val type: String,
    val download_url: String
) {
    fun isDir(): Boolean {
        return type == "dir"
    }
}